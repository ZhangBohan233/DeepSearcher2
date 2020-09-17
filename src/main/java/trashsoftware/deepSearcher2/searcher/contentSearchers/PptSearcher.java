package trashsoftware.deepSearcher2.searcher.contentSearchers;

import trashsoftware.deepSearcher2.searcher.ContentSearchingResult;
import trashsoftware.deepSearcher2.searcher.StringMatcher;

import java.io.File;
import java.util.List;
import java.util.Set;

public class PptSearcher extends TwoKeysSearcher {

    public PptSearcher(File file, Class<? extends StringMatcher> matcherClass, boolean caseSensitive) {
        super(file, matcherClass, caseSensitive, ContentSearchingResult.PAGES_KEY, ContentSearchingResult.PARAGRAPHS_KEY);
    }

    @Override
    protected void searchFile(List<String> targets) {

    }
}
