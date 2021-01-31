package trashsoftware.deepSearcher2.searcher.contentSearchers;

import org.apache.poi.hwpf.extractor.WordExtractor;
import trashsoftware.deepSearcher2.searcher.ContentResult;
import trashsoftware.deepSearcher2.searcher.matchers.MatcherFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

public class DocSearcher extends TwoKeysSearcher {

    public DocSearcher(File file, MatcherFactory matcherFactory, boolean caseSensitive) {
        super(file, matcherFactory, caseSensitive, ContentResult.Category.PARAGRAPH, ContentResult.Category.CHAR);
    }

    @Override
    protected void searchFile(List<String> targets) {
        try (WordExtractor we = new WordExtractor(new FileInputStream(file))) {
            String[] paragraphs = we.getParagraphText();

            for (int i = 0; i < paragraphs.length; i++) {
                String paragraph = paragraphs[i];
                searchInString(paragraph, targets, i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
