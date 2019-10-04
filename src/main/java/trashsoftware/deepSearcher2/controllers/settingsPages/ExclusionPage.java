package trashsoftware.deepSearcher2.controllers.settingsPages;

import javafx.fxml.FXMLLoader;
import trashsoftware.deepSearcher2.Main;
import trashsoftware.deepSearcher2.controllers.SettingsPanelController;

import java.io.IOException;
import java.util.ResourceBundle;

public class ExclusionPage extends SettingsPage {

    public ExclusionPage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/trashsoftware/deepSearcher2/fxml/settingsPages/exclusion.fxml"),
                Main.getBundle());
        loader.setRoot(this);
        loader.setController(this);

        loader.load();
    }

    @Override
    public void saveChanges() {

    }
}
