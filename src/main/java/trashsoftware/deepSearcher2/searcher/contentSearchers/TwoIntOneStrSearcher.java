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

public abstract class TwoIntOneStrSearcher extends ContentSearcher {

    private final int key1;
    private final int key2;

    private final Set<String> foundTargets = new HashSet<>();
    private final List<Integer> found1s = new ArrayList<>();
    private final List<Integer> found2s = new ArrayList<>();
    private final List<ContentSearchingResult.StringValue> strValues = new ArrayList<>();

    public TwoIntOneStrSearcher(File file, MatcherFactory matcherFactory, boolean caseSensitive,
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
        strValues.clear();
        searchFile(targets);

        if (foundTargets.size() == targets.size()) {  // all matched
            return new ContentSearchingResult(key1, found1s,
                    key2, found2s, strValues);
        }

        return null;
    }

    @Override
    public ContentSearchingResult searchAny(List<String> targets) {
        foundTargets.clear();
        found1s.clear();
        found2s.clear();
        strValues.clear();
        searchFile(targets);

        if (foundTargets.size() > 0) {  // at least one matched
            return new ContentSearchingResult(key1, found1s,
                    key2, found2s, strValues);
        }

        return null;
    }

    protected void searchInString(String string, List<String> targets, int value1, int strValue) {
        searchInString(string, targets, value1, 0, strValue);
    }

    protected void searchInString(String string, List<String> targets, int value1, int value2Base, int strCategory) {
        if (!caseSensitive) string = string.toLowerCase();
        StringMatcher matcher = matcherFactory.createMatcher(string);
        for (String tar : targets) {
            int pos = matcher.search(tar);
            if (pos >= 0) {
                foundTargets.add(tar);
                found1s.add(value1);
                found2s.add(pos + value2Base + 1);  // add one because index 0 means first (1st) element
                strValues.add(new ContentSearchingResult.StringValue(strCategory));
            }
        }
    }

    protected void searchInStringFixedV2(String string, List<String> targets, int value1, int value2,
                                         String strValue) {
        if (!caseSensitive) string = string.toLowerCase();
        StringMatcher matcher = matcherFactory.createMatcher(string);
        for (String tar : targets) {
            int pos = matcher.search(tar);
            if (pos >= 0) {
                foundTargets.add(tar);
                found1s.add(value1);
                found2s.add(value2);
                strValues.add(new ContentSearchingResult.StringValue(strValue));
            }
        }
    }

    protected abstract void searchFile(List<String> targets);
}
