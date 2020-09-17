package trashsoftware.deepSearcher2.searcher.matchers;

import trashsoftware.deepSearcher2.searcher.matchers.MatcherFactory;
import trashsoftware.deepSearcher2.searcher.matchers.regularMatchers.NativeMatcher;
import trashsoftware.deepSearcher2.searcher.matchers.regularMatchers.SundayMatcher;

public class AutoRegularMatcherFactory extends MatcherFactory {

    /**
     * If the given string has length exceeds {@code THRESHOLD}, use a high-level matcher.
     * Otherwise use the native matcher
     */
    public static final int THRESHOLD = 1000;

    public StringMatcher createMatcher(String string) {
        if (string.length() > THRESHOLD) {
            return new SundayMatcher(string);
        } else {
            return new NativeMatcher(string);
        }
    }
}
