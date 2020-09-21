package trashsoftware.deepSearcher2.searcher.contentSearchers;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import trashsoftware.deepSearcher2.searcher.matchers.MatcherFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

public class XlsSearcher extends ExcelSearcher {

    public XlsSearcher(File file, MatcherFactory matcherFactory, boolean caseSensitive) {
        super(file, matcherFactory, caseSensitive);
    }

    @Override
    protected void searchFile(List<String> targets) {
        try {
            FileInputStream fis = new FileInputStream(file);
            HSSFWorkbook workbook = new HSSFWorkbook(fis);

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                HSSFSheet sheet = workbook.getSheetAt(i);
                searchOneSheet(sheet, targets);
            }

            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
