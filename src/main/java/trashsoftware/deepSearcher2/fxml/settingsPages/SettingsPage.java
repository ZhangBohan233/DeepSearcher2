package trashsoftware.deepSearcher2.fxml.settingsPages;

import trashsoftware.deepSearcher2.fxml.SettingsPanelController;

/**
 * A page that has real setting items.
 */
public abstract class SettingsPage extends Page {

    public SettingsPage(SettingsPanelController controller) {
        super(controller);
    }

    /**
     * Saves all stated changes to configuration file.
     */
    public abstract void saveChanges();

    /**
     * @return the status saver of the whole setting panel.
     */
    protected StatusSaver getStatusSaver() {
        return getController().getStatusSaver();
    }
}
