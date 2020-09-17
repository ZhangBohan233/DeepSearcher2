package trashsoftware.deepSearcher2.searcher.matchers.regularMatchers;

import trashsoftware.deepSearcher2.searcher.matchers.StringMatcher;

public class NaiveMatcher extends StringMatcher {

    public NaiveMatcher(String s) {
        super(s);
    }

    @Override
    public int search(String target) {
        return naiveMatch(string, target);
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
}
