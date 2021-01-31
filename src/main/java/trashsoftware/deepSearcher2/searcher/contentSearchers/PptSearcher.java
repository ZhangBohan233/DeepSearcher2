package trashsoftware.deepSearcher2.searcher.contentSearchers;

import org.apache.poi.hslf.usermodel.*;
import trashsoftware.deepSearcher2.searcher.ContentResult;
import trashsoftware.deepSearcher2.searcher.matchers.MatcherFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

public class PptSearcher extends TwoIntOneStrSearcher {

    public PptSearcher(File file, MatcherFactory matcherFactory, boolean caseSensitive) {
        super(file, matcherFactory, caseSensitive, ContentResult.Category.PAGE, ContentResult.Category.CHAR);
    }

    @Override
    protected void searchFile(List<String> targets) {
        try (HSLFSlideShow slideShow = new HSLFSlideShow(new FileInputStream(file))) {
            int page = 1;
            for (HSLFSlide slide : slideShow.getSlides()) {
                List<HSLFShape> shapes = slide.getShapes();

                String title = slide.getTitle();
                if (title != null) searchInString(title, targets, page, ContentResult.ValueCategory.TITLE);

                int pageTextCount = 0;
                for (int j = 0; j < shapes.size(); j++) {
                    HSLFShape shape = shapes.get(j);
                    if (shape instanceof HSLFTextShape) {  // text shape
                        String text = ((HSLFTextShape) shape).getText();
                        if (j == 0 && text.equals(title)) {
                            continue;  // first shape (title) already checked, if not null
                        }
                        searchInString(text,
                                targets, page, pageTextCount, ContentResult.ValueCategory.TEXT);
                        pageTextCount += text.length();
                    } else if (shape instanceof HSLFTable) {
                        HSLFTable table = (HSLFTable) shape;
                        for (int r = 0; r < table.getNumberOfRows(); r++) {
                            for (int c = 0; c < table.getNumberOfColumns(); c++) {
                                HSLFTableCell cell = table.getCell(r, c);
                                if (cell != null) {
                                    searchInString(cell.getText(), targets, page, ContentResult.ValueCategory.TABLE);
                                }
                            }
                        }
                    }
                }

                page++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
