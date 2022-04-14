package trashsoftware.deepSearcher2.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * This class loads and saves user's cache to the disk periodically.
 */
public class Cache {

    public static final String FORMATS_KEY = "formats";
    public static final String OPENED_DIRS_KEY = "openedDirs";
    public static final String CACHE_DIR = "cache";
    private static final String COMMON_CACHE_NAME = CACHE_DIR + File.separator + "cache.json";
    private static final String SEARCH_PROMPTS_NAME = CACHE_DIR + File.separator + "prompts.json";
    /**
     * Time interval in mills between two save tasks that save cache in ram to disk
     */
    private static final long AUTO_SAVE_INTERVAL = 3000;

    private static Cache activeCache;
    private final Timer autoSave;
    private final List<CacheObservable> cacheObservables = new ArrayList<>();
    private JSONObject root;
    private Deque<String> promptsQueue;

    private Cache(List<CacheObservable> cacheObservables) {
        loadFromDisk();

        autoSave = new Timer();
        autoSave.schedule(new AutoSaveTask(), AUTO_SAVE_INTERVAL, AUTO_SAVE_INTERVAL);

        this.cacheObservables.addAll(cacheObservables);
    }

    /**
     * Gets the active cache loader.
     * <p>
     * The active cache loader is guaranteed unique.
     *
     * @return the current active cache loader
     */
    public static Cache getCache() {
        return activeCache;
    }

    /**
     * Starts a new {@code Cache} and sets it as active.
     * <p>
     * This method stops the previous active cache loader, if there was.
     *
     * @param cacheObservables the gui pages that supports cache.
     */
    public static void startCache(List<CacheObservable> cacheObservables) {
        if (activeCache != null) {
            activeCache.stop();
        }
        activeCache = new Cache(cacheObservables);
    }

    public static void startCache() {
        startCache(List.of());
    }

    /**
     * Terminates the current active cache loader.
     */
    public static void stopCache() {
        if (activeCache != null) {
            activeCache.stop();
            activeCache = null;
        }
    }

    /**
     * Clear cache stored on the disk.
     */
    public static void clearCache() {
        List<CacheObservable> cos = activeCache.cacheObservables;
        activeCache.stop();
        Configs.deleteFileByName(COMMON_CACHE_NAME);
        Configs.deleteFileByName(SEARCH_PROMPTS_NAME);
        activeCache = new Cache(cos);
        for (CacheObservable co : cos) {
            co.loadFromCache(activeCache);
        }
    }

    /**
     * Terminates this cache loader and stores all cache in ram to disk.
     */
    public void stop() {
        autoSave.cancel();
        saveToDisk();
    }

    /**
     * Adds a new gui page that supports cache.
     *
     * @param cacheObservable the gui page that supports cache to be added
     */
    public void addObservable(CacheObservable cacheObservable) {
        cacheObservables.add(cacheObservable);
    }

    private synchronized void saveToDisk() {
        for (CacheObservable co : cacheObservables) {
            co.putCache(root);
        }
        Configs.createDirsIfNotExist();
        String cache = root.toString(2);
        try (FileWriter fw = new FileWriter(COMMON_CACHE_NAME, StandardCharsets.UTF_8)) {
            fw.write(cache);
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject promptObj = new JSONObject();
        JSONArray promptArray = new JSONArray();
        for (String p : promptsQueue) {
            promptArray.put(p);
        }
        promptObj.put("prompts", promptArray);
        String prompts = promptObj.toString(2);
        try (FileWriter fw = new FileWriter(SEARCH_PROMPTS_NAME, StandardCharsets.UTF_8)) {
            fw.write(prompts);
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFromDisk() {
        try (BufferedReader br = new BufferedReader(
                new FileReader(COMMON_CACHE_NAME, StandardCharsets.UTF_8))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            root = new JSONObject(builder.toString());
        } catch (FileNotFoundException | JSONException e) {
            root = new JSONObject();
        } catch (IOException e) {
            e.printStackTrace();
            root = new JSONObject();
        }

        promptsQueue = new ArrayDeque<>();
        try (BufferedReader br = new BufferedReader(
                new FileReader(SEARCH_PROMPTS_NAME, StandardCharsets.UTF_8))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            JSONObject obj = new JSONObject(builder.toString());
            int limit = Configs.getConfigs().getMaxSearchPrompt();
            for (Object raw : obj.getJSONArray("prompts")) {
                if (raw instanceof String) {
                    addSearchItemPrompt((String) raw, limit);
                }
            }
        } catch (FileNotFoundException | JSONException e) {
            // do nothing
        } catch (IOException e) {
            e.printStackTrace();
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
    
    public void addSearchItemPrompt(String searchItem) {
        addSearchItemPrompt(searchItem, Configs.getConfigs().getMaxSearchPrompt());
    }
    
    private void addSearchItemPrompt(String searchItem, int limit) {
        promptsQueue.removeIf(next -> Objects.equals(next, searchItem));
        promptsQueue.addFirst(searchItem);

        while (promptsQueue.size() > limit) promptsQueue.removeLast();
    }
    
    public Deque<String> getSearchPrompts() {
        return promptsQueue;
    }

    private class AutoSaveTask extends TimerTask {

        @Override
        public void run() {
            saveToDisk();
        }
    }
}
