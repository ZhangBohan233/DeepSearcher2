package trashsoftware.deepSearcher2.searcher.matchers;

import trashsoftware.deepSearcher2.searcher.PrefSet;
import trashsoftware.deepSearcher2.searcher.matchers.regexMatchers.NativeRegexMatcher;
import trashsoftware.deepSearcher2.searcher.matchers.regularMatchers.KMPMatcher;
import trashsoftware.deepSearcher2.searcher.matchers.regularMatchers.NaiveMatcher;
import trashsoftware.deepSearcher2.searcher.matchers.regularMatchers.NativeMatcher;
import trashsoftware.deepSearcher2.searcher.matchers.regularMatchers.SundayMatcher;
import trashsoftware.deepSearcher2.searcher.matchers.wordMatchers.HashedWordMatcher;
import trashsoftware.deepSearcher2.searcher.matchers.wordMatchers.NaiveWordMatcher;

public abstract class MatcherFactory {

    public abstract StringMatcher createMatcher(String string);

    public static MatcherFactory createFactoryByPrefSet(PrefSet prefSet) {
        if (prefSet.getMatchMode() == PrefSet.NORMAL) {
            switch (prefSet.getMatchingAlgorithm()) {
                case "algAuto":
                    return new AutoRegularMatcherFactory();
                case "algNative":
                    return new FixedMatcherFactory(NativeMatcher.class);
                case "algNaive":
                    return new FixedMatcherFactory(NaiveMatcher.class);
                case "algKmp":
                    return new FixedMatcherFactory(KMPMatcher.class);
                case "algSunday":
                    return new FixedMatcherFactory(SundayMatcher.class);
                default:
                    throw new RuntimeException("Not a valid matching algorithm");
            }
        } else if (prefSet.getMatchMode() == PrefSet.WORD) {
            switch (prefSet.getWordMatchingAlgorithm()) {
                case "algNaive":
                    return new FixedMatcherFactory(NaiveWordMatcher.class);
                case "algHash":
                    return new FixedMatcherFactory(HashedWordMatcher.class);
                default:
                    throw new RuntimeException("Not a valid matching algorithm for words");
            }
        } else if (prefSet.getMatchMode() == PrefSet.REGEX) {
            if (prefSet.getRegexAlgorithm().equals("algNative")) {
                return new FixedMatcherFactory(NativeRegexMatcher.class);
            } else {
                throw new RuntimeException("Not a valid matching algorithm for regex");
            }
        } else {
            throw new RuntimeException("Invalid match mode");
        }
    }
}
