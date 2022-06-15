package trashsoftware.deepSearcher2.fxml.settingsPages;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.text.Font;
import javafx.util.Callback;
import trashsoftware.deepSearcher2.fxml.Client;
import trashsoftware.deepSearcher2.fxml.ConfirmBox;
import trashsoftware.deepSearcher2.fxml.SettingsPanelController;
import trashsoftware.deepSearcher2.util.Configs;
import trashsoftware.deepSearcher2.util.NamedLocale;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class GeneralPage extends SettingsPage {

    @FXML
    ComboBox<Integer> fontSizeBox;
    @FXML
    ComboBox<String> fontBox;
    @FXML
    ComboBox<NamedLocale> languageBox;
    @FXML
    ComboBox<ThemeSelector> themeBox;
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
        controller.addControls(languageBox, fontSizeBox, languageBox, themeBox, 
                useCustomFontBox, fontBox);

        initLanguageBox();
        initThemeBox();
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
        if (getStatusSaver().hasChanged(themeBox)) {
            Configs.getConfigs().setTheme(themeBox.getValue().themeName);
            getStatusSaver().store(themeBox);
            
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
    
    private void initThemeBox() {
        themeBox.getItems().addAll(ThemeSelector.values());
        String selected = Configs.getConfigs().getTheme();
        for (int i = 0; i < themeBox.getItems().size(); i++) {
            if (Objects.equals(selected, themeBox.getItems().get(i).themeName)) {
                themeBox.getSelectionModel().select(i);
                break;
            }
        }
        getStatusSaver().store(themeBox);
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
        fontBox.setCellFactory(stringListView -> new FontCell());
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
    
    enum ThemeSelector {
        DEFAULT("default", "themeDefault"),
        DARK("dark", "themeDark");
        
        final String themeName;
        private final String themeNameString;
        
        ThemeSelector(String themeName, String themeNameString) {
            this.themeName = themeName;
            this.themeNameString = themeNameString;
        }

        @Override
        public String toString() {
            return Client.getBundle().getString(themeNameString);
        }
    }
    
    private class FontCell extends ListCell<String> {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            
            setFont(new Font(item, fontSizeBox.getSelectionModel().getSelectedItem()));
            setText(item);
        }
    }
}
