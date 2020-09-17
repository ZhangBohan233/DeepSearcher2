package trashsoftware.deepSearcher2.searcher.contentSearchers;

import trashsoftware.deepSearcher2.searcher.ContentSearcher;
import trashsoftware.deepSearcher2.searcher.ContentSearchingResult;
import trashsoftware.deepSearcher2.searcher.matchers.MatcherFactory;
import trashsoftware.deepSearcher2.searcher.matchers.StringMatcher;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class TwoKeysSearcher extends ContentSearcher {

    private final int key1;
    private final int key2;

    private final Set<String> foundTargets = new HashSet<>();
    private final List<Integer> found1s = new ArrayList<>();
    private final List<Integer> found2s = new ArrayList<>();

    public TwoKeysSearcher(File file, MatcherFactory matcherFactory, boolean caseSensitive,
                           int key1, int key2) {
        super(file, matcherFactory, caseSensitive);

        this.key1 = key1;
        this.key2 = key2;
    }

    @Override
    public ContentSearchingResult searchAll(List<String> targets) {

        foundTargets.clear();
        found1s.clear();
        found2s.clear();
        searchFile(targets);

        if (foundTargets.size() == targets.size()) {  // all matched
            return new ContentSearchingResult(key1, found1s,
                    key2, found2s);
        }

        return null;
    }

    @Override
    public ContentSearchingResult searchAny(List<String> targets) {
        foundTargets.clear();
        found1s.clear();
        found2s.clear();
        searchFile(targets);

        if (foundTargets.size() > 0) {  // at least one matched
            return new ContentSearchingResult(key1, found1s,
                    key2, found2s);
        }

        return null;
    }

    protected void searchInString(String string, List<String> targets, int thisInValue1) {
        if (!caseSensitive) string = string.toLowerCase();
        StringMatcher matcher = matcherFactory.createMatcher(string);
        for (String tar : targets) {
            int pos = matcher.search(tar);
            if (pos >= 0) {
                foundTargets.add(tar);
                found1s.add(thisInValue1);
                found2s.add(pos);
            }
        }
    }

    protected abstract void searchFile(List<String> targets);
}
