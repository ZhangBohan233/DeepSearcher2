package trashsoftware.deepSearcher2.controllers.settingsPages;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import trashsoftware.deepSearcher2.controllers.Client;
import trashsoftware.deepSearcher2.controllers.SettingsPanelController;
import trashsoftware.deepSearcher2.util.Configs;

import java.io.IOException;

public class SearchingOptionsPage extends SettingsPage {

    @FXML
    CheckBox includePathNameBox;
    @FXML
    CheckBox shownHiddenBox;

    public SearchingOptionsPage(SettingsPanelController controller) throws IOException {
        super(controller);

        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/trashsoftware/deepSearcher2/fxml/settingsPages/searchingOptions.fxml"),
                Client.getBundle());
        loader.setRoot(this);
        loader.setController(this);

        loader.load();

        addControls(includePathNameBox, shownHiddenBox);

        initCheckBoxes();
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
    }

    private void initCheckBoxes() {
        includePathNameBox.setSelected(Configs.isIncludePathName());
        shownHiddenBox.setSelected(Configs.isShowHidden());
        statusSaver.store(includePathNameBox);
        statusSaver.store(shownHiddenBox);
    }
}
