package trashsoftware.deepSearcher2.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import trashsoftware.deepSearcher2.controllers.settingsPages.GeneralPage;
import trashsoftware.deepSearcher2.controllers.settingsPages.Page;
import trashsoftware.deepSearcher2.controllers.settingsPages.SettingsMainPage;
import trashsoftware.deepSearcher2.controllers.settingsPages.SettingsPage;
import trashsoftware.deepSearcher2.items.SettingsItem;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsPanelController implements Initializable {

    @FXML
    TreeView<SettingsItem> treeView;

    @FXML
    ScrollPane contentPane;

    @FXML
    Button okButton, cancelButton, applyButton;

    private Stage thisStage;
    private ResourceBundle bundle;

    private GeneralPage generalPage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bundle = resourceBundle;

        setTreeViewListener();
        setUpItems();

        contentPane.setFitToWidth(true);
    }

    void setStage(Stage stage) {
        this.thisStage = stage;
    }

    @FXML
    void cancelAction() {
        closeWindow();
    }

    private void closeWindow() {
        thisStage.close();
    }

    private void setUpItems() {
        TreeItem<SettingsItem> root = new TreeItem<>();

        try {
            SettingsMainPage smp = new SettingsMainPage(bundle, this);
            root.setValue(new SettingsItem(bundle.getString("settings"), smp));

            generalPage = new GeneralPage(bundle);
            root.getChildren().add(new TreeItem<>(new SettingsItem(bundle.getString("general"), generalPage)));

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        treeView.setRoot(root);
    }

    private void setTreeViewListener() {
        treeView.getSelectionModel().selectedItemProperty()
                .addListener((observableValue, settingsItemTreeItem, t1) -> {
                    showPage(t1.getValue().getPage());
                });
    }

    private void showPage(Page page) {
        if (page instanceof SettingsPage) {
            SettingsPage settingsPage = (SettingsPage) page;
            ((SettingsPage) page).setApplyButtonStatusChanger(applyButton);
            applyButton.setOnAction(e -> {
                settingsPage.saveChanges();
                applyButton.setDisable(true);
            });
            okButton.setOnAction(e -> {
                settingsPage.saveChanges();
                closeWindow();
            });
        } else {
            applyButton.setDisable(true);
        }
        contentPane.setContent(page);
    }

    public void toGeneralPage() {
        showPage(generalPage);
    }
}
