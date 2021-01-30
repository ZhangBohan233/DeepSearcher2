package trashsoftware.deepSearcher2.searcher.matchers;

/**
 * A class that stores a long text to be search.
 */
public abstract class StringMatcher {

    protected final String string;

    /**
     * Constructor.
     *
     * @param string the long string, which may be the file name or file content.
     */
    public StringMatcher(String string) {
        this.string = string;
    }

    /**
     * Returns the first occurrence position of {@code pattern} in {@code this.string}, {@code -1} if not found
     *
     * @param pattern the target string fragment
     * @return the first occurrence position of {@code pattern} in {@code this.string}, {@code -1} if not found
     */
    public abstract int search(String pattern);
}


