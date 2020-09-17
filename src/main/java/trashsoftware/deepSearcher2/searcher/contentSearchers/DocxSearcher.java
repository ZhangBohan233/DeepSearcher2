package trashsoftware.deepSearcher2.searcher.contentSearchers;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import trashsoftware.deepSearcher2.searcher.ContentSearchingResult;
import trashsoftware.deepSearcher2.searcher.matchers.MatcherFactory;
import trashsoftware.deepSearcher2.searcher.matchers.StringMatcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class DocxSearcher extends TwoKeysSearcher {

    public DocxSearcher(File file, MatcherFactory matcherFactory, boolean caseSensitive) {
        super(file, matcherFactory, caseSensitive, ContentSearchingResult.PARAGRAPHS_KEY, ContentSearchingResult.CHARS_KEY);
    }

    @Override
    protected void searchFile(List<String> targets) {
        try {
            FileInputStream fis = new FileInputStream(file);
            XWPFDocument docx = new XWPFDocument(fis);
            List<XWPFParagraph> paragraphs = docx.getParagraphs();
            for (int i = 0; i < paragraphs.size(); i++) {
                String par = paragraphs.get(i).getText();
                searchInString(par, targets, i);
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
