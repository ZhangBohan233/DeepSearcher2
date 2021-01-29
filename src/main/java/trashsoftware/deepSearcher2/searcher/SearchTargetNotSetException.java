package trashsoftware.deepSearcher2.searcher;

/**
 * Indicates no search target is inputted
 */
public class SearchTargetNotSetException extends Exception {

    SearchTargetNotSetException(String message) {
        super(message);
    }
}
