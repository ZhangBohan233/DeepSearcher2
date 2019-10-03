package trashsoftware.deepSearcher2.controllers.settingsPages;

import javafx.scene.control.Button;

public abstract class SettingsPage extends Page {

    public abstract void saveChanges();

    public abstract void setApplyButtonStatusChanger(Button applyButton);
}
