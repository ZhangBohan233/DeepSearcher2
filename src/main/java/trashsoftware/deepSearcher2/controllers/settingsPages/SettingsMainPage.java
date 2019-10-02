package trashsoftware.deepSearcher2.controllers.settingsPages;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import trashsoftware.deepSearcher2.controllers.SettingsPanelController;

import java.io.IOException;
import java.util.ResourceBundle;

public class SettingsMainPage extends SettingsPage {

    private SettingsPanelController parent;

    public SettingsMainPage(ResourceBundle bundle, SettingsPanelController parent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/trashsoftware/deepSearcher2/fxml/settingsPages/settings.fxml"), bundle);
        loader.setRoot(this);
        loader.setController(this);

        loader.load();

        this.parent = parent;
    }

    @FXML
    public void toGeneralPage() {
        parent.toGeneralPage();
    }
}
