package trashsoftware.deepSearcher2.searcher.contentSearchers;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import trashsoftware.deepSearcher2.searcher.ContentResult;
import trashsoftware.deepSearcher2.searcher.matchers.MatcherFactory;

import java.io.File;
import java.util.List;

public class PdfSearcher extends TwoKeysSearcher {

    public PdfSearcher(File file, MatcherFactory matcherFactory, boolean caseSensitive) {
        super(file, matcherFactory, caseSensitive, ContentResult.Category.PAGE, ContentResult.Category.CHAR);
    }

    @Override
    protected void searchFile(List<String> targets) {
        try (PDDocument document = PDDocument.load(file)) {
            if (!document.isEncrypted()) {
                PDFTextStripper stripper = new PDFTextStripper();
                int endPage = document.getNumberOfPages();
                for (int i = 1; i < endPage; i++) {
                    stripper.setStartPage(i);
                    stripper.setEndPage(i + 1);
                    String page = stripper.getText(document);

                    searchInString(page, targets, i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
