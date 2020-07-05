package trashsoftware.deepSearcher2.controllers.settingsPages;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import trashsoftware.deepSearcher2.Main;
import trashsoftware.deepSearcher2.controllers.ConfirmBox;
import trashsoftware.deepSearcher2.controllers.SettingsPanelController;
import trashsoftware.deepSearcher2.util.Configs;

import java.io.IOException;

public class OthersPage extends SettingsPage {

    public OthersPage(SettingsPanelController controller) throws IOException {
        super(controller);

        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/trashsoftware/deepSearcher2/fxml/settingsPages/others.fxml"),
                Main.getBundle());
        loader.setRoot(this);
        loader.setController(this);

        loader.load();
    }

    @Override
    public void saveChanges() {

    }

    private void showConfirm(String msg, String confirmButtonText, Runnable onConfirm) {
        ConfirmBox confirmBox = ConfirmBox.createConfirmBox(this, getController().getStage());
        confirmBox.setConfirmButtonText(confirmButtonText);
        confirmBox.setMessage(msg);
        confirmBox.setOnConfirmed(onConfirm);

        confirmBox.show();
    }

    @FXML
    void clearHistory() {
        showConfirm(Main.getBundle().getString("confirmClearHistory"),
                Main.getBundle().getString("clear"),
                Configs::clearAllHistory);
    }

    @FXML
    void restoreSettings() {
        showConfirm(Main.getBundle().getString("confirmRestoreSettings") +
                        "\n" + Main.getBundle().getString("needRestart"),
                Main.getBundle().getString("clear"),
                Configs::clearAllHistory);
    }

    @FXML
    void clearAllData() {
        showConfirm(Main.getBundle().getString("confirmClearData") +
                        "\n" + Main.getBundle().getString("needRestart"),
                Main.getBundle().getString("clear"),
                Configs::clearAllHistory);
    }

    @FXML
    void clearCache() {
        Configs.clearCache();
    }
}
