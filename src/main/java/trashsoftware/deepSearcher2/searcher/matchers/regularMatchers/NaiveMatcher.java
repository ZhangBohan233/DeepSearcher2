package trashsoftware.deepSearcher2.searcher.matchers.regularMatchers;

import trashsoftware.deepSearcher2.searcher.matchers.StringMatcher;

/**
 * A string matcher that is implemented by the naive algorithm.
 * <p>
 * This algorithm is strongly not-recommended.
 */
public class NaiveMatcher extends StringMatcher {

    public NaiveMatcher(String s) {
        super(s);
    }

    private static int naiveMatch(String string, String pattern) {
        if (pattern.length() > string.length()) return -1;
        for (int i = 0; i < string.length() - pattern.length() + 1; i++) {
            int j;
            for (j = 0; j < pattern.length(); j++) {
                if (string.charAt(i + j) != pattern.charAt(j)) break;
            }
            if (j == pattern.length()) return i;
        }
        return -1;
    }

    @Override
    public int search(String target) {
        return naiveMatch(string, target);
    }
}
