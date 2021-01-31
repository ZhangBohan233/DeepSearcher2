package trashsoftware.deepSearcher2.searcher.contentSearchers;

import trashsoftware.deepSearcher2.searcher.ContentResult;
import trashsoftware.deepSearcher2.searcher.ContentSearcher;
import trashsoftware.deepSearcher2.searcher.matchers.MatcherFactory;
import trashsoftware.deepSearcher2.searcher.matchers.StringMatcher;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Searchers that have two ints as sub-keys.
 * <p>
 * For example, "page" and "line" are two int keys.
 */
public abstract class TwoKeysSearcher extends ContentSearcher {

    private final ContentResult.Category key1;
    private final ContentResult.Category key2;

    private final Set<String> foundTargets = new HashSet<>();
    private final List<Integer> found1s = new ArrayList<>();
    private final List<Integer> found2s = new ArrayList<>();

    public TwoKeysSearcher(File file,
                           MatcherFactory matcherFactory,
                           boolean caseSensitive,
                           ContentResult.Category key1,
                           ContentResult.Category key2) {
        super(file, matcherFactory, caseSensitive);

        this.key1 = key1;
        this.key2 = key2;
    }

    @Override
    public ContentResult searchAll(List<String> targets) {
        foundTargets.clear();
        found1s.clear();
        found2s.clear();
        searchFile(targets);

        if (foundTargets.size() == targets.size()) {  // all matched
            return new ContentResult(key1, found1s,
                    key2, found2s);
        }

        return null;
    }

    @Override
    public ContentResult searchAny(List<String> targets) {
        foundTargets.clear();
        found1s.clear();
        found2s.clear();
        searchFile(targets);

        if (foundTargets.size() > 0) {  // at least one matched
            return new ContentResult(key1, found1s,
                    key2, found2s);
        }

        return null;
    }

    protected void searchInString(String string, List<String> targets, int value1) {
        if (!caseSensitive) string = string.toLowerCase();
        StringMatcher matcher = matcherFactory.createMatcher(string);
        for (String tar : targets) {
            int pos = matcher.search(tar);
            if (pos >= 0) {
                foundTargets.add(tar);
                found1s.add(value1);
                found2s.add(pos + 1);
            }
        }
    }

    protected void searchInString(String string, List<String> targets, int value1, int value2) {
        if (!caseSensitive) string = string.toLowerCase();
        StringMatcher matcher = matcherFactory.createMatcher(string);
        for (String tar : targets) {
            int pos = matcher.search(tar);
            if (pos >= 0) {
                foundTargets.add(tar);
                found1s.add(value1);
                found2s.add(value2);
            }
        }
    }

    protected abstract void searchFile(List<String> targets);
}
