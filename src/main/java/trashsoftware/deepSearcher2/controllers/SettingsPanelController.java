package trashsoftware.deepSearcher2.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import trashsoftware.deepSearcher2.controllers.settingsPages.GeneralPage;
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

    private ResourceBundle bundle;

    private SettingsPage generalPage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bundle = resourceBundle;

        setTreeViewListener();
        setUpItems();

        contentPane.setFitToWidth(true);
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

    private void showPage(SettingsPage page) {
        contentPane.setContent(page);
    }

    public void toGeneralPage() {
        showPage(generalPage);
    }
}
