package trashsoftware.deepSearcher2.controllers.widgets;

import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import trashsoftware.deepSearcher2.guiItems.FormatFilterItem;
import trashsoftware.deepSearcher2.guiItems.FormatItem;

import java.util.ArrayList;
import java.util.List;

public class FormatTable extends TableView<FormatItem> {

    private final List<FormatItem> allItems = new ArrayList<>();

    public void addItem(FormatItem formatItem) {
        allItems.add(formatItem);
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
}
