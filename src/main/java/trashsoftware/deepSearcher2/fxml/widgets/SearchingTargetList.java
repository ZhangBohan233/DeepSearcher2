package trashsoftware.deepSearcher2.fxml.widgets;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import trashsoftware.deepSearcher2.util.Configs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SearchingTargetList extends ScrollPane {

    private final ReadOnlyIntegerWrapper selectedIndexWrapper = new ReadOnlyIntegerWrapper(-1);
    private final List<ChangeListener<Boolean>> itemFocusListeners = new ArrayList<>();
    @FXML
    private VBox baseList;

    public SearchingTargetList() throws IOException {
        getStylesheets().add(getUserAgentStylesheet());
        getStyleClass().add("searching-target-list");
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/trashsoftware/deepSearcher2/fxml/widgets/searchingTargetList.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        loader.load();

        setFitToWidth(true);

        initializeListener();
    }

    @Override
    public String getUserAgentStylesheet() {
        return Configs.getConfigs().getStyleSheetPath();
    }

    /**
     * @return an observable list of {@link SearchingTargetBox}
     */
    public ObservableList<Node> getTargetBoxes() {
        return baseList.getChildren();
    }

    public ReadOnlyIntegerProperty selectedIndexProperty() {
        return selectedIndexWrapper;
    }

    public int getSelectedIndex() {
        return selectedIndexWrapper.intValue();
    }

    /**
     * Sets the dropdown menu all boxes in <code>this</code> to the given prompt list.
     *
     * @param prompts collection of prompt texts, 
     *                will appear in the dropdown menu of <code>box</code>
     */
    public void refreshPromptItems(Collection<String> prompts) {
        for (Node node : getTargetBoxes()) {
            refreshPromptItems((SearchingTargetBox) node, prompts);
        }
    }

    /**
     * Sets the dropdown menu of <code>box</code> to the given prompt list.
     * 
     * @param box     the box to be set
     * @param prompts collection of prompt texts, 
     *                will appear in the dropdown menu of <code>box</code>
     */
    public void refreshPromptItems(SearchingTargetBox box, Collection<String> prompts) {
        String value = box.getValue();
        box.getItems().clear();
        for (String p : prompts) {
            box.getItems().add(p);
        }
        box.setValue(value);
    }

    private void initializeListener() {
        getTargetBoxes().addListener((ListChangeListener<Node>) change -> {
            change.next();
            if (change.wasAdded() && change.getAddedSize() == 1) {
                final int thisIndex = change.getTo() - 1;
                Node added = getTargetBoxes().get(thisIndex);
                ChangeListener<Boolean> focusListener = (observableValue, aBoolean, t1) -> {
                    if (t1) selectedIndexWrapper.setValue(thisIndex);
                };
                added.focusedProperty().addListener(focusListener);
                itemFocusListeners.add(thisIndex, focusListener);
            } else if (change.wasRemoved() && change.getRemovedSize() == 1) {
                final int removedIndex = change.getFrom();
                itemFocusListeners.remove(removedIndex);
                for (int i = removedIndex; i < getTargetBoxes().size(); i++) {  // decreases the index stored in
                    // listeners of every element after the removed item
                    final int index = i;
                    Node node = getTargetBoxes().get(index);
                    node.focusedProperty().removeListener(itemFocusListeners.get(index));
                    ChangeListener<Boolean> focusListener = (observableValue, aBoolean, t1) -> {
                        if (t1) selectedIndexWrapper.setValue(index);
                    };
                    node.focusedProperty().addListener(focusListener);
                    itemFocusListeners.set(index, focusListener);
                }
            }
        });
    }
}
