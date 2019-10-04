package trashsoftware.deepSearcher2.controllers.settingsPages;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;

import java.util.ArrayList;
import java.util.List;

public abstract class SettingsPage extends Page {

    private List<ComboBox> comboBoxes = new ArrayList<>();
    private List<CheckBox> checkBoxes = new ArrayList<>();

    StatusSaver statusSaver = new StatusSaver();

    public abstract void saveChanges();

    public void setApplyButtonStatusChanger(Button applyButton) {
        for (ComboBox comboBox : comboBoxes) {
            comboBox.getSelectionModel().selectedIndexProperty().addListener(((observableValue, number, t1) -> {
                if (isAnyStatusChanged()) applyButton.setDisable(false);
                else applyButton.setDisable(true);
            }));
        }
        for (CheckBox checkBox : checkBoxes) {
            checkBox.selectedProperty().addListener(((observableValue, aBoolean, t1) -> {
                if (isAnyStatusChanged()) applyButton.setDisable(false);
                else applyButton.setDisable(true);
            }));
        }
    }

    boolean isAnyStatusChanged() {
        for (ComboBox comboBox : comboBoxes) {
            if (statusSaver.hasChanged(comboBox)) return true;
        }
        for (CheckBox checkBox : checkBoxes) {
            if (statusSaver.hasChanged(checkBox)) return true;
        }
        return false;
    }

    /**
     * Adds all controllable {@code Control}'s to page.
     *
     * This method should be called just after {@code FXMLLoader.load} in the constructor of any sub-classes of this.
     *
     * @param controls array of controllable {@code Control}'s
     */
    void addControls(Control... controls) {
        for (Control control : controls) {
            if (control instanceof ComboBox) comboBoxes.add((ComboBox) control);
            else if (control instanceof CheckBox) checkBoxes.add((CheckBox) control);

            else throw new RuntimeException("Unrecognizable Control");
        }
    }
}
