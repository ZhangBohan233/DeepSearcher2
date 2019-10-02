package trashsoftware.deepSearcher2.controllers.widgets;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TextFieldList extends ScrollPane {

    @FXML
    private VBox baseList;

    private ReadOnlyIntegerWrapper selectedIndexWrapper = new ReadOnlyIntegerWrapper(-1);

    private List<ChangeListener<Boolean>> itemFocusListeners = new ArrayList<>();

    public TextFieldList() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/trashsoftware/deepSearcher2/fxml/textFieldList.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        loader.load();

        setFitToWidth(true);

        initializeListener();
    }

    public ObservableList<Node> getTextFields() {
        return baseList.getChildren();
    }

    public ReadOnlyIntegerProperty selectedIndexProperty() {
        return selectedIndexWrapper;
    }

    public int getSelectedIndex() {
        return selectedIndexWrapper.intValue();
    }

    private void initializeListener() {
        getTextFields().addListener((ListChangeListener<Node>) change -> {
            change.next();
            if (change.wasAdded() && change.getAddedSize() == 1) {
                final int thisIndex = change.getTo() - 1;
                Node added = getTextFields().get(thisIndex);
                ChangeListener<Boolean> focusListener = (observableValue, aBoolean, t1) -> {
                    if (t1) selectedIndexWrapper.setValue(thisIndex);
                };
                added.focusedProperty().addListener(focusListener);
                itemFocusListeners.add(thisIndex, focusListener);
            } else if (change.wasRemoved() && change.getRemovedSize() == 1) {
                final int removedIndex = change.getFrom();
                itemFocusListeners.remove(removedIndex);
                for (int i = removedIndex; i < getTextFields().size(); i++) {  // decreases the index stored in
                    // listeners of every element after the removed item
                    final int index = i;
                    Node node = getTextFields().get(index);
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
