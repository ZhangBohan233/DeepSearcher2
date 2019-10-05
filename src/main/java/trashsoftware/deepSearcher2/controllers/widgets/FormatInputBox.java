package trashsoftware.deepSearcher2.controllers.widgets;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import trashsoftware.deepSearcher2.controllers.settingsPages.ExclusionPage;

import java.net.URL;
import java.util.ResourceBundle;

public class FormatInputBox implements Initializable {

    @FXML
    TextField textField;

    private ExclusionPage parent;
    private Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    void okAction() {
        String ext = textField.getText();
        if (ext.length() > 0) {
            parent.addFormat(ext);
            stage.close();
        } else {
            // TODO: alert
        }
    }

    @FXML
    void cancelAction() {
        stage.close();
    }

    public void setParentAndStage(ExclusionPage parent, Stage stage) {
        this.parent = parent;
        this.stage = stage;
    }
}
