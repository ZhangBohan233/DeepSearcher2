package trashsoftware.deepSearcher2.controllers.settingsPages;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import trashsoftware.deepSearcher2.controllers.Client;
import trashsoftware.deepSearcher2.controllers.SettingsPanelController;
import trashsoftware.deepSearcher2.controllers.widgets.FormatInputBox;
import trashsoftware.deepSearcher2.util.Configs;

import java.io.IOException;
import java.util.Map;

public class CustomPage extends SettingsPage implements FormatInputAble {
    @FXML
    TableView<FmtItem> customFmtTable;
    @FXML
    Button deleteFmtButton;

    public CustomPage(SettingsPanelController controller) throws IOException {
        super(controller);

        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/trashsoftware/deepSearcher2/fxml/settingsPages/custom.fxml"),
                Client.getBundle());
        loader.setRoot(this);
        loader.setController(this);

        loader.load();

        setFmtTableFactory();
        refreshTable();
    }

    @Override
    public void saveChanges() {
    }

    @FXML
    void addFmt() throws IOException {
        FormatInputBox.showInputBox(
                this,
                getController().getStage(),
                Client.getBundle().getString("customTextFormats"),
                true);
    }

    @FXML
    void deleteFmt() {
        int index = customFmtTable.getSelectionModel().getSelectedIndex();
        String removedExt = customFmtTable.getItems().remove(index).ext.substring(1);  // also remove the dot
        Configs.removeCustomFormat(removedExt);
        refreshTable();
        getController().getMainView().refreshFormatTable();
    }

    @Override
    public void addFormat(String ext, String description) {
        String pureExt;
        if (ext.startsWith(".")) {
            pureExt = ext.substring(1);
        } else {
            pureExt = ext;
        }
        String realDes;
        if (description.strip().length() == 0) {
            realDes = pureExt.toUpperCase() + " " + Client.getBundle().getString("file");
        } else {
            realDes = description;
        }
        Configs.addCustomFormat(pureExt, realDes);
        refreshTable();
        getController().getMainView().refreshFormatTable();
    }

    private void refreshTable() {
        customFmtTable.getItems().clear();
        Map<String, String> dirs = Configs.getAllCustomFormats();
        for (Map.Entry<String, String> extDes : dirs.entrySet()) {
            customFmtTable.getItems().add(new FmtItem("." + extDes.getKey(), extDes.getValue()));
        }
    }

    private void setFmtTableFactory() {
        TableColumn<FmtItem, ?> desCol = customFmtTable.getColumns().get(0);
        TableColumn<FmtItem, ?> extCol = customFmtTable.getColumns().get(1);

        desCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        extCol.setCellValueFactory(new PropertyValueFactory<>("ext"));

        customFmtTable.getSelectionModel().selectedIndexProperty().addListener(((observableValue, number, t1) ->
                deleteFmtButton.setDisable(t1.intValue() == -1)));
    }

    public static class FmtItem {
        private final String description;
        private final String ext;  // with dot

        private FmtItem(String ext, String description) {
            this.ext = ext;
            this.description = description;
        }

        @FXML
        public String getDescription() {
            return description;
        }

        @FXML
        public String getExt() {
            return ext;
        }
    }
}
