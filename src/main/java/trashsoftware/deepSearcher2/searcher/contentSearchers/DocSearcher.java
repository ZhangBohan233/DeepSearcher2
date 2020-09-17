package trashsoftware.deepSearcher2.searcher.contentSearchers;

import org.apache.poi.hwpf.extractor.WordExtractor;
import trashsoftware.deepSearcher2.searcher.ContentSearchingResult;
import trashsoftware.deepSearcher2.searcher.matchers.MatcherFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class DocSearcher extends TwoKeysSearcher {

    public DocSearcher(File file, MatcherFactory matcherFactory, boolean caseSensitive) {
        super(file, matcherFactory, caseSensitive, ContentSearchingResult.PARAGRAPHS_KEY, ContentSearchingResult.CHARS_KEY);
    }

    @Override
    protected void searchFile(List<String> targets) {
        try {
            FileInputStream fis = new FileInputStream(file);
            WordExtractor we = new WordExtractor(fis);

            String[] paragraphs = we.getParagraphText();

            for (int i = 0; i < paragraphs.length; i++) {
                String paragraph = paragraphs[i];
                searchInString(paragraph, targets, i);
            }
            fis.close();
            we.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
