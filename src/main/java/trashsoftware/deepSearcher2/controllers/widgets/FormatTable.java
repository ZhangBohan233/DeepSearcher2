package trashsoftware.deepSearcher2.controllers.widgets;

import javafx.scene.control.TableView;
import trashsoftware.deepSearcher2.guiItems.FormatFilterItem;
import trashsoftware.deepSearcher2.guiItems.FormatItem;
import trashsoftware.deepSearcher2.guiItems.FormatType;
import trashsoftware.deepSearcher2.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FormatTable extends TableView<FormatItem> {

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
    public static final Map<FormatType, Set<String>> FMT_MAP = Map.of(
            FormatType.TEXT, TEXT_FMTS,
            FormatType.CODES, CODE_FMTS,
            FormatType.MS_OFFICE, MS_OFFICE_FMTS,
            FormatType.DOCUMENTS, DOCUMENT_FMTS
    );
    private final Set<String> knownFormats =
            Util.mergeSets(TEXT_FMTS, CODE_FMTS, MS_OFFICE_FMTS, DOCUMENT_FMTS);
    private final List<FormatItem> allItems = new ArrayList<>();
    private Map<String, String> customFormats;

    public void addItem(FormatItem formatItem) {
        allItems.add(formatItem);
    }

    public void clearItems() {
        allItems.clear();
    }

    public void setFilter(FormatFilterItem filterItem) {
        getItems().clear();
        List<FormatItem> filtered = filterItem.filter(allItems);
        getItems().addAll(filtered);
    }

    public List<FormatItem> getAllItems() {
        return allItems;
    }

    public List<FormatItem> getShowingItems() {
        return getItems();
    }

    public Map<String, String> getCustomFormats() {
        return customFormats;
    }

    public void setCustomFormats(Map<String, String> customFormats) {
        this.customFormats = customFormats;
        knownFormats.addAll(customFormats.keySet());
    }

    public Set<String> getKnownFormats() {
        return knownFormats;
    }
}
