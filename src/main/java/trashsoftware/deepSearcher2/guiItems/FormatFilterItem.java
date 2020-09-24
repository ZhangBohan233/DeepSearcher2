package trashsoftware.deepSearcher2.guiItems;

import trashsoftware.deepSearcher2.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FormatFilterItem {

    public static final int FILTER_ALL = 1;
    public static final int FILTER_TEXT = 2;
    public static final int FILTER_CODES = 3;
    public static final int FILTER_MS_OFFICE = 4;
    public static final int FILTER_DOCUMENTS = 5;
    public static final int FILTER_OTHERS = 10000;

    public static final Set<String> TEXT_FMTS = Set.of(
            "txt", "log"
    );

    public static final Set<String> CODE_FMTS = Set.of(
            "c", "cpp", "h", "java", "js", "py", "r"
    );

    public static final Set<String> MS_OFFICE_FMTS = Set.of(
            "doc", "docx", "ppt", "pptx", "xls", "xlsx"
    );

    public static final Set<String> DOCUMENT_FMTS = Set.of(
            "pdf", "rmd", "tex"
    );

    public static final Set<String> ALL_KNOWN_FMTS =
            Util.mergeSets(TEXT_FMTS, CODE_FMTS, MS_OFFICE_FMTS, DOCUMENT_FMTS);

    public static final Map<Integer, Set<String>> FMT_MAP = Map.of(
            FILTER_TEXT, TEXT_FMTS,
            FILTER_CODES, CODE_FMTS,
            FILTER_MS_OFFICE, MS_OFFICE_FMTS,
            FILTER_DOCUMENTS, DOCUMENT_FMTS
    );

    private final int filterType;
    private final String showing;

    public FormatFilterItem(int filterType, String showing) {
        this.filterType = filterType;
        this.showing = showing;
    }

    public List<FormatItem> filter(List<FormatItem> allFormats) {
        List<FormatItem> result = new ArrayList<>();
        if (filterType == FILTER_ALL) {
            result.addAll(allFormats);
        } else if (filterType == FILTER_OTHERS) {
            for (FormatItem fi: allFormats) {
                if (!ALL_KNOWN_FMTS.contains(fi.getExtension())) result.add(fi);
            }
        } else {
            Set<String> fmts = FMT_MAP.get(filterType);
            for (FormatItem fi: allFormats) {
                if (fmts.contains(fi.getExtension())) result.add(fi);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return showing;
    }
}
