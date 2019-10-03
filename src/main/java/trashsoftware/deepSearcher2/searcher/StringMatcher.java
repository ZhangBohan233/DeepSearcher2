package trashsoftware.deepSearcher2.searcher;

public interface StringMatcher {

    /**
     * Returns {@code true} if the <code>target</code> is in this {@code StringMatcher}.
     *
     * @param target the pattern to be matched
     * @return {@code true} if the <code>target</code> is in this {@code StringMatcher}
     */
    boolean contains(String target);
}
