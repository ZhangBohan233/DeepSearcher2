package trashsoftware.deepSearcher2.searcher.matchers;

import trashsoftware.deepSearcher2.searcher.Algorithm;
import trashsoftware.deepSearcher2.searcher.PrefSet;

public abstract class MatcherFactory {

    /**
     * Creates a matcher factory according to a pref set.
     *
     * @param prefSet the pref set
     * @return the new factory
     */
    public static MatcherFactory createFactoryByPrefSet(PrefSet prefSet) {
        Algorithm alg;
        if (prefSet.getMatchMode() == MatchMode.NORMAL) {
            alg = prefSet.getMatchingAlgorithm();
            if (alg == Algorithm.Regular.AUTO) return new AutoRegularMatcherFactory();
        } else if (prefSet.getMatchMode() == MatchMode.WORD) {
            alg = prefSet.getWordMatchingAlgorithm();
        } else if (prefSet.getMatchMode() == MatchMode.REGEX) {
            alg = prefSet.getRegexAlgorithm();
        } else {
            throw new RuntimeException("Invalid match mode");
        }
        return new FixedMatcherFactory(alg.getMatcherClass());
    }

    /**
     * Creates a real matcher.
     *
     * @param string the string to be searched
     * @return the matcher
     */
    public abstract StringMatcher createMatcher(String string);
}
