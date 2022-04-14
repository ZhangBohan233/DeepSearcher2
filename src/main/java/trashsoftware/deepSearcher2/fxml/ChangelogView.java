package trashsoftware.deepSearcher2.fxml;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import trashsoftware.deepSearcher2.doc.ChangelogLoader;
import trashsoftware.deepSearcher2.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.ResourceBundle;

public class ChangelogView implements Initializable {
    @FXML
    TextArea changelogText;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fillChangelog();
    }
    
    private void fillChangelog() {
        URL uu = getClass().getResource("/trashsoftware/deepSearcher2/doc/changelog.txt");
        System.out.println(uu);
        changelogText.setText(ChangelogLoader.loadChangelog());
    }
}
