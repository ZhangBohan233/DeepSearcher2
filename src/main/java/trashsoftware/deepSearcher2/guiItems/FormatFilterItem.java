package trashsoftware.deepSearcher2.guiItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FormatFilterItem {

    public static final int FILTER_ALL = 1;
    public static final int FILTER_TEXT = 2;
    public static final int FILTER_CODES = 3;
    public static final int FILTER_MS_OFFICE = 4;
    public static final int FILTER_OTHERS = 5;

    public static final Set<String> TEXT_FMTS = Set.of(
            "txt", "log"
    );

    public static final Set<String> CODE_FMTS = Set.of(
            "c", "cpp", "h", "java", "js", "py"
    );

    public static final Set<String> MS_OFFICE_FORMATS = Set.of(
            "doc", "docx", "ppt", "pptx"
    );

    private final int filterType;
    private final String showing;

    public FormatFilterItem(int filterType, String showing) {
        this.filterType = filterType;
        this.showing = showing;
    }

    public List<FormatItem> filter(List<FormatItem> allFormats) {
        List<FormatItem> result = new ArrayList<>();
        switch (filterType) {
            case FILTER_TEXT:
                for (FormatItem fi : allFormats) {
                    if (TEXT_FMTS.contains(fi.getExtension())) result.add(fi);
                }
                break;
            case FILTER_CODES:
                for (FormatItem fi : allFormats) {
                    if (CODE_FMTS.contains(fi.getExtension())) result.add(fi);
                }
                break;
            case FILTER_MS_OFFICE:
                for (FormatItem fi : allFormats) {
                    if (MS_OFFICE_FORMATS.contains(fi.getExtension())) result.add(fi);
                }
                break;
            case FILTER_OTHERS:
                for (FormatItem fi : allFormats) {
                    String ext = fi.getExtension();
                    if (!(TEXT_FMTS.contains(ext) ||
                            CODE_FMTS.contains(ext) ||
                            MS_OFFICE_FORMATS.contains(ext)))
                        result.add(fi);
                }
                break;
            default:
                result.addAll(allFormats);
                break;
        }
        return result;
    }

    @Override
    public String toString() {
        return showing;
    }
}
