package trashsoftware.deepSearcher2.controllers.settingsPages;

import javafx.scene.control.*;
import trashsoftware.deepSearcher2.controllers.SettingsPanelController;

import java.util.ArrayList;
import java.util.List;

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
