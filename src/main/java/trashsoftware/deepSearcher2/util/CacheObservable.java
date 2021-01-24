package trashsoftware.deepSearcher2.util;

import org.json.JSONObject;

/**
 * An interface of gui classes that periodically saves cache to disk.
 */
public interface CacheObservable {

    /**
     * Saves cache of gui to the saver
     *
     * @param rootObject target json
     */
    void putCache(JSONObject rootObject);

    /**
     * Loads saved cache to the gui
     *
     * @param cache saved cache from last time
     */
    void loadFromCache(Cache cache);
}
