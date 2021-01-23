package trashsoftware.deepSearcher2.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Cache {

    public static final String FORMATS_KEY = "formats";
    public static final String OPENED_DIRS_KEY = "openedDirs";
    static final String CACHE_DIR = "cache";
    private static final String COMMON_CACHE_NAME = CACHE_DIR + File.separator + "cache.json";
    private static final long AUTO_SAVE_INTERVAL = 3000;

    private static Cache activeCache;
    private final Timer autoSave;
    private final List<CacheObservable> cacheObservables = new ArrayList<>();
    private JSONObject root;

    private Cache(List<CacheObservable> cacheObservables) {
        loadFromDisk();

        autoSave = new Timer();
        autoSave.schedule(new AutoSaveTask(), AUTO_SAVE_INTERVAL, AUTO_SAVE_INTERVAL);

        this.cacheObservables.addAll(cacheObservables);
    }

    public static Cache getCache() {
        return activeCache;
    }

    public static void startCache(List<CacheObservable> cacheObservables) {
        if (activeCache != null) {
            activeCache.stop();
        }
        activeCache = new Cache(cacheObservables);
    }

    public static void stopCache() {
        if (activeCache != null) {
            activeCache.stop();
        }
    }

    public static void clearCache() {
        List<CacheObservable> cos = activeCache.cacheObservables;
        activeCache.stop();
        Configs.deleteFileByName(COMMON_CACHE_NAME);
        activeCache = new Cache(cos);
    }

    public void stop() {
        saveToDisk();
        autoSave.cancel();
    }

    public void addObservable(CacheObservable cacheObservable) {
        cacheObservables.add(cacheObservable);
    }

    private void saveToDisk() {
        for (CacheObservable co : cacheObservables) {
            co.putCache(root);
        }
        Configs.createDirsIfNotExist();
        String s = root.toString(2);
        FileWriter fw = null;
        try {
            fw = new FileWriter(COMMON_CACHE_NAME);
            fw.write(s);
            fw.flush();
        } catch (IOException e) {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }

    private void loadFromDisk() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(COMMON_CACHE_NAME));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            root = new JSONObject(builder.toString());
        } catch (FileNotFoundException e) {
            root = new JSONObject();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getStringCache(String key) {
        if (root.has(key)) {
            try {
                return root.getString(key);
            } catch (JSONException e) {
                //
            }
        }
        return null;
    }

    public boolean getBooleanCache(String key, boolean defaultValue) {
        if (root.has(key)) {
            try {
                return root.getBoolean(key);
            } catch (JSONException e) {
                //
            }
        }
        return defaultValue;
    }

    public JSONArray getArrayCache(String key) {
        if (root.has(key)) {
            try {
                return root.getJSONArray(key);
            } catch (JSONException e) {
                //
            }
        }
        JSONArray array = new JSONArray();
        root.put(key, array);
        return array;
    }

    private class AutoSaveTask extends TimerTask {

        @Override
        public void run() {
            saveToDisk();
        }
    }
}
