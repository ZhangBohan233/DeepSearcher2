package trashsoftware.deepSearcher2.controllers.settingsPages;

import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.ResourceBundle;

public class GeneralPage extends SettingsPage {
    public GeneralPage(ResourceBundle bundle) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/trashsoftware/deepSearcher2/fxml/settingsPages/general.fxml"), bundle);
        loader.setRoot(this);
        loader.setController(this);

        loader.load();
    }
}
