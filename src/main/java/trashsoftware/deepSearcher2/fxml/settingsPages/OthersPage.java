package trashsoftware.deepSearcher2.fxml.settingsPages;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import trashsoftware.deepSearcher2.fxml.Client;
import trashsoftware.deepSearcher2.fxml.ConfirmBox;
import trashsoftware.deepSearcher2.fxml.SettingsPanelController;
import trashsoftware.deepSearcher2.util.Cache;
import trashsoftware.deepSearcher2.util.Configs;

import java.io.IOException;

public class OthersPage extends SettingsPage {
    
    @FXML
    ComboBox<Integer> promptsNumberBox;

    public OthersPage(SettingsPanelController controller) throws IOException {
        super(controller);

        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/trashsoftware/deepSearcher2/fxml/settingsPages/others.fxml"),
                Client.getBundle());
        loader.setRoot(this);
        loader.setController(this);

        loader.load();
        
        controller.addControls(promptsNumberBox);
        
        setPromptsNumberBox();
    }
    
    private void setPromptsNumberBox() {
        promptsNumberBox.getItems().addAll(
                1, 3, 5, 10, 20
        );
        promptsNumberBox.getSelectionModel().select(
                Integer.valueOf(Configs.getConfigs().getMaxSearchPrompt()));
        getStatusSaver().store(promptsNumberBox);
    }

    @Override
    public void saveChanges() {
        if (getStatusSaver().hasChanged(promptsNumberBox)) {
            Configs.getConfigs().setMaxSearchPrompt(promptsNumberBox.getValue());
            getStatusSaver().store(promptsNumberBox);
        }
    }

    private void showConfirm(String msg, String confirmButtonText, Runnable onConfirm) {
        ConfirmBox confirmBox = ConfirmBox.createConfirmBox(getController().getStage());
        confirmBox.setConfirmButtonText(confirmButtonText);
        confirmBox.setMessage(msg);
        confirmBox.setOnConfirmed(onConfirm);

        confirmBox.show();
    }

    @FXML
    void clearHistory() {
        showConfirm(Client.getBundle().getString("confirmClearHistory"),
                Client.getBundle().getString("clear"),
                Configs::clearAllHistory);
    }

    @FXML
    void restoreSettings() {
        showConfirm(Client.getBundle().getString("confirmRestoreSettings") +
                        "\n" + Client.getBundle().getString("needRestart"),
                Client.getBundle().getString("clear"),
                Configs.getConfigs()::clearSettings);
    }

    @FXML
    void clearAllData() {
        showConfirm(Client.getBundle().getString("confirmClearData") +
                        "\n" + Client.getBundle().getString("needRestart"),
                Client.getBundle().getString("clear"),
                Configs.getConfigs()::clearAllData);
    }

    @FXML
    void clearCache() {
        Cache.clearCache();
    }
}
