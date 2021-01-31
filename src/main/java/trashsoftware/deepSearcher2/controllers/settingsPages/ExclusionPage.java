package trashsoftware.deepSearcher2.controllers.settingsPages;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;
import trashsoftware.deepSearcher2.controllers.Client;
import trashsoftware.deepSearcher2.controllers.SettingsPanelController;
import trashsoftware.deepSearcher2.controllers.widgets.FormatInputBox;
import trashsoftware.deepSearcher2.util.Configs;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class ExclusionPage extends SettingsPage implements FormatInputAble {

    @FXML
    ListView<String> excludedDirList, excludedFormatList;

    @FXML
    Button deleteDirButton, deleteFormatButton;

    public ExclusionPage(SettingsPanelController controller) throws IOException {
        super(controller);

        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/trashsoftware/deepSearcher2/fxml/settingsPages/exclusion.fxml"),
                Client.getBundle());
        loader.setRoot(this);
        loader.setController(this);

        loader.load();

        addDirListListener();
        addFormatListListener();

        refreshDirList();
        refreshFormatsList();
    }

    private void addDirListListener() {
        excludedDirList.getSelectionModel().selectedIndexProperty().addListener(((observableValue, number, t1) ->
                deleteDirButton.setDisable(t1.intValue() == -1)));
    }

    private void addFormatListListener() {
        excludedFormatList.getSelectionModel().selectedIndexProperty().addListener(((observableValue, number, t1) ->
                deleteFormatButton.setDisable(t1.intValue() == -1)));
    }

    @Override
    public void saveChanges() {
    }

    @FXML
    void addExcludedDir() {
        File file = new DirectoryChooser().showDialog(getController().getStage());
        if (file != null) {
            Configs.getConfigs().addExcludedDir(file.getAbsolutePath());
            refreshDirList();
        }
    }

    @FXML
    void deleteExcludedDir() {
        int index = excludedDirList.getSelectionModel().getSelectedIndex();
        String removed = excludedDirList.getItems().remove(index);
        Configs.getConfigs().removeExcludedDir(removed);
        refreshDirList();
    }

    @FXML
    void addExcludedFormat() throws IOException {
        FormatInputBox.showInputBox(
                this,
                getController().getStage(),
                Client.getBundle().getString("excludedFormats"),
                false);
    }

    @FXML
    void deleteExcludedFormat() {
        int index = excludedFormatList.getSelectionModel().getSelectedIndex();
        String removed = excludedFormatList.getItems().remove(index).substring(1);  // also remove the dot
        Configs.getConfigs().removeExcludedFormat(removed);
        refreshFormatsList();
    }

    @Override
    public void addFormat(String extension, String description) {
        String pureExt;
        if (extension.startsWith(".")) {
            pureExt = extension.substring(1);
        } else {
            pureExt = extension;
        }
        Configs.getConfigs().addExcludedFormat(pureExt);
        refreshFormatsList();
    }

    private void refreshDirList() {
        excludedDirList.getItems().clear();
        Set<String> dirs = Configs.getConfigs().getAllExcludedDirs();
        excludedDirList.getItems().addAll(dirs);
    }

    private void refreshFormatsList() {
        excludedFormatList.getItems().clear();
        Set<String> formats = Configs.getConfigs().getAllExcludedFormats();
        for (String f : formats) {
            excludedFormatList.getItems().add("." + f);
        }
    }
}
