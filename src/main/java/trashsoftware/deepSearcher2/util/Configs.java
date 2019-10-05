package trashsoftware.deepSearcher2.util;

import java.io.*;
import java.net.FileNameMap;
import java.util.*;

public class Configs {

    private static final String USER_DATA_DIR = "userData";
    private static final String CONFIG_FILE_NAME = "userData/config.cfg";
    private static final String EXCLUDED_DIRS_NAME = "userData/excludedDirs.cfg";
    private static final String EXCLUDED_FORMATS_NAME = "userData/excludedFormats.cfg";
    private static final String CACHE_DIR = "cache";
    private static final String PAIRED_CACHE_NAME = "cache/pairs.cfg";
    private static final String FORMAT_FILE_NAME = "cache/formats.cfg";

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

    public static String getPairedCache(String key) {
        Map<String, String> cache = readMapFile(PAIRED_CACHE_NAME);
        return cache.get(key);
    }

    public static void writePairedCache(String key, String value) {
        Map<String, String> cache = readMapFile(PAIRED_CACHE_NAME);
        cache.put(key, value);
        writePairedCache(cache);
    }

    public static void addFormat(String format) {
        Set<String> set = readFormatFile();
        set.add(format);
        writeFormatFile(set);
    }

    public static void removeFormat(String format) {
        Set<String> set = readFormatFile();
        set.remove(format);
        writeFormatFile(set);
    }

    public static Set<String> getAllFormats() {
        return readFormatFile();
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

    private static Map<String, String> readConfigFile() {
        return readMapFile(CONFIG_FILE_NAME);
    }

    private static void writePairedCache(Map<String, String> map) {
        writeMapFile(PAIRED_CACHE_NAME, map);
    }

    private static void writeConfigFile(Map<String, String> map) {
        writeMapFile(CONFIG_FILE_NAME, map);
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

    private static Set<String> readFormatFile() {
        return readListFile(FORMAT_FILE_NAME);
    }

    private static void writeFormatFile(Set<String> formats) {
        writeListFile(FORMAT_FILE_NAME, formats);
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
    }
}
