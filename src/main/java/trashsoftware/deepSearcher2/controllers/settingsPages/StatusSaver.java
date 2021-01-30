package trashsoftware.deepSearcher2.controllers.settingsPages;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.Map;

/**
 * A class that stores the dynamic selection status of any added controls.
 */
public class StatusSaver {

    private final Map<Integer, Integer> comboBoxesIndexStatus = new HashMap<>();
    private final Map<Integer, Boolean> checkBoxesStatus = new HashMap<>();
    private final Map<Integer, String> textFieldStatus = new HashMap<>();

    /**
     * Stores the current selection status of a {@code ComboBox}
     *
     * @param comboBox the box
     */
    public void store(ComboBox<?> comboBox) {
        comboBoxesIndexStatus.put(System.identityHashCode(comboBox), comboBox.getSelectionModel().getSelectedIndex());
    }

    /**
     * Stores the current selection status of a {@code CheckBox}
     *
     * @param checkBox the box
     */
    public void store(CheckBox checkBox) {
        checkBoxesStatus.put(System.identityHashCode(checkBox), checkBox.isSelected());
    }

    /**
     * Stores the current text of a {@code TextField}
     *
     * @param textField the text field
     */
    public void store(TextField textField) {
        textFieldStatus.put(System.identityHashCode(textField), textField.getText());
    }

    /**
     * Returns whether a {@code ComboBox} has changed its selection status compared to the last stored value.
     *
     * @param comboBox the box
     * @return {@code true} if the box has changed its status
     */
    public boolean hasChanged(ComboBox<?> comboBox) {
        Integer storedIndex = comboBoxesIndexStatus.get(System.identityHashCode(comboBox));
        if (storedIndex == null) throw new RuntimeException("Status of ComboBox Not Saved");
        return storedIndex != comboBox.getSelectionModel().getSelectedIndex();
    }

    /**
     * Returns whether a {@code CheckBox} has changed its selection status compared to the last stored value.
     *
     * @param checkBox the box
     * @return {@code true} if the box has changed its status
     */
    public boolean hasChanged(CheckBox checkBox) {
        Boolean storedBoolean = checkBoxesStatus.get(System.identityHashCode(checkBox));
        if (storedBoolean == null) throw new RuntimeException("Status of CheckBox Not Saved");
        return storedBoolean != checkBox.isSelected();
    }

    /**
     * Returns whether a {@code TextField} has changed its text compared to the last stored value.
     *
     * @param textField the text field
     * @return {@code true} if the text field has changed its status
     */
    public boolean hasChanged(TextField textField) {
        String storedValue = textFieldStatus.get(System.identityHashCode(textField));
        if (storedValue == null) throw new RuntimeException("Status of TextField Not Saved");
        return !textField.getText().equals(storedValue);
    }
}
