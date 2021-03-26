package trashsoftware.deepSearcher2.guiItems;

import trashsoftware.deepSearcher2.controllers.widgets.FormatTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A categorical item of the filter combobox.
 */
public class FormatFilterItem {

    private final FormatTable parent;
    private final FormatType filterType;
    private final String showing;

    public FormatFilterItem(FormatType filterType, String showing, FormatTable parent) {
        this.filterType = filterType;
        this.showing = showing;
        this.parent = parent;
    }

    public List<FormatItem> filter(List<FormatItem> allFormats) {
        List<FormatItem> result = new ArrayList<>();
        if (filterType == FormatType.ALL) {
            result.addAll(allFormats);
        } else if (filterType == FormatType.OTHERS) {
            for (FormatItem fi : allFormats) {
                if (!parent.getKnownFormats().contains(fi.getExtension())) result.add(fi);
            }
        } else if (filterType == FormatType.CUSTOMS) {
            for (FormatItem fi : allFormats) {
                if (parent.getCustomFormats().containsKey(fi.getExtension())) result.add(fi);
            }
        } else if (filterType == FormatType.EXTENSIONS) {
            for (FormatItem fi : allFormats) {
                if (parent.getExtensionFormats().containsKey(fi.getExtension())) result.add(fi);
            }
        } else {
            Set<String> fmts = FormatTable.FMT_MAP.get(filterType);
            for (FormatItem fi : allFormats) {
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
