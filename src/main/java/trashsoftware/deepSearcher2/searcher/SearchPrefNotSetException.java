package trashsoftware.deepSearcher2.searcher;

/**
 * Indicator of no pref checkboxes are checked.
 */
public class SearchPrefNotSetException extends Exception {

    SearchPrefNotSetException(String msg) {
        super(msg);
    }
}
