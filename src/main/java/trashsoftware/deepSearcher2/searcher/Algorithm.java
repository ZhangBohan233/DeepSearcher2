package trashsoftware.deepSearcher2.searcher;

import trashsoftware.deepSearcher2.controllers.Client;
import trashsoftware.deepSearcher2.searcher.matchers.StringMatcher;
import trashsoftware.deepSearcher2.searcher.matchers.regexMatchers.NativeRegexMatcher;
import trashsoftware.deepSearcher2.searcher.matchers.regularMatchers.KMPMatcher;
import trashsoftware.deepSearcher2.searcher.matchers.regularMatchers.NaiveMatcher;
import trashsoftware.deepSearcher2.searcher.matchers.regularMatchers.NativeMatcher;
import trashsoftware.deepSearcher2.searcher.matchers.regularMatchers.SundayMatcher;
import trashsoftware.deepSearcher2.searcher.matchers.wordMatchers.HashedWordMatcher;
import trashsoftware.deepSearcher2.searcher.matchers.wordMatchers.NaiveWordMatcher;

/**
 * An interface of enums that represents a matching algorithm.
 */
public interface Algorithm {

    /**
     * Returns the string matcher class corresponding to this algorithm.
     *
     * @return the string matcher class corresponding to this algorithm
     */
    Class<? extends StringMatcher> getMatcherClass();

    /**
     * Algorithms for regular searching.
     */
    enum Regular implements Algorithm {
        AUTO("algAuto", null),
        NATIVE("algNative", NativeMatcher.class),
        NAIVE("algNaive", NaiveMatcher.class),
        KMP("algKmp", KMPMatcher.class),
        SUNDAY("algSunday", SundayMatcher.class);

        private final Class<? extends StringMatcher> matcherClass;
        private final String showKey;

        Regular(String showKey, Class<? extends StringMatcher> matcherClass) {
            this.showKey = showKey;
            this.matcherClass = matcherClass;
        }

        @Override
        public Class<? extends StringMatcher> getMatcherClass() {
            return matcherClass;
        }

        @Override
        public String toString() {
            return Client.getBundle().getString(showKey);
        }
    }

    /**
     * Algorithms for full word searching.
     */
    enum Word implements Algorithm {
        NAIVE("algNaive", NaiveWordMatcher.class),
        HASH("algHash", HashedWordMatcher.class);

        private final Class<? extends StringMatcher> matcherClass;
        private final String showKey;

        Word(String showKey, Class<? extends StringMatcher> matcherClass) {
            this.showKey = showKey;
            this.matcherClass = matcherClass;
        }

        @Override
        public Class<? extends StringMatcher> getMatcherClass() {
            return matcherClass;
        }

        @Override
        public String toString() {
            return Client.getBundle().getString(showKey);
        }
    }

    /**
     * Algorithms for regular expression matching.
     */
    enum Regex implements Algorithm {
        NATIVE("algNative", NativeRegexMatcher.class);

        private final Class<? extends StringMatcher> matcherClass;
        private final String showKey;

        Regex(String showKey, Class<? extends StringMatcher> matcherClass) {
            this.showKey = showKey;
            this.matcherClass = matcherClass;
        }

        @Override
        public Class<? extends StringMatcher> getMatcherClass() {
            return matcherClass;
        }

        @Override
        public String toString() {
            return Client.getBundle().getString(showKey);
        }
    }
}
