package trashsoftware.deepSearcher2.searcher.contentSearchers;

import org.apache.poi.hwpf.extractor.WordExtractor;
import trashsoftware.deepSearcher2.searcher.ContentSearchingResult;
import trashsoftware.deepSearcher2.searcher.StringMatcher;
import trashsoftware.deepSearcher2.searcher.contentSearchers.TwoKeysSearcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class DocSearcher extends TwoKeysSearcher {

    public DocSearcher(File file, Class<? extends StringMatcher> matcherClass, boolean caseSensitive) {
        super(file, matcherClass, caseSensitive, ContentSearchingResult.PARAGRAPHS_KEY, ContentSearchingResult.CHARS_KEY);
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
