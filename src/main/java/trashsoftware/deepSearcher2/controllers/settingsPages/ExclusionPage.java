package trashsoftware.deepSearcher2.controllers.settingsPages;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import trashsoftware.deepSearcher2.Main;
import trashsoftware.deepSearcher2.controllers.SettingsPanelController;
import trashsoftware.deepSearcher2.controllers.widgets.FormatInputBox;
import trashsoftware.deepSearcher2.util.Configs;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.Set;

public class ExclusionPage extends SettingsPage {

    @FXML
    ListView<String> excludedDirList, excludedFormatList;

    @FXML
    Button deleteDirButton, deleteFormatButton;

    public ExclusionPage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/trashsoftware/deepSearcher2/fxml/settingsPages/exclusion.fxml"),
                Main.getBundle());
        loader.setRoot(this);
        loader.setController(this);

        loader.load();

        addDirListListener();
        addFormatListListener();

        refreshDirList();
        refreshFormatsList();
    }

    private void addDirListListener() {
        excludedDirList.getSelectionModel().selectedIndexProperty().addListener(((observableValue, number, t1) -> {
            if (t1.intValue() == -1) {
                deleteDirButton.setDisable(true);
            } else {
                deleteDirButton.setDisable(false);
            }
        }));
    }

    private void addFormatListListener() {
        excludedFormatList.getSelectionModel().selectedIndexProperty().addListener(((observableValue, number, t1) -> {
            if (t1.intValue() == -1) {
                deleteFormatButton.setDisable(true);
            } else {
                deleteFormatButton.setDisable(false);
            }
        }));
    }

    @Override
    public void saveChanges() {

    }

    @FXML
    void addExcludedDir() {
        File file = new DirectoryChooser().showDialog(null);
        if (file != null) {
            Configs.addExcludedDir(file.getAbsolutePath());
            refreshDirList();
        }
    }

    @FXML
    void deleteExcludedDir() {
        int index = excludedDirList.getSelectionModel().getSelectedIndex();
        String removed = excludedDirList.getItems().remove(index);
        Configs.removeExcludedDir(removed);
        refreshDirList();
    }

    @FXML
    void addExcludedFormat() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/trashsoftware/deepSearcher2/fxml/widgets/formatInputBox.fxml"),
                Main.getBundle());
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle(Main.getBundle().getString("excludedFormats"));
        stage.initStyle(StageStyle.UTILITY);
        stage.setScene(new Scene(root));

        FormatInputBox fib = loader.getController();
        fib.setParentAndStage(this, stage);

        stage.show();
    }

    @FXML
    void deleteExcludedFormat() {
        int index = excludedFormatList.getSelectionModel().getSelectedIndex();
        String removed = excludedFormatList.getItems().remove(index);
        Configs.removeExcludedFormat(removed);
        refreshFormatsList();
    }

    public void addFormat(String extension) {
        String pureExt;
        if (extension.startsWith(".")) {
            pureExt = extension.substring(1);
        } else {
            pureExt = extension;
        }
        Configs.addExcludedFormat(pureExt);
        refreshFormatsList();
    }

    private void refreshDirList() {
        excludedDirList.getItems().clear();
        Set<String> dirs = Configs.getAllExcludedDirs();
        excludedDirList.getItems().addAll(dirs);
    }

    private void refreshFormatsList() {
        excludedFormatList.getItems().clear();
        Set<String> formats = Configs.getAllExcludedFormats();
        for (String f : formats) {
            excludedFormatList.getItems().add("." + f);
        }
    }
}
