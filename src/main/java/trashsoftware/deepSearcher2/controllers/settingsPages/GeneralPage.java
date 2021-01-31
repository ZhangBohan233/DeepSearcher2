package trashsoftware.deepSearcher2.controllers.settingsPages;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Font;
import trashsoftware.deepSearcher2.controllers.Client;
import trashsoftware.deepSearcher2.controllers.ConfirmBox;
import trashsoftware.deepSearcher2.controllers.SettingsPanelController;
import trashsoftware.deepSearcher2.util.Configs;
import trashsoftware.deepSearcher2.util.NamedLocale;

import java.io.IOException;
import java.util.List;

public class GeneralPage extends SettingsPage {

    @FXML
    ComboBox<Integer> fontSizeBox;
    @FXML
    ComboBox<String> fontBox;
    @FXML
    ComboBox<NamedLocale> languageBox;
    @FXML
    CheckBox useCustomFontBox;

    public GeneralPage(SettingsPanelController controller) throws IOException {
        super(controller);

        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/trashsoftware/deepSearcher2/fxml/settingsPages/general.fxml"),
                Client.getBundle());
        loader.setRoot(this);
        loader.setController(this);

        loader.load();
        controller.addControls(languageBox, fontSizeBox, languageBox, useCustomFontBox, fontBox);

        initLanguageBox();
        initUseCusFontBox();
        initFontBoxes();
    }

    @Override
    public void saveChanges() {
        if (getStatusSaver().hasChanged(languageBox)) {
            NamedLocale selectedLocale = languageBox.getSelectionModel().getSelectedItem();
            Configs.getConfigs().writeConfig("locale", selectedLocale.getConfigValue());
            getStatusSaver().store(languageBox);

            askRestart();
        }
        if (getStatusSaver().hasChanged(useCustomFontBox) ||
                getStatusSaver().hasChanged(fontBox) ||
                getStatusSaver().hasChanged(fontSizeBox)) {
            Configs.getConfigs().setUseCustomFont(
                    useCustomFontBox.isSelected(),
                    fontBox.getSelectionModel().getSelectedItem(),
                    fontSizeBox.getSelectionModel().getSelectedItem());
            getStatusSaver().store(useCustomFontBox);
            getStatusSaver().store(fontBox);
            getStatusSaver().store(fontSizeBox);

            askRestart();
        }
    }

    private void askRestart() {
        ConfirmBox confirmBox = ConfirmBox.createConfirmBox(getController().getStage());
        confirmBox.setMessage(Client.getBundle().getString("operationApplyAfterRestart"));
        confirmBox.setOnConfirmed(() -> {
            getController().getStage().close();
            Client.restartClient();
        });
        confirmBox.show();
    }

    private void initLanguageBox() {
        List<NamedLocale> localeList = Configs.getAllLocales();
        for (NamedLocale locale : localeList) {
            languageBox.getItems().add(locale);
            if (Client.getBundle().getLocale().equals(locale.getLocale())) {
                languageBox.getSelectionModel().selectLast();
            }
        }
        getStatusSaver().store(languageBox);
    }

    private void initUseCusFontBox() {
        useCustomFontBox.selectedProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue) {
                fontBox.setDisable(false);
                fontSizeBox.setDisable(false);
            } else {
                fontBox.setDisable(true);
                fontSizeBox.setDisable(true);
            }
        }));
        useCustomFontBox.setSelected(Configs.getConfigs().isUseCustomFont());
        getStatusSaver().store(useCustomFontBox);
    }

    private void initFontBoxes() {
        for (String font : Font.getFamilies()) {
            fontBox.getItems().add(font);
        }
        fontBox.getSelectionModel().select(Configs.getConfigs().getCustomFont());
        if (fontBox.getSelectionModel().getSelectedIndex() == -1) {
            fontBox.getSelectionModel().select(Font.getDefault().getFamily());
        }
        getStatusSaver().store(fontBox);

        fontSizeBox.getItems().addAll(8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 36);
        fontSizeBox.getSelectionModel().select(Integer.valueOf(Configs.getConfigs().getFontSize(12)));
        getStatusSaver().store(fontSizeBox);
    }
}
