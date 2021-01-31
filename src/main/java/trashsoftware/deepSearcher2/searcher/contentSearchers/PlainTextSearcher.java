package trashsoftware.deepSearcher2.searcher.contentSearchers;

import trashsoftware.deepSearcher2.searcher.ContentResult;
import trashsoftware.deepSearcher2.searcher.matchers.MatcherFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

/**
 * Searcher for any plain text format.
 */
public class PlainTextSearcher extends TwoKeysSearcher {

    public PlainTextSearcher(File file, MatcherFactory matcherFactory, boolean caseSensitive) {
        super(file, matcherFactory, caseSensitive, ContentResult.Category.LINE, ContentResult.Category.CHAR);
    }

    @Override
    protected void searchFile(List<String> targets) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineCount = 1;
            while ((line = bufferedReader.readLine()) != null) {
                searchInString(line, targets, lineCount);
                lineCount++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
