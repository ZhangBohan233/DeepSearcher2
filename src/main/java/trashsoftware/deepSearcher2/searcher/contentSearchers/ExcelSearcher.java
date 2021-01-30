package trashsoftware.deepSearcher2.searcher.contentSearchers;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import trashsoftware.deepSearcher2.searcher.ContentSearchingResult;
import trashsoftware.deepSearcher2.searcher.matchers.MatcherFactory;

import java.io.File;
import java.util.List;

/**
 * Content searcher for Microsoft Excel formats.
 *
 * This class is overridden by two subclasses which search .xls and .xlsx
 */
abstract class ExcelSearcher extends TwoIntOneStrSearcher {
    public ExcelSearcher(File file, MatcherFactory matcherFactory, boolean caseSensitive) {
        super(file, matcherFactory, caseSensitive,
                ContentSearchingResult.ROWS_KEY, ContentSearchingResult.COLUMNS_KEY);
    }

    void searchOneSheet(Sheet sheet, List<String> targets) {
        int lastRowNum = sheet.getLastRowNum();
        for (int r = sheet.getFirstRowNum(); r <= lastRowNum; r++) {
            Row row = sheet.getRow(r);
            int lastCellNum = row.getLastCellNum();
            for (int c = row.getFirstCellNum(); c < lastCellNum; c++) {
                Cell cell = row.getCell(c);
                if (cell != null) {
                    searchInStringFixedV2(
                            cell.toString(),
                            targets,
                            r + 1,
                            c + 1,
                            sheet.getSheetName());
                }
            }
        }
    }
}
