package trashsoftware.deepSearcher2.searcher;

/**
 * Indicates no searching directory is selected.
 */
public class SearchDirNotSetException extends Exception {

    SearchDirNotSetException(String message) {
        super(message);
    }
}
