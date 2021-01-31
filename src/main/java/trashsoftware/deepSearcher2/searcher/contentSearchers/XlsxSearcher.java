package trashsoftware.deepSearcher2.searcher.contentSearchers;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import trashsoftware.deepSearcher2.searcher.matchers.MatcherFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

public class XlsxSearcher extends ExcelSearcher {

    public XlsxSearcher(File file, MatcherFactory matcherFactory, boolean caseSensitive) {
        super(file, matcherFactory, caseSensitive);
    }

    @Override
    protected void searchFile(List<String> targets) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file))) {
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                XSSFSheet sheet = workbook.getSheetAt(i);
                searchOneSheet(sheet, targets);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String readWholeFile() {
        try (XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file))) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                XSSFSheet sheet = workbook.getSheetAt(i);
                builder.append(sheetToString(sheet)).append("\f");
            }
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
