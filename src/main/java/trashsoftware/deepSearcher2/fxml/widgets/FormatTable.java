package trashsoftware.deepSearcher2.fxml.widgets;

import javafx.scene.control.TableView;
import trashsoftware.deepSearcher2.guiItems.FormatFilterItem;
import trashsoftware.deepSearcher2.guiItems.FormatItem;
import trashsoftware.deepSearcher2.guiItems.FormatType;
import trashsoftware.deepSearcher2.util.Util;

import java.util.*;

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
    private static final Set<String> BUILTIN_FORMATS =
            Util.mergeSets(TEXT_FMTS, CODE_FMTS, MS_OFFICE_FMTS, DOCUMENT_FMTS);
    private final Set<String> knownFormats = new HashSet<>();
    private final List<FormatItem> allItems = new ArrayList<>();
    private Map<String, String> customFormats;
    private Map<String, String> extensionFormats;

    public FormatTable() {
        initialize();
    }

    /**
     * Adds a format.
     *
     * @param formatItem the format to be added
     */
    public void addItem(FormatItem formatItem) {
        allItems.add(formatItem);
    }

    /**
     * Restore all status to the initial status
     */
    public void initialize() {
        allItems.clear();
        getItems().clear();
        if (customFormats != null) customFormats.clear();
        knownFormats.clear();
        knownFormats.addAll(BUILTIN_FORMATS);
    }

    /**
     * Applies a filter and using this filter to display formats.
     *
     * @param filterItem the filter
     */
    public void setFilter(FormatFilterItem filterItem) {
        getItems().clear();
        List<FormatItem> filtered = filterItem.filter(allItems);
        getItems().addAll(filtered);
    }

    /**
     * Returns a list of all formats, regardless of displaying status.
     *
     * @return a list of all formats
     */
    public List<FormatItem> getAllItems() {
        return allItems;
    }

    /**
     * Returns a list of current showing formats.
     * <p>
     * This is filtered by the format filter.
     *
     * @return a list of current showing formats
     */
    public List<FormatItem> getShowingItems() {
        return getItems();
    }

    /**
     * Returns a map containing all custom formats.
     * <p>
     * The keys are the extensions and the values are the descriptions
     *
     * @return a map of all custom formats
     */
    public Map<String, String> getCustomFormats() {
        return customFormats;
    }

    /**
     * Sets the custom formats.
     *
     * @param customFormats custom formats
     */
    public void setCustomFormats(Map<String, String> customFormats) {
        this.customFormats = customFormats;
        knownFormats.addAll(customFormats.keySet());
    }

    /**
     * Returns the formats loaded from external jars.
     */
    public Map<String, String> getExtensionFormats() {
        return extensionFormats;
    }

    /**
     * Sets the formats loaded from external jars.
     *
     * @param extensionFormats formats loaded from external jars
     */
    public void setExtensionFormats(Map<String, String> extensionFormats) {
        this.extensionFormats = extensionFormats;
        knownFormats.addAll(extensionFormats.keySet());
    }

    /**
     * Returns a set of all known formats, including custom formats.
     *
     * @return a set of all known formats, including custom formats
     */
    public Set<String> getKnownFormats() {
        return knownFormats;
    }

    /**
     * Returns a set of extensions of selected formats.
     *
     * @return a set of extensions of selected formats
     */
    public Set<String> getSelectedFormats() {
        Set<String> extensions = new HashSet<>();
        for (FormatItem formatItem : allItems) {
            if (formatItem.getCheckBox().isSelected()) extensions.add(formatItem.getExtension());
        }
        return extensions;
    }

    /**
     * This method is just used for restoring selection status of the format table.
     *
     * @param selectedFormats old selected formats
     */
    public void selectFormats(Set<String> selectedFormats) {
        for (FormatItem formatItem : allItems) {
            if (selectedFormats.contains(formatItem.getExtension())) formatItem.getCheckBox().setSelected(true);
        }
    }
}
