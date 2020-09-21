package trashsoftware.deepSearcher2.searcher.contentSearchers;

import org.apache.poi.xslf.usermodel.*;
import trashsoftware.deepSearcher2.searcher.ContentSearchingResult;
import trashsoftware.deepSearcher2.searcher.matchers.MatcherFactory;
import trashsoftware.deepSearcher2.searcher.matchers.StringMatcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class PptxSearcher extends TwoIntOneStrSearcher {
    public PptxSearcher(File file, MatcherFactory matcherFactory, boolean caseSensitive) {
        super(file, matcherFactory, caseSensitive, ContentSearchingResult.PAGES_KEY, ContentSearchingResult.CHARS_KEY);
    }

    @Override
    protected void searchFile(List<String> targets) {
        try {
            FileInputStream fis = new FileInputStream(file);
            XMLSlideShow slideShow = new XMLSlideShow(fis);
            int page = 1;
            for (XSLFSlide slide : slideShow.getSlides()) {
                List<XSLFShape> shapes = slide.getShapes();

                String title = slide.getTitle();
                if (title != null) searchInString(title, targets, page, ContentSearchingResult.TITLE_VALUE);

                int pageTextCount = 0;
                for (int j = 0; j < shapes.size(); j++) {
                    XSLFShape shape = shapes.get(j);
                    if (shape instanceof XSLFTextShape) {  // text shape
                        String text = ((XSLFTextShape) shape).getText();
                        if (j == 0 && text.equals(title)) {
                            continue;  // first shape (title) already checked, if not null
                        }
                        searchInString(text,
                                targets, page, pageTextCount, ContentSearchingResult.TEXT_VALUE);
                        pageTextCount += text.length();
                    } else if (shape instanceof XSLFTable) {
                        XSLFTable table = (XSLFTable) shape;
                        for (int r = 0; r < table.getNumberOfRows(); r++) {
                            for (int c = 0; c < table.getNumberOfColumns(); c++) {
                                XSLFTableCell cell = table.getCell(r, c);
                                if (cell != null) {
                                    searchInString(cell.getText(), targets, page, ContentSearchingResult.TABLE_VALUE);
                                }
                            }
                        }
                    }
                }

                page++;
            }
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
