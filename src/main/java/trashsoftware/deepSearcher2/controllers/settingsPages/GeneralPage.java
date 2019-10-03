package trashsoftware.deepSearcher2.controllers.settingsPages;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import trashsoftware.deepSearcher2.util.Configs;
import trashsoftware.deepSearcher2.util.NamedLocale;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

public class GeneralPage extends SettingsPage {

    @FXML
    ComboBox<NamedLocale> languageBox;

    private ResourceBundle bundle;

    public GeneralPage(ResourceBundle bundle) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/trashsoftware/deepSearcher2/fxml/settingsPages/general.fxml"), bundle);
        loader.setRoot(this);
        loader.setController(this);

        loader.load();

        this.bundle = bundle;

        initLanguageBox();
    }

    @Override
    public void saveChanges() {
        NamedLocale selectedLocale = languageBox.getSelectionModel().getSelectedItem();
        Configs.writeConfig("locale", selectedLocale.getConfigValue());
    }

    @Override
    public void setApplyButtonStatusChanger(Button applyButton) {
        languageBox.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> {
            if (number.intValue() != t1.intValue()) applyButton.setDisable(false);
        });
    }

    private void initLanguageBox() {
        List<NamedLocale> localeList = Configs.getAllLocales();
        for (NamedLocale locale : localeList) {
            languageBox.getItems().add(locale);
            if (bundle.getLocale().equals(locale.getLocale())) {
                languageBox.getSelectionModel().selectLast();
            }
        }
    }
}
