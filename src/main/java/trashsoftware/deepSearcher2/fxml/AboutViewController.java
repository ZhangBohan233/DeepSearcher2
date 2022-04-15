package trashsoftware.deepSearcher2.fxml;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class AboutViewController implements Initializable {
    @FXML
    Label copyrightNameLabel;
    @FXML
    Label copyrightLabel;
    @FXML
    Label versionLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        versionLabel.setText(Client.VERSION);
        String copyrightFormat = resourceBundle.getString("copyrightText");
        String nameText;
        String copyrightText;
        if ("CN".equals(resourceBundle.getLocale().getCountry())) {
            nameText = Client.COPYRIGHT_NAME_ZH;
            copyrightText = String.format(copyrightFormat, Client.COPYRIGHT_YEAR, Client.AUTHOR_ZH);
        } else {
            nameText = Client.COPYRIGHT_NAME_EN;
            copyrightText = String.format(copyrightFormat, Client.COPYRIGHT_YEAR, Client.AUTHOR_EN);
        }
        copyrightNameLabel.setText(nameText);
        copyrightLabel.setText(copyrightText);
    }
}
