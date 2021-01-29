package trashsoftware.deepSearcher2.controllers.settingsPages;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.Map;

class StatusSaver {

    private final Map<String, Integer> comboBoxesIndexStatus = new HashMap<>();
    private final Map<String, Boolean> checkBoxesStatus = new HashMap<>();
    private final Map<String, String> textFieldStatus = new HashMap<>();

    void store(ComboBox<?> comboBox) {
        comboBoxesIndexStatus.put(comboBox.getId(), comboBox.getSelectionModel().getSelectedIndex());
    }

    void store(CheckBox checkBox) {
        checkBoxesStatus.put(checkBox.getId(), checkBox.isSelected());
    }

    void store(TextField textField) {
        textFieldStatus.put(textField.getId(), textField.getText());
    }

    boolean hasChanged(ComboBox<?> comboBox) {
        Integer storedIndex = comboBoxesIndexStatus.get(comboBox.getId());
        if (storedIndex == null) throw new RuntimeException("Status of ComboBox Not Saved");
        return storedIndex != comboBox.getSelectionModel().getSelectedIndex();
    }

    boolean hasChanged(CheckBox checkBox) {
        Boolean storedBoolean = checkBoxesStatus.get(checkBox.getId());
        if (storedBoolean == null) throw new RuntimeException("Status of CheckBox Not Saved");
        return storedBoolean != checkBox.isSelected();
    }

    boolean hasChanged(TextField textField) {
        String storedValue = textFieldStatus.get(textField.getId());
        if (storedValue == null) throw new RuntimeException("Status of TextField Not Saved");
        return textField.getText().equals(storedValue);
    }
}
