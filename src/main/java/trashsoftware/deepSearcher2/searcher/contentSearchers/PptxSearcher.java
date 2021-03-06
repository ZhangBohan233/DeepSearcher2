package trashsoftware.deepSearcher2.searcher.contentSearchers;

import org.apache.poi.xslf.usermodel.*;
import trashsoftware.deepSearcher2.searcher.ContentResult;
import trashsoftware.deepSearcher2.searcher.matchers.MatcherFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

public class PptxSearcher extends TwoIntOneStrSearcher {
    public PptxSearcher(File file, MatcherFactory matcherFactory, boolean caseSensitive) {
        super(file, matcherFactory, caseSensitive, ContentResult.Category.PAGE, ContentResult.Category.CHAR);
    }

    @Override
    protected void searchFile(List<String> targets) {
        try (XMLSlideShow slideShow = new XMLSlideShow(new FileInputStream(file))) {
            int page = 1;
            for (XSLFSlide slide : slideShow.getSlides()) {
                List<XSLFShape> shapes = slide.getShapes();

                String title = slide.getTitle();
                if (title != null) searchInString(title, targets, page, ContentResult.ValueCategory.TITLE);

                int pageTextCount = 0;
                for (int j = 0; j < shapes.size(); j++) {
                    XSLFShape shape = shapes.get(j);
                    if (shape instanceof XSLFTextShape) {  // text shape
                        String text = ((XSLFTextShape) shape).getText();
                        if (j == 0 && text.equals(title)) {
                            continue;  // first shape (title) already checked, if not null
                        }
                        searchInString(text,
                                targets, page, pageTextCount, ContentResult.ValueCategory.TEXT);
                        pageTextCount += text.length();
                    } else if (shape instanceof XSLFTable) {
                        XSLFTable table = (XSLFTable) shape;
                        for (int r = 0; r < table.getNumberOfRows(); r++) {
                            for (int c = 0; c < table.getNumberOfColumns(); c++) {
                                XSLFTableCell cell = table.getCell(r, c);
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

    @Override
    protected String readWholeFile() {
        try (XMLSlideShow slideShow = new XMLSlideShow(new FileInputStream(file))) {
            StringBuilder builder = new StringBuilder();

            for (XSLFSlide slide : slideShow.getSlides()) {
                List<XSLFShape> shapes = slide.getShapes();
                String title = slide.getTitle();
                if (title != null) builder.append(title).append('\n');

                for (int j = 0; j < shapes.size(); j++) {
                    XSLFShape shape = shapes.get(j);
                    if (shape instanceof XSLFTextShape) {  // text shape
                        String text = ((XSLFTextShape) shape).getText();
                        if (j == 0 && text.equals(title)) {
                            continue;  // first shape (title) already checked, if not null
                        }
                        builder.append(text).append('\n');
                    } else if (shape instanceof XSLFTable) {
                        XSLFTable table = (XSLFTable) shape;
                        for (int r = 0; r < table.getNumberOfRows(); r++) {
                            for (int c = 0; c < table.getNumberOfColumns(); c++) {
                                XSLFTableCell cell = table.getCell(r, c);
                                if (cell != null) builder.append(cell.getText()).append('\n');
                            }
                        }
                    }
                }
                builder.append('\f');
            }
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
