package trashsoftware.deepSearcher2.controllers.settingsPages;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import trashsoftware.deepSearcher2.Main;
import trashsoftware.deepSearcher2.controllers.Client;
import trashsoftware.deepSearcher2.controllers.SettingsPanelController;
import trashsoftware.deepSearcher2.util.Configs;
import trashsoftware.deepSearcher2.util.NamedLocale;

import java.io.IOException;
import java.util.List;

public class GeneralPage extends SettingsPage {

    @FXML
    ComboBox<NamedLocale> languageBox;

    public GeneralPage(SettingsPanelController controller) throws IOException {
        super(controller);

        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/trashsoftware/deepSearcher2/fxml/settingsPages/general.fxml"),
                Client.getBundle());
        loader.setRoot(this);
        loader.setController(this);

        loader.load();
        addControls(languageBox);

        initLanguageBox();
    }

    @Override
    public void saveChanges() {
        if (statusSaver.hasChanged(languageBox)) {
            NamedLocale selectedLocale = languageBox.getSelectionModel().getSelectedItem();
            Configs.writeConfig("locale", selectedLocale.getConfigValue());
            statusSaver.store(languageBox);

            getController().getStage().close();
            Client.restartClient();
        }
    }

    private void initLanguageBox() {
        List<NamedLocale> localeList = Configs.getAllLocales();
        for (NamedLocale locale : localeList) {
            languageBox.getItems().add(locale);
            if (Client.getBundle().getLocale().equals(locale.getLocale())) {
                languageBox.getSelectionModel().selectLast();
            }
        }
        statusSaver.store(languageBox);
    }
}
