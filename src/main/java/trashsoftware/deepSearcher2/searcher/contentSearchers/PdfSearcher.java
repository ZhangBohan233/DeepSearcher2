package trashsoftware.deepSearcher2.searcher.contentSearchers;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import trashsoftware.deepSearcher2.searcher.ContentSearchingResult;
import trashsoftware.deepSearcher2.searcher.matchers.MatcherFactory;
import trashsoftware.deepSearcher2.searcher.matchers.StringMatcher;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PdfSearcher extends TwoKeysSearcher {

    public PdfSearcher(File file, MatcherFactory matcherFactory, boolean caseSensitive) {
        super(file, matcherFactory, caseSensitive, ContentSearchingResult.PAGES_KEY, ContentSearchingResult.CHARS_KEY);
    }

    @Override
    protected void searchFile(List<String> targets) {
        try {
            PDDocument document = PDDocument.load(file);
            if (!document.isEncrypted()) {

                PDFTextStripper stripper = new PDFTextStripper();

                int endPage = document.getNumberOfPages();

                for (int i = 1; i < endPage; i++) {
                    stripper.setStartPage(i);
                    stripper.setEndPage(i + 1);
                    String page = stripper.getText(document);

                    searchInString(page, targets, i);
                }

                document.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
