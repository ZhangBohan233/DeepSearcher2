package trashsoftware.deepSearcher2.controllers.settingsPages;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import trashsoftware.deepSearcher2.controllers.Client;
import trashsoftware.deepSearcher2.controllers.SettingsPanelController;
import trashsoftware.deepSearcher2.searcher.archiveSearchers.ArchiveSearcher;
import trashsoftware.deepSearcher2.util.Configs;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CmpFileSearchPage extends SettingsPage {

    @FXML
    TableView<CmpFormatItem> formatTable;

    @FXML
    TableColumn<CheckBox, CmpFormatItem> checkCol;

    @FXML
    TableColumn<String, CmpFormatItem> formatCol;

    @FXML
    CheckBox enableBox, selectAllBox;

    @FXML
    Label helpLabel;

    public CmpFileSearchPage(SettingsPanelController controller) throws IOException {
        super(controller);

        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/trashsoftware/deepSearcher2/fxml/settingsPages/cmpFileSearch.fxml"),
                Client.getBundle());
        loader.setRoot(this);
        loader.setController(this);

        loader.load();
        controller.addControls(enableBox);

        initTable();
        initBoxes();
    }

    private void initTable() {
        checkCol.setCellValueFactory(new PropertyValueFactory<>("checkBox"));
        formatCol.setCellValueFactory(new PropertyValueFactory<>("extension"));

        for (String format : ArchiveSearcher.COMPRESSED_FORMATS) {
            formatTable.getItems().add(new CmpFormatItem(format));
        }

        Set<String> formats = Configs.getConfigs().getCmpFormats();
        for (CmpFormatItem cfi : formatTable.getItems()) {
            if (formats.contains(cfi.getExtension())) cfi.getCheckBox().setSelected(true);
        }
    }

    private void setEnabled(boolean selected) {
        formatTable.setDisable(!selected);
        selectAllBox.setDisable(!selected);
    }

    private void initBoxes() {
        enableBox.selectedProperty().addListener(((observable, oldValue, newValue) ->
                setEnabled(newValue)));

        selectAllBox.selectedProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue) {
                selectAllBox.setText(Client.getBundle().getString("deselectAll"));
                for (CmpFormatItem cfi : formatTable.getItems()) {
                    cfi.getCheckBox().setSelected(true);
                }
            } else {
                selectAllBox.setText(Client.getBundle().getString("selectAll"));
                for (CmpFormatItem cfi : formatTable.getItems()) {
                    cfi.getCheckBox().setSelected(false);
                }
            }
        }));

        enableBox.setSelected(Configs.getConfigs().isSearchCmpFiles());
        setEnabled(enableBox.isSelected());

        getStatusSaver().store(enableBox);

        Tooltip help = new Tooltip(Client.getBundle().getString("cmpFileHelp"));
        help.setWrapText(true);
        help.setShowDuration(Duration.seconds(10));
        Tooltip.install(helpLabel, help);
    }

    @Override
    public void saveChanges() {
        if (getStatusSaver().hasChanged(enableBox)) {
            Configs.getConfigs().setSearchCmpFiles(enableBox.isSelected());
            getStatusSaver().store(enableBox);
        }

        Set<String> formats = new HashSet<>();
        for (CmpFormatItem cfi : formatTable.getItems()) {
            if (cfi.getCheckBox().isSelected()) formats.add(cfi.getExtension());
        }
        Configs.getConfigs().setCmpFmts(formats);
    }

    public static class CmpFormatItem {
        private final String extension;
        private final CheckBox checkBox = new CheckBox();

        CmpFormatItem(String extension) {
            this.extension = extension;
        }

        @FXML
        public String getExtension() {
            return extension;
        }

        @FXML
        public CheckBox getCheckBox() {
            return checkBox;
        }
    }
}
