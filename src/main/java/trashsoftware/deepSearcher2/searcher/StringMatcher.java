package trashsoftware.deepSearcher2.searcher;

public abstract class StringMatcher {

    protected final String string;

    StringMatcher(String string) {
        this.string = string;
    }

    /**
     * Returns the first occurrence position of {@code pattern} in {@code this.string}, {@code -1} if not found
     *
     * @param pattern the target string fragment
     * @return the first occurrence position of {@code pattern} in {@code this.string}, {@code -1} if not found
     */
    abstract int search(String pattern);
}


