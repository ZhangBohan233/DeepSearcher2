package trashsoftware.deepSearcher2.fxml;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class AboutViewController implements Initializable {
    @FXML
    Label authorLabel;
    @FXML
    Label versionLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        versionLabel.setText(Client.VERSION);
        if ("CN".equals(resourceBundle.getLocale().getCountry())) {
            authorLabel.setText(Client.AUTHOR_ZH);
        } else {
            authorLabel.setText(Client.AUTHOR_EN);
        }
    }
}
