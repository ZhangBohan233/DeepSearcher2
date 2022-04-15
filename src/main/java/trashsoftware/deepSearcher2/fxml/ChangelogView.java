package trashsoftware.deepSearcher2.fxml;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import trashsoftware.deepSearcher2.doc.ChangelogLoader;

import java.net.URL;
import java.util.ResourceBundle;

public class ChangelogView implements Initializable {
    @FXML
    TextArea changelogText;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fillChangelog();
    }
    
    private void fillChangelog() {
        changelogText.setText(ChangelogLoader.loadChangelog());
    }
}
