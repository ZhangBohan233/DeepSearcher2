package trashsoftware.deepSearcher2.fxml.settingsPages;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import trashsoftware.deepSearcher2.fxml.Client;
import trashsoftware.deepSearcher2.fxml.SettingsPanelController;
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

        controller.addControls(includePathNameBox, shownHiddenBox, limitDepthBox, searchDepthField);

        initCheckBoxes();
        initMaxDepthBox();
    }

    @Override
    public void saveChanges() {
        if (getStatusSaver().hasChanged(includePathNameBox)) {
            Configs.getConfigs().setIncludePathName(includePathNameBox.isSelected());
            getStatusSaver().store(includePathNameBox);
        }
        if (getStatusSaver().hasChanged(shownHiddenBox)) {
            Configs.getConfigs().setShowHidden(shownHiddenBox.isSelected());
            getStatusSaver().store(shownHiddenBox);
        }
        if (getStatusSaver().hasChanged(limitDepthBox)) {
            Configs.getConfigs().setLimitDepth(limitDepthBox.isSelected());
            getStatusSaver().store(limitDepthBox);
        }
        if (limitDepthBox.isSelected()) {
            try {
                Configs.getConfigs().setMaxSearchDepth(Integer.parseInt(searchDepthField.getText()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            getStatusSaver().store(searchDepthField);
        }
    }

    private void initCheckBoxes() {
        includePathNameBox.setSelected(Configs.getConfigs().isIncludePathName());
        shownHiddenBox.setSelected(Configs.getConfigs().isShowHidden());
        getStatusSaver().store(includePathNameBox);
        getStatusSaver().store(shownHiddenBox);
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
        searchDepthField.setText(String.valueOf(Configs.getConfigs().getMaxSearchDepth()));
        limitDepthBox.setSelected(Configs.getConfigs().isLimitDepth());

        getStatusSaver().store(limitDepthBox);
        getStatusSaver().store(searchDepthField);
    }
}
