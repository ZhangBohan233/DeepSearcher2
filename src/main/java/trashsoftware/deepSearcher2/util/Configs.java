package trashsoftware.deepSearcher2.util;

import java.io.*;
import java.util.*;

public class Configs {

    private static final String CONFIG_FILE_NAME = "config.ini";

    public static Locale getCurrentLocale() {
        String localeName = getConfig("locale");
        if (localeName == null) {
            return new Locale("zh", "CN");
        } else {
            String[] lanCountry = localeName.split("_");
            return new Locale(lanCountry[0], lanCountry[1]);
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

    public static String getConfig(String key) {
        Map<String, String> configs = readConfigFile();
        return configs.get(key);
    }

    public static void writeConfig(String key, String value) {
        Map<String, String> configs = readConfigFile();
        configs.put(key, value);
        writeConfigFile(configs);
    }

    private static Map<String, String> readConfigFile() {
        Map<String, String> map = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(CONFIG_FILE_NAME));
            String line;
            while ((line = br.readLine()) != null) {
                String[] keyValue = line.split("=");
                map.put(keyValue[0], keyValue[1]);
            }
            br.close();
        } catch (FileNotFoundException e) {
            writeConfigFile(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    private static void writeConfigFile(Map<String, String> map) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(CONFIG_FILE_NAME));
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
}
