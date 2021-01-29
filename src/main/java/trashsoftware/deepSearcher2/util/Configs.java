package trashsoftware.deepSearcher2.util;

import javafx.scene.Scene;
import javafx.scene.text.Font;
import org.json.JSONArray;
import org.json.JSONObject;
import trashsoftware.deepSearcher2.guiItems.HistoryItem;
import trashsoftware.deepSearcher2.searcher.PrefSet;
import trashsoftware.deepSearcher2.searcher.SearchDirNotSetException;
import trashsoftware.deepSearcher2.searcher.SearchPrefNotSetException;
import trashsoftware.deepSearcher2.searcher.SearchTargetNotSetException;
import trashsoftware.deepSearcher2.searcher.matchers.MatchMode;

import java.io.*;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Configs {

    private static final String USER_DATA_DIR = "userData";
    private static final String CONFIG_FILE_NAME = USER_DATA_DIR + File.separator + "config.cfg";
    private static final String EXCLUDED_DIRS_NAME = USER_DATA_DIR + File.separator + "excludedDirs.cfg";
    private static final String EXCLUDED_FORMATS_NAME = USER_DATA_DIR + File.separator + "excludedFormats.cfg";
    private static final String CUSTOM_FORMATS_NAME = USER_DATA_DIR + File.separator + "customFormats.cfg";
    private static final String CUSTOM_CSS = USER_DATA_DIR + File.separator + "style.css";
    private static final String HISTORY_DIR = USER_DATA_DIR + File.separator + "history";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd,HH-mm-ss-SSS");

    public static Locale getCurrentLocale() {
        String localeName = getConfig("locale");
        if (localeName == null) {
            return new Locale("zh", "CN");
        } else {
            String[] lanCountry = localeName.split("_");
            return new Locale(lanCountry[0], lanCountry[1]);
        }
    }

    public static void applyCustomFont(Scene scene) {
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

    public static String getCustomFont() {
        return getConfig("font");
    }

    public static int getFontSize(int defaultValue) {
        return getInt("fontSize", defaultValue);
    }

    public static boolean isUseCustomFont() {
        return getBoolean("useCustomFont");
    }

    public static void setUseCustomFont(boolean value, String customFont, int fontSize) {
        writeConfigs(
                "useCustomFont", String.valueOf(value),
                "font", customFont,
                "fontSize", String.valueOf(fontSize));
    }

    /**
     * Sets whether to limit search depth.
     *
     * @param limitDepth whether to limit search depth
     */
    public static void setLimitDepth(boolean limitDepth) {
        writeConfig("limitDepth", String.valueOf(limitDepth));
    }

    public static boolean isLimitDepth() {
        return getBoolean("limitDepth");
    }

    /**
     * @return the max traversal depth
     */
    public static int getMaxSearchDepth() {
        return getInt("maxDepth", 5);
    }

    /**
     * @param searchDepth max traversal depth
     */
    public static void setMaxSearchDepth(int searchDepth) {
        writeConfig("maxDepth", String.valueOf(searchDepth));
    }

    public static boolean isIncludePathName() {
        return getBoolean("includePathName");
    }

    public static void setIncludePathName(boolean value) {
        writeConfig("includePathName", String.valueOf(value));
    }

    public static boolean isShowHidden() {
        return getBoolean("showHidden");
    }

    public static void setShowHidden(boolean value) {
        writeConfig("showHidden", String.valueOf(value));
    }

    public static boolean isDepthFirst() {
        return getBoolean("depthFirst");
    }

    public static void setDepthFirst(boolean value) {
        writeConfig("depthFirst", String.valueOf(value));
    }

    public static String getCurrentSearchingAlgorithm() {
        String savedAlg = Configs.getConfig("alg");
        return Objects.requireNonNullElse(savedAlg, "algNative");
    }

    public static String getCurrentWordSearchingAlgorithm() {
        String savedAlg = Configs.getConfig("wordAlg");
        return Objects.requireNonNullElse(savedAlg, "algNative");
    }

    public static String getCurrentRegexSearchingAlgorithm() {
        String savedAlg = Configs.getConfig("regexAlg");
        return Objects.requireNonNullElse(savedAlg, "algNative");
    }

    public static int getCurrentCpuThreads() {
        String threadLimit = Configs.getConfig("cpuThreads");
        try {
            return Integer.parseInt(threadLimit);
        } catch (NumberFormatException e) {
            return 4;
        }
    }

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

    private static int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(getConfig(key));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static boolean getBoolean(String key) {
        return Boolean.parseBoolean(getConfig(key));
    }

    private static String getConfig(String key) {
        Map<String, String> configs = readConfigFile();
        return configs.get(key);
    }

    /**
     * Write multiple key-value pairs in one time.
     * <p>
     * Length of {@code keyValues} must be even.
     *
     * @param keyValues key-value pairs
     */
    public static void writeConfigs(String... keyValues) {
        Map<String, String> configs = readConfigFile();
        for (int i = 0; i < keyValues.length; i += 2) {
            configs.put(keyValues[i], keyValues[i + 1]);
        }
        writeConfigFile(configs);
    }

    public static void writeConfig(String key, String value) {
        Map<String, String> configs = readConfigFile();
        configs.put(key, value);
        writeConfigFile(configs);
    }

    public static void addExcludedDir(String path) {
        Set<String> set = readListFile(EXCLUDED_DIRS_NAME);
        set.add(path);
        writeListFile(EXCLUDED_DIRS_NAME, set);
    }

    public static void removeExcludedDir(String path) {
        Set<String> set = readListFile(EXCLUDED_DIRS_NAME);
        set.remove(path);
        writeListFile(EXCLUDED_DIRS_NAME, set);
    }

    public static Set<String> getAllExcludedDirs() {
        return readListFile(EXCLUDED_DIRS_NAME);
    }

    public static void addExcludedFormat(String path) {
        Set<String> set = readListFile(EXCLUDED_FORMATS_NAME);
        set.add(path);
        writeListFile(EXCLUDED_FORMATS_NAME, set);
    }

    public static void removeExcludedFormat(String path) {
        Set<String> set = readListFile(EXCLUDED_FORMATS_NAME);
        set.remove(path);
        writeListFile(EXCLUDED_FORMATS_NAME, set);
    }

    public static Set<String> getAllExcludedFormats() {
        return readListFile(EXCLUDED_FORMATS_NAME);
    }

    public static void addCustomFormat(String ext, String description) {
        Map<String, String> map = readMapFile(CUSTOM_FORMATS_NAME);
        map.put(ext, description);
        writeMapFile(CUSTOM_FORMATS_NAME, map);
    }

    public static void removeCustomFormat(String ext) {
        Map<String, String> map = readMapFile(CUSTOM_FORMATS_NAME);
        map.remove(ext);
        writeMapFile(CUSTOM_FORMATS_NAME, map);
    }

    public static Map<String, String> getAllCustomFormats() {
        return readMapFile(CUSTOM_FORMATS_NAME);
    }

    public static List<HistoryItem> getAllHistory() {
        List<HistoryItem> list = new ArrayList<>();
        createDirsIfNotExist();
        File historyDir = new File(HISTORY_DIR);
        for (File his : Objects.requireNonNull(historyDir.listFiles())) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(his));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                Date date = DATE_FORMAT.parse(his.getName());
                JSONObject jsonObject = new JSONObject(builder.toString());
                PrefSet prefSet = toPrefSet(jsonObject);
                HistoryItem historyItem = new HistoryItem(prefSet, date);
                list.add(historyItem);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                //
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return list;
    }

    public static void clearAllHistory() {
        File dir = new File(HISTORY_DIR);
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (!file.delete()) {
                System.err.println("Failed to delete " + file.getAbsolutePath());
            }
        }
    }

    public static void clearSettings() {
        deleteFileByName(CONFIG_FILE_NAME);
    }

    public static void clearAllData() {
        Cache.clearCache();
        clearSettings();
        clearAllHistory();
        deleteFileByName(EXCLUDED_DIRS_NAME);
        deleteFileByName(EXCLUDED_FORMATS_NAME);
        deleteFileByName(CUSTOM_FORMATS_NAME);
    }

    static void deleteFileByName(String path) {
        File file = new File(path);
        if (!file.delete()) {
            System.err.println("Failed to delete '" + path + "'!");
        }
    }

    public static void addHistory(PrefSet historyItem) {
        JSONObject object = toJsonObject(historyItem);

        String fileName = HISTORY_DIR + File.separator + DATE_FORMAT.format(new Date()) + ".json";
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName));
            bufferedWriter.write(object.toString(2));
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
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

    private static PrefSet toPrefSet(JSONObject root) {
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
            throw new RuntimeException("History item cannot be converted");
        }
    }

    private static Map<String, String> readConfigFile() {
        return readMapFile(CONFIG_FILE_NAME);
    }

    private static void writeConfigFile(Map<String, String> map) {
        writeMapFile(CONFIG_FILE_NAME, map);
    }

    private synchronized static Map<String, String> readMapFile(String fileName) {
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
        }
        return map;
    }

    private synchronized static void writeMapFile(String fileName, Map<String, String> map) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            createDirsIfNotExist();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String line = entry.getKey() + "=" + entry.getValue() + '\n';
                bw.write(line);
            }
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized static Set<String> readListFile(String fileName) {
        Set<String> set = new HashSet<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = br.readLine()) != null) {
                set.add(line);
            }
            br.close();
        } catch (FileNotFoundException e) {
            writeListFile(fileName, set);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return set;
    }

    private synchronized static void writeListFile(String fileName, Set<String> set) {
        try {
            createDirsIfNotExist();
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
            for (String s : set) {
                bw.write(s);
                bw.write('\n');
            }
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void createDirsIfNotExist() {
        File cache = new File(Cache.CACHE_DIR);
        if (!cache.exists()) {
            if (!cache.mkdirs()) {
                System.err.println("Cannot create directory 'cache'");
            }
        }
        File userData = new File(USER_DATA_DIR);
        if (!userData.exists()) {
            if (!userData.mkdirs()) {
                System.err.println("Cannot create directory 'userData'");
            }
        }
        File history = new File(HISTORY_DIR);
        if (!history.exists()) {
            if (!history.mkdirs()) {
                System.err.println("Cannot create directory 'userData/history'");
            }
        }
    }
}
