package trashsoftware.deepSearcher2.controllers.settingsPages;

import javafx.scene.control.*;
import trashsoftware.deepSearcher2.controllers.SettingsPanelController;

import java.util.ArrayList;
import java.util.List;

/**
 * A page that has real setting items.
 */
public abstract class SettingsPage extends Page {

    private final List<ComboBox<?>> comboBoxes = new ArrayList<>();
    private final List<CheckBox> checkBoxes = new ArrayList<>();
    private final List<TextField> textFields = new ArrayList<>();

    StatusSaver statusSaver = new StatusSaver();

    public SettingsPage(SettingsPanelController controller) {
        super(controller);
    }

    /**
     * Saves all stated changes to configuration file.
     */
    public abstract void saveChanges();

    /**
     * Sets the enable/disable status listener of apply button.
     * <p>
     * The apply button should be enabled when any managed controls have changed their selection.
     *
     * @param applyButton the apply button
     */
    public void setApplyButtonStatusChanger(Button applyButton) {
        for (ComboBox<?> comboBox : comboBoxes) {
            comboBox.getSelectionModel().selectedIndexProperty().addListener(((observableValue, number, t1) ->
                    applyButton.setDisable(noStatusChanged())));
        }
        for (CheckBox checkBox : checkBoxes) {
            checkBox.selectedProperty().addListener(((observableValue, aBoolean, t1) ->
                    applyButton.setDisable(noStatusChanged())));
        }
        // do not set textfield listeners
    }

    private boolean noStatusChanged() {
        for (ComboBox<?> comboBox : comboBoxes) {
            if (statusSaver.hasChanged(comboBox)) return false;
        }
        for (CheckBox checkBox : checkBoxes) {
            if (statusSaver.hasChanged(checkBox)) return false;
        }
        for (TextField textField : textFields) {
            if (statusSaver.hasChanged(textField)) return false;
        }
        return true;
    }

    /**
     * Adds all controllable {@code Control}'s that need to be monitored for changes to page.
     * <p>
     * This method should be called just after {@code FXMLLoader.load} in the constructor of any sub-classes of this.
     *
     * @param controls array of controllable {@code Control}'s
     */
    void addControls(Control... controls) {
        for (Control control : controls) {
            if (control instanceof ComboBox) comboBoxes.add((ComboBox<?>) control);
            else if (control instanceof CheckBox) checkBoxes.add((CheckBox) control);
            else if (control instanceof TextField) textFields.add((TextField) control);

            else throw new RuntimeException("Unrecognizable Control");
        }
    }
}
