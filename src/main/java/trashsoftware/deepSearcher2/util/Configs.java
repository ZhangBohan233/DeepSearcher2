package trashsoftware.deepSearcher2.util;

import javafx.scene.Scene;
import javafx.scene.text.Font;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import trashsoftware.deepSearcher2.guiItems.HistoryItem;
import trashsoftware.deepSearcher2.searcher.*;
import trashsoftware.deepSearcher2.searcher.matchers.MatchMode;

import java.io.*;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.CRC32;

public class Configs {

    private static final String USER_DATA_DIR = "userData";
    private static final String CONFIG_FILE_NAME = USER_DATA_DIR + File.separator + "config.cfg";
    private static final String EXCLUDED_DIRS_NAME = USER_DATA_DIR + File.separator + "excludedDirs.cfg";
    private static final String EXCLUDED_FORMATS_NAME = USER_DATA_DIR + File.separator + "excludedFormats.cfg";
    private static final String CUSTOM_FORMATS_NAME = USER_DATA_DIR + File.separator + "customFormats.cfg";
    private static final String CUSTOM_CSS = USER_DATA_DIR + File.separator + "style.css";
    private static final String HISTORY_DIR = USER_DATA_DIR + File.separator + "history";
    /**
     * Time interval in mills between two save tasks that save changed configs in ram to disk
     */
    private static final long AUTO_SAVE_INTERVAL = 5000;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd,HH-mm-ss-SSS");
    private static Configs activeConfig;
    private final Timer autoSave;
    private Map<String, String> configMap;
    private Set<String> excludedDirs;
    private Set<String> excludedFmts;
    private Map<String, String> customFmts;
    private long excludedDirsChecksum;
    private long excludedFmtsChecksum;
    private long customFmtsChecksum;

    private Configs() {
        loadAll();

        autoSave = new Timer();
        autoSave.schedule(new AutoSaveTask(), AUTO_SAVE_INTERVAL, AUTO_SAVE_INTERVAL);
    }

    /**
     * Starts running a config loader and terminate the previous one, if existed.
     */
    public static void startConfig() {
        if (activeConfig != null) {
            activeConfig.stop();
        }
        activeConfig = new Configs();
    }

    /**
     * Terminates the current running config loader.
     * <p>
     * This static method should be called before the program exits. Otherwise, a background saver might keep running.
     */
    public static void stopConfig() {
        if (activeConfig != null) {
            activeConfig.stop();
            activeConfig = null;
        }
    }

    /**
     * @return the current running config loader
     */
    public static Configs getConfigs() {
        return activeConfig;
    }

    /**
     * Returns a list containing all supported locales.
     *
     * @return a list containing all supported locales
     */
    public static List<NamedLocale> getAllLocales() {
        List<NamedLocale> locales = new ArrayList<>();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("trashsoftware.deepSearcher2.bundles.Languages");
        Enumeration<String> keys = resourceBundle.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            String[] lanLocale = key.split("_");
            NamedLocale namedLocale = new NamedLocale(lanLocale[0], lanLocale[1], resourceBundle.getString(key));
            locales.add(namedLocale);
        }
        return locales;
    }

    /**
     * Returns a list of history records, ranked from newest to oldest.
     *
     * @return a list of histories, ranked from newest to oldest
     */
    public static List<HistoryItem> getAllHistory() {
        List<HistoryItem> list = new ArrayList<>();
        createDirsIfNotExist();
        File historyDir = new File(HISTORY_DIR);
        for (File his : Objects.requireNonNull(historyDir.listFiles())) {
            try (BufferedReader reader = new BufferedReader(new FileReader(his))) {
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                Date date = DATE_FORMAT.parse(his.getName());
                JSONObject jsonObject = new JSONObject(builder.toString());
                PrefSet prefSet = toPrefSet(jsonObject, his.getName());
                if (prefSet == null) continue;
                HistoryItem historyItem = new HistoryItem(prefSet, date);
                list.add(historyItem);
            } catch (IOException e) {
                e.printStackTrace();
                EventLogger.log(e);
            } catch (JSONException | ParseException e) {
                //
            }
        }
        Collections.reverse(list);
        return list;
    }

    public static void clearAllHistory() {
        File dir = new File(HISTORY_DIR);
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (!file.delete()) {
                System.err.println("Failed to delete " + file.getAbsolutePath());
                EventLogger.log("Failed to delete " + file.getAbsolutePath());
            }
        }
    }

    static void deleteFileByName(String path) {
        File file = new File(path);
        if (!file.delete()) {
            System.err.println("Failed to delete '" + path + "'!");
            EventLogger.log("Failed to delete '" + path + "'!");
        }
    }

    public static void addHistory(PrefSet historyItem) {
        JSONObject object = toJsonObject(historyItem);

        String fileName = HISTORY_DIR + File.separator + DATE_FORMAT.format(new Date()) + ".json";
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName))) {
            bufferedWriter.write(object.toString(2));
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            EventLogger.log(e);
        }
    }

    private static JSONObject toJsonObject(PrefSet historyItem) {
        JSONObject root = new JSONObject();
        root.put("searchFileName", historyItem.isFileName());
        root.put("includePathName", historyItem.isIncludePathName());
        root.put("matchCase", historyItem.isCaseSensitive());
        root.put("matchMode", historyItem.getMatchMode().name());
        root.put("searchContent", historyItem.getExtensions() != null);
        root.put("searchDirName", historyItem.isDirName());
        root.put("matchAll", historyItem.isMatchAll());
        JSONArray dirs = new JSONArray(historyItem.getSearchDirs());
        JSONArray patterns = new JSONArray(historyItem.getTargets());
        JSONArray extensions = new JSONArray(historyItem.getExtensions());
        root.put("dirs", dirs);
        root.put("patterns", patterns);
        root.put("extensions", extensions);
        return root;
    }

    private static PrefSet toPrefSet(JSONObject root, String fileName) {
        List<File> dirs = new ArrayList<>();
        for (Object s : root.getJSONArray("dirs")) dirs.add(new File((String) s));
        List<String> patterns = new ArrayList<>();
        for (Object p : root.getJSONArray("patterns")) patterns.add((String) p);
        Set<String> extensions = new HashSet<>();
        for (Object e : root.getJSONArray("extensions")) extensions.add((String) e);
        try {
            return new PrefSet.PrefSetBuilder()
                    .searchFileName(root.getBoolean("searchFileName"))
                    .caseSensitive(root.getBoolean("matchCase"))
                    .directSetMatchMode(MatchMode.valueOf(root.getString("matchMode")))
                    .searchDirName(root.getBoolean("searchDirName"))
                    .setMatchAll(root.getBoolean("matchAll"))
                    .setSearchDirs(dirs)
                    .setTargets(patterns)
                    .setExtensions(root.getBoolean("searchContent") ? extensions : null)
                    .build();
        } catch (SearchTargetNotSetException | SearchDirNotSetException | SearchPrefNotSetException e) {
            // This would never happen
            System.err.println("Failed to load " + fileName);
            return null;
        }
    }

    private static Map<String, String> readMapFile(String fileName) {
        Map<String, String> map = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] keyValue = line.split("=");
                if (keyValue.length == 1) {
                    map.put(keyValue[0], "");
                } else if (keyValue.length == 2) {
                    map.put(keyValue[0], keyValue[1]);
                }
            }
        } catch (FileNotFoundException e) {
            writeMapFile(fileName, map);
        } catch (IOException e) {
            e.printStackTrace();
            EventLogger.log(e);
        }
        return map;
    }

    private static void writeMapFile(String fileName, Map<String, String> map) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            createDirsIfNotExist();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String line = entry.getKey() + "=" + entry.getValue() + '\n';
                bw.write(line);
            }
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
            EventLogger.log(e);
        }
    }

    private static Set<String> readListFile(String fileName) {
        Set<String> set = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                set.add(line);
            }
        } catch (FileNotFoundException e) {
            writeListFile(fileName, set);
        } catch (IOException e) {
            e.printStackTrace();
            EventLogger.log(e);
        }
        return set;
    }

    private static void writeListFile(String fileName, Collection<String> set) {
        createDirsIfNotExist();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (String s : set) {
                bw.write(s);
                bw.write('\n');
            }
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
            EventLogger.log(e);
        }
    }

    static void createDirsIfNotExist() {
        File cache = new File(Cache.CACHE_DIR);
        if (!cache.exists()) {
            if (!cache.mkdirs()) {
                System.err.println("Cannot create directory 'cache'");
                EventLogger.log("Cannot create directory 'cache'");
            }
        }
        File userData = new File(USER_DATA_DIR);
        if (!userData.exists()) {
            if (!userData.mkdirs()) {
                System.err.println("Cannot create directory 'userData'");
                EventLogger.log("Cannot create directory 'userData'");
            }
        }
        File history = new File(HISTORY_DIR);
        if (!history.exists()) {
            if (!history.mkdirs()) {
                System.err.println("Cannot create directory 'userData/history'");
                EventLogger.log("Cannot create directory 'userData'");
            }
        }
    }

    /**
     * Returns the current using locale.
     *
     * @return the current using locale
     */
    public Locale getCurrentLocale() {
        String localeName = getConfig("locale");
        if (localeName == null) {
            return new Locale("zh", "CN");
        } else {
            String[] lanCountry = localeName.split("_");
            return new Locale(lanCountry[0], lanCountry[1]);
        }
    }

    public void stop() {
        autoSave.cancel();
        saveToDisk();
    }

    public void applyCustomFont(Scene scene) {
        String fontFamily = getCustomFont();
        if (fontFamily == null) fontFamily = Font.getDefault().getFamily();
        int fontSize = getFontSize(12);
        String content = String.format(
                ".root {\n    -fx-font-family: %s;\n    -fx-font-size: %dpx;\n}",
                fontFamily,
                fontSize);
        try (FileWriter fw = new FileWriter(CUSTOM_CSS)) {
            fw.write(content);
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
            EventLogger.log(e);
        }
        try {
            scene.getStylesheets().add(new File(CUSTOM_CSS).toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            EventLogger.log(e);
        }
    }

    public String getCustomFont() {
        return getConfig("font");
    }

    public int getFontSize(int defaultValue) {
        return getInt("fontSize", defaultValue);
    }

    public boolean isUseCustomFont() {
        return getBoolean("useCustomFont");
    }

    public void setUseCustomFont(boolean value, String customFont, int fontSize) {
        writeConfigs(
                "useCustomFont", String.valueOf(value),
                "font", customFont,
                "fontSize", String.valueOf(fontSize));
    }

    public boolean isLimitDepth() {
        return getBoolean("limitDepth");
    }

    /**
     * Sets whether to limit search depth.
     *
     * @param limitDepth whether to limit search depth
     */
    public void setLimitDepth(boolean limitDepth) {
        writeConfig("limitDepth", String.valueOf(limitDepth));
    }

    /**
     * @return the max traversal depth
     */
    public int getMaxSearchDepth() {
        return getInt("maxDepth", 5);
    }

    /**
     * @param searchDepth max traversal depth
     */
    public void setMaxSearchDepth(int searchDepth) {
        writeConfig("maxDepth", String.valueOf(searchDepth));
    }

    public boolean isIncludePathName() {
        return getBoolean("includePathName");
    }

    public void setIncludePathName(boolean value) {
        writeConfig("includePathName", String.valueOf(value));
    }

    public boolean isShowHidden() {
        return getBoolean("showHidden");
    }

    public void setShowHidden(boolean value) {
        writeConfig("showHidden", String.valueOf(value));
    }

    public boolean isDepthFirst() {
        return getBoolean("depthFirst");
    }

    public void setDepthFirst(boolean value) {
        writeConfig("depthFirst", String.valueOf(value));
    }

    public Algorithm.Regular getCurrentSearchingAlgorithm() {
        String savedAlg = getConfig("alg");
        try {
            return Algorithm.Regular.valueOf(savedAlg);
        } catch (NullPointerException | IllegalArgumentException e) {
            return Algorithm.Regular.AUTO;
        }
    }

    public Algorithm.Word getCurrentWordSearchingAlgorithm() {
        String savedAlg = getConfig("wordAlg");
        try {
            return Algorithm.Word.valueOf(savedAlg);
        } catch (NullPointerException | IllegalArgumentException e) {
            return Algorithm.Word.NAIVE;
        }
    }

    public Algorithm.Regex getCurrentRegexSearchingAlgorithm() {
        String savedAlg = getConfig("regexAlg");
        try {
            return Algorithm.Regex.valueOf(savedAlg);
        } catch (NullPointerException | IllegalArgumentException e) {
            return Algorithm.Regex.NATIVE;
        }
    }

    public int getCurrentCpuThreads() {
        return getInt("cpuThreads", 4);
    }

    private int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(getConfig(key));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private boolean getBoolean(String key) {
        return Boolean.parseBoolean(getConfig(key));
    }

    private String getConfig(String key) {
        return configMap.get(key);
    }

    /**
     * Write multiple key-value pairs in one time.
     * <p>
     * Length of {@code keyValues} must be even.
     *
     * @param keyValues key-value pairs
     */
    public void writeConfigs(String... keyValues) {
        for (int i = 0; i < keyValues.length; i += 2) {
            configMap.put(keyValues[i], keyValues[i + 1]);
        }
    }

    public void writeConfig(String key, String value) {
        configMap.put(key, value);
    }

    public void addExcludedDir(String path) {
        excludedDirs.add(path);
    }

    public void removeExcludedDir(String path) {
        excludedDirs.remove(path);
    }

    public Set<String> getAllExcludedDirs() {
        return excludedDirs;
    }

    public void addExcludedFormat(String path) {
        excludedFmts.add(path);
    }

    public void removeExcludedFormat(String path) {
        excludedFmts.remove(path);
    }

    public Set<String> getAllExcludedFormats() {
        return excludedFmts;
    }

    public void addCustomFormat(String ext, String description) {
        customFmts.put(ext, description);
    }

    public void removeCustomFormat(String ext) {
        customFmts.remove(ext);
    }

    public Map<String, String> getAllCustomFormats() {
        return customFmts;
    }

    /**
     * Clears all user settings, but does not delete data.
     */
    public void clearSettings() {
        configMap.clear();
        deleteFileByName(CONFIG_FILE_NAME);
    }

    /**
     * Clears all user settings and data.
     */
    public void clearAllData() {
        Cache.clearCache();
        clearSettings();
        clearAllHistory();
        deleteFileByName(EXCLUDED_DIRS_NAME);
        deleteFileByName(EXCLUDED_FORMATS_NAME);
        deleteFileByName(CUSTOM_FORMATS_NAME);
    }

    /**
     * Computes the crc32 checksum of a collection of strings.
     * <p>
     * This method returns the same result regardless the order in collection.
     *
     * @param collection the collection of strings to be compute
     * @return the crc32 checksum
     */
    private long computeChecksum(Collection<String> collection) {
        List<String> sorted = new ArrayList<>(collection);
        Collections.sort(sorted);
        CRC32 crc32 = new CRC32();
        for (String s : sorted) {
            crc32.update(s.getBytes());
        }
        return crc32.getValue();
    }

    private void loadAll() {
        configMap = readMapFile(CONFIG_FILE_NAME);
        excludedDirs = readListFile(EXCLUDED_DIRS_NAME);
        excludedFmts = readListFile(EXCLUDED_FORMATS_NAME);
        customFmts = readMapFile(CUSTOM_FORMATS_NAME);
        excludedDirsChecksum = computeChecksum(excludedDirs);
        excludedFmtsChecksum = computeChecksum(excludedFmts);
        customFmtsChecksum = computeChecksum(customFmts.keySet());
    }

    private void saveToDisk() {
        long excDirsCs = computeChecksum(excludedDirs);
        long excFmtsCs = computeChecksum(excludedFmts);
        long cusFmtsCs = computeChecksum(customFmts.keySet());

        writeMapFile(CONFIG_FILE_NAME, configMap);

        if (excDirsCs != excludedDirsChecksum) {
            excludedDirsChecksum = excDirsCs;
            writeListFile(EXCLUDED_DIRS_NAME, excludedDirs);
        }
        if (excFmtsCs != excludedFmtsChecksum) {
            excludedFmtsChecksum = excFmtsCs;
            writeListFile(EXCLUDED_FORMATS_NAME, excludedFmts);
        }
        if (cusFmtsCs != customFmtsChecksum) {
            customFmtsChecksum = cusFmtsCs;
            writeMapFile(CUSTOM_FORMATS_NAME, customFmts);
        }
    }

    private class AutoSaveTask extends TimerTask {

        @Override
        public void run() {
            saveToDisk();
        }
    }
}
