package trashsoftware.deepSearcher2.fxml.widgets;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import trashsoftware.deepSearcher2.fxml.Client;
import trashsoftware.deepSearcher2.fxml.settingsPages.FormatInputAble;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FormatInputBox implements Initializable {

    @FXML
    RowConstraints descriptionRow;
    @FXML
    TextField descriptionField;
    @FXML
    TextField extField;
    @FXML
    Label descriptionLabel;

    private FormatInputAble parent;
    private Stage stage;

    public static void showInputBox(FormatInputAble parent, Window ownerWindow, String title, boolean hasDescription)
            throws IOException {
        FXMLLoader loader = new FXMLLoader(
                FormatInputBox.class.getResource("/trashsoftware/deepSearcher2/fxml/widgets/formatInputBox.fxml"),
                Client.getBundle());
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.initOwner(ownerWindow);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.setScene(new Scene(root));

        FormatInputBox fib = loader.getController();
        fib.setParentAndStage(parent, stage, hasDescription);

        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @FXML
    void okAction() {
        String ext = extField.getText();
        String des = descriptionField.getText();
        if (ext.length() > 0) {
            parent.addFormat(ext, des);
            stage.close();
        } else {
            // TODO: alert
        }
    }

    @FXML
    void cancelAction() {
        stage.close();
    }

    private void setParentAndStage(FormatInputAble parent, Stage stage, boolean hasDescription) {
        this.parent = parent;
        this.stage = stage;
        if (!hasDescription) {
            descriptionRow.setMaxHeight(0);
            descriptionField.setVisible(false);
            descriptionLabel.setVisible(false);
        }
    }
}
