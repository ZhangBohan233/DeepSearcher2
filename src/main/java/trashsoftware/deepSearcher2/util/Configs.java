package trashsoftware.deepSearcher2.util;

import trashsoftware.deepSearcher2.items.HistoryItem;
import trashsoftware.deepSearcher2.searcher.PrefSet;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.json.*;
import trashsoftware.deepSearcher2.searcher.SearchDirNotSetException;
import trashsoftware.deepSearcher2.searcher.SearchTargetNotSetException;

public class Configs {

    public static final String FORMATS_KEY = "formats";
    public static final String OPENED_DIRS_KEY = "openedDirs";

    private static final String USER_DATA_DIR = "userData";
    private static final String CONFIG_FILE_NAME = "userData/config.cfg";
    private static final String EXCLUDED_DIRS_NAME = "userData/excludedDirs.cfg";
    private static final String EXCLUDED_FORMATS_NAME = "userData/excludedFormats.cfg";
    private static final String HISTORY_DIR = "userData/history";

    private static final String CACHE_DIR = "cache";
    private static final String COMMON_CACHE_NAME = CACHE_DIR + File.separator + "cache.json";
//    private static final String PAIRED_CACHE_NAME = "cache/pairs.cfg";
//    private static final String FORMAT_FILE_NAME = "cache/formats.cfg";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd,HH-mm-ss-SSS");

    private static JSONObject cache;

    public static Locale getCurrentLocale() {
        String localeName = getConfig("locale");
        if (localeName == null) {
            return new Locale("zh", "CN");
        } else {
            String[] lanCountry = localeName.split("_");
            return new Locale(lanCountry[0], lanCountry[1]);
        }
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

    private static String getConfig(String key) {
        Map<String, String> configs = readConfigFile();
        return configs.get(key);
    }

    public static void writeConfig(String key, String value) {
        Map<String, String> configs = readConfigFile();
        configs.put(key, value);
        writeConfigFile(configs);
    }

    public static String getStringCache(String key) {
        if (cache == null) loadCache();
        if (cache.has(key))
            return cache.getString(key);
        else return null;
    }

    public static void writeStringCache(String key, String value) {
        cache.put(key, value);
        saveCache();
    }

    public static JSONArray getArrayCache(String key) {
        if (cache == null) loadCache();
        if (cache.has(key))
            return cache.getJSONArray(key);
        else {
            JSONArray array = new JSONArray();
            cache.put(key, array);
            return array;
        }
    }

    public static void addToArrayCache(String key, String value) {
        JSONArray array = getArrayCache(key);
        array.put(value);
        saveCache();
    }

    /**
     * Adds a new element to {@code JSONArray} at toplevel, if the element is not currently in this array.
     *
     * @param key   the name of this {@code JSONArray} in the toplevel {@code JSONObject}
     * @param value the element to be added to this {@code JSONArray}
     */
    public static void addToArrayCacheNoDup(String key, String value) {
        JSONArray array = getArrayCache(key);
        for (Object obj : array) {
            if (value.equals(obj)) return;
        }
        array.put(value);
        saveCache();
    }

//    public static void addFormat(String format) {
//        addToArrayCache("formats", format);
//    }

    public static void removeFromArrayCache(String key, String item) {
        JSONArray set = getArrayCache(key);
        for (int i = 0; i < set.length(); ++i) {
            if (item.equals(set.get(i))) {
                set.remove(i);
                break;
            }
        }
        saveCache();
    }

    public static Set<String> getAllFormats() {
        Set<String> list = new HashSet<>();
        for (Object fmt : getFormats()) {
            list.add((String) fmt);
        }
        return list;
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

    public static List<HistoryItem> getAllHistory() {
        List<HistoryItem> list = new ArrayList<>();
        try {
            createDirsIfNotExist();
            File historyDir = new File(HISTORY_DIR);
            for (File his : Objects.requireNonNull(historyDir.listFiles())) {
                BufferedReader reader = new BufferedReader(new FileReader(his));
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
                reader.close();
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
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

    public static void addHistory(PrefSet historyItem) {
        JSONObject object = toJsonObject(historyItem);

        String fileName = HISTORY_DIR + File.separator + DATE_FORMAT.format(new Date()) + ".json";
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName));
            bufferedWriter.write(object.toString());
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
        root.put("matchCase", historyItem.isMatchCase());
        root.put("matchMode", historyItem.getMatchMode());
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
                    .includePathName(root.getBoolean("includePathName"))
                    .matchCase(root.getBoolean("matchCase"))
                    .directSetMatchMode(root.getInt("matchMode"))
                    .searchDirName(root.getBoolean("searchDirName"))
                    .setMatchAll(root.getBoolean("matchAll"))
                    .setSearchDirs(dirs)
                    .setTargets(patterns)
                    .setExtensions(root.getBoolean("searchContent") ? extensions : null)
                    .build();
        } catch (SearchTargetNotSetException | SearchDirNotSetException e) {
            // This would never happen
            throw new RuntimeException("History item cannot be converted");
        }
    }

    private static Map<String, String> readConfigFile() {
        return readMapFile(CONFIG_FILE_NAME);
    }

//    private static void writePairedCache(Map<String, String> map) {
//        writeMapFile(PAIRED_CACHE_NAME, map);
//    }

    private static void writeConfigFile(Map<String, String> map) {
        writeMapFile(CONFIG_FILE_NAME, map);
    }

    private static void loadCache() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(COMMON_CACHE_NAME));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            br.close();
            cache = new JSONObject(builder.toString());
        } catch (FileNotFoundException e) {
            cache = new JSONObject();
            saveCache();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveCache() {
        try {
            createDirsIfNotExist();
            String s = cache.toString(2);
            FileWriter fw = new FileWriter(COMMON_CACHE_NAME);
            fw.write(s);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, String> readMapFile(String fileName) {
        Map<String, String> map = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = br.readLine()) != null) {
                String[] keyValue = line.split("=");
                map.put(keyValue[0], keyValue[1]);
            }
            br.close();
        } catch (FileNotFoundException e) {
            writeMapFile(fileName, map);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    private static void writeMapFile(String fileName, Map<String, String> map) {
        try {
            createDirsIfNotExist();
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String line = entry.getKey() + "=" + entry.getValue() + '\n';
                bw.write(line);
            }
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Set<String> readListFile(String fileName) {
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

    private static void writeListFile(String fileName, Set<String> set) {
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

    private static JSONArray getFormats() {
        return getArrayCache(FORMATS_KEY);
    }

    private static void createDirsIfNotExist() throws IOException {
        File cache = new File(CACHE_DIR);
        if (!cache.exists()) {
            if (!cache.mkdirs()) {
                throw new IOException("Cannot create directory 'cache'");
            }
        }
        File userData = new File(USER_DATA_DIR);
        if (!userData.exists()) {
            if (!userData.mkdirs()) {
                throw new IOException("Cannot create directory 'userData'");
            }
        }
        File history = new File(HISTORY_DIR);
        if (!history.exists()) {
            if (!history.mkdirs()) {
                throw new IOException("Cannot create directory 'userData/history'");
            }
        }
    }
}
