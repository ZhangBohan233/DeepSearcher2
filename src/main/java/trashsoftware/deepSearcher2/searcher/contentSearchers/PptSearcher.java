package trashsoftware.deepSearcher2.searcher.contentSearchers;

import trashsoftware.deepSearcher2.searcher.ContentSearchingResult;
import trashsoftware.deepSearcher2.searcher.matchers.MatcherFactory;
import trashsoftware.deepSearcher2.searcher.matchers.StringMatcher;

import java.io.File;
import java.util.List;

public class PptSearcher extends TwoKeysSearcher {

    public PptSearcher(File file, MatcherFactory matcherFactory, boolean caseSensitive) {
        super(file, matcherFactory, caseSensitive, ContentSearchingResult.PAGES_KEY, ContentSearchingResult.PARAGRAPHS_KEY);
    }

    @Override
    protected void searchFile(List<String> targets) {

    }
}
