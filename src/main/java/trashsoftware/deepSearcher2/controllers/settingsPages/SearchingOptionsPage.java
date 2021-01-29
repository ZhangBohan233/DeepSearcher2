package trashsoftware.deepSearcher2.controllers.settingsPages;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import trashsoftware.deepSearcher2.controllers.Client;
import trashsoftware.deepSearcher2.controllers.SettingsPanelController;
import trashsoftware.deepSearcher2.util.Configs;

import java.io.IOException;

public class SearchingOptionsPage extends SettingsPage {

    @FXML
    CheckBox includePathNameBox;
    @FXML
    CheckBox shownHiddenBox;
    @FXML
    CheckBox limitDepthBox;
    @FXML
    TextField searchDepthField;

    public SearchingOptionsPage(SettingsPanelController controller) throws IOException {
        super(controller);

        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/trashsoftware/deepSearcher2/fxml/settingsPages/searchingOptions.fxml"),
                Client.getBundle());
        loader.setRoot(this);
        loader.setController(this);

        loader.load();

        addControls(includePathNameBox, shownHiddenBox, limitDepthBox, searchDepthField);

        initCheckBoxes();
        initMaxDepthBox();
    }

    @Override
    public void saveChanges() {
        if (statusSaver.hasChanged(includePathNameBox)) {
            Configs.setIncludePathName(includePathNameBox.isSelected());
            statusSaver.store(includePathNameBox);
        }
        if (statusSaver.hasChanged(shownHiddenBox)) {
            Configs.setShowHidden(shownHiddenBox.isSelected());
            statusSaver.store(shownHiddenBox);
        }
        if (statusSaver.hasChanged(limitDepthBox)) {
            Configs.setLimitDepth(limitDepthBox.isSelected());
            statusSaver.store(limitDepthBox);
        }
        if (limitDepthBox.isSelected()) {
            try {
                Configs.setMaxSearchDepth(Integer.parseInt(searchDepthField.getText()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            statusSaver.store(searchDepthField);
        }
    }

    private void initCheckBoxes() {
        includePathNameBox.setSelected(Configs.isIncludePathName());
        shownHiddenBox.setSelected(Configs.isShowHidden());
        statusSaver.store(includePathNameBox);
        statusSaver.store(shownHiddenBox);
    }

    private void initMaxDepthBox() {
        limitDepthBox.selectedProperty().addListener(((observable, oldValue, newValue) ->
                searchDepthField.setDisable(!newValue)));

        searchDepthField.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue.length() > 0) {
                try {
                    int r = Integer.parseInt(newValue);
                    if (r > 0) {
                        searchDepthField.setText(newValue);
                    } else {
                        searchDepthField.setText(oldValue);
                    }
                } catch (NumberFormatException e) {
                    searchDepthField.setText(oldValue);
                }
            }
        }));
        searchDepthField.setText(String.valueOf(Configs.getMaxSearchDepth()));
        limitDepthBox.setSelected(Configs.isLimitDepth());

        statusSaver.store(limitDepthBox);
        statusSaver.store(searchDepthField);
    }
}
