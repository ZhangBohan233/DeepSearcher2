package trashsoftware.deepSearcher2.searcher.contentSearchers;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import trashsoftware.deepSearcher2.searcher.ContentResult;
import trashsoftware.deepSearcher2.searcher.matchers.MatcherFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

public class DocxSearcher extends TwoKeysSearcher {

    public DocxSearcher(File file, MatcherFactory matcherFactory, boolean caseSensitive) {
        super(file, matcherFactory, caseSensitive, ContentResult.Category.PARAGRAPH, ContentResult.Category.CHAR);
    }

    @Override
    protected void searchFile(List<String> targets) {
        try (XWPFDocument docx = new XWPFDocument(new FileInputStream(file))) {
            List<XWPFParagraph> paragraphs = docx.getParagraphs();
            for (int i = 0; i < paragraphs.size(); i++) {
                String par = paragraphs.get(i).getText();
                searchInString(par, targets, i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String readWholeFile() {
        try (XWPFDocument docx = new XWPFDocument(new FileInputStream(file))) {
            StringBuilder builder = new StringBuilder();
            List<XWPFParagraph> paragraphs = docx.getParagraphs();
            for (XWPFParagraph paragraph : paragraphs) {
                String par = paragraph.getText();
                builder.append(par).append('\n');
            }
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
