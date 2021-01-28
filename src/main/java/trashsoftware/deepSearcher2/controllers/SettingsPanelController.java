package trashsoftware.deepSearcher2.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import trashsoftware.deepSearcher2.controllers.settingsPages.*;
import trashsoftware.deepSearcher2.guiItems.SettingsItem;
import trashsoftware.deepSearcher2.util.EventLogger;

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
    private MainViewController mainView;
    private ResourceBundle bundle;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bundle = resourceBundle;

        setTreeViewListener();
        setUpItems();

        contentPane.setFitToWidth(true);
    }

    void setStage(Stage stage, MainViewController mainView) {
        this.thisStage = stage;
        this.mainView = mainView;
    }

    public Stage getStage() {
        return thisStage;
    }

    public MainViewController getMainView() {
        return mainView;
    }

    public void expandUntil(Class<? extends Page> targetPage) {
        expandUntil(targetPage, treeView.getRoot());
    }

    private boolean expandUntil(Class<? extends Page> targetPage, TreeItem<SettingsItem> item) {
        if (item.getValue().getPage().getClass() == targetPage) {
            treeView.getSelectionModel().select(item);
            return true;
        }
        for (TreeItem<SettingsItem> child : item.getChildren()) {
            if (expandUntil(targetPage, child)) {
                item.setExpanded(true);
            }
        }
        return false;
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
            NavigatorPage mainPage = new NavigatorPage(this);
            root.setValue(new SettingsItem(bundle.getString("settings"), mainPage));

            GeneralPage generalPage = new GeneralPage(this);
            root.getChildren().add(new TreeItem<>(
                    new SettingsItem(bundle.getString("general"), generalPage)));

            TreeItem<SettingsItem> searchingRoot = new TreeItem<>();
            NavigatorPage searchingMainPage = new NavigatorPage(this);
            searchingRoot.setValue(new SettingsItem(bundle.getString("searchSettings"), searchingMainPage));
            root.getChildren().add(searchingRoot);

            SearchingOptionsPage searchingOptionsPage = new SearchingOptionsPage(this);
            searchingRoot.getChildren().add(new TreeItem<>(
                    new SettingsItem(bundle.getString("searchingOptions"), searchingOptionsPage)
            ));

            ExclusionPage exclusionPage = new ExclusionPage(this);
            searchingRoot.getChildren().add(new TreeItem<>(
                    new SettingsItem(bundle.getString("exclusions"), exclusionPage)
            ));

            CustomPage customPage = new CustomPage(this);
            searchingRoot.getChildren().add(new TreeItem<>(
                    new SettingsItem(bundle.getString("custom"), customPage)
            ));

            AdvancedSearchingPage advancedSearchingPage = new AdvancedSearchingPage(this);
            searchingRoot.getChildren().add(new TreeItem<>(
                    new SettingsItem(bundle.getString("advancedSearching"), advancedSearchingPage)));

            OthersPage othersPage = new OthersPage(this);
            root.getChildren().add(new TreeItem<>(
                    new SettingsItem(bundle.getString("others"), othersPage)));
        } catch (IOException ioe) {
            ioe.printStackTrace();
            EventLogger.log(ioe);
        }
        root.setExpanded(true);
        treeView.setRoot(root);
    }

    private void setTreeViewListener() {
        treeView.getSelectionModel().selectedItemProperty()
                .addListener((observableValue, settingsItemTreeItem, t1) -> {
                    Page page = t1.getValue().getPage();
                    if (page instanceof SettingsPage) {
                        showPage((SettingsPage) page);
                    } else if (page instanceof NavigatorPage) {
                        showNavigatorPage(t1);
                    }
                });
    }

    private void showNavigatorPage(TreeItem<SettingsItem> t1) {
        VBox root = new VBox();
        for (TreeItem<SettingsItem> treeItem : t1.getChildren()) {
            Hyperlink link = new Hyperlink(treeItem.getValue().toString());
            link.setOnAction(e -> treeView.getSelectionModel().select(treeItem));
            root.getChildren().add(link);
        }
        contentPane.setContent(root);
    }

    private void showPage(SettingsPage settingsPage) {
        settingsPage.setApplyButtonStatusChanger(applyButton);
        applyButton.setOnAction(e -> {
            settingsPage.saveChanges();
            applyButton.setDisable(true);
        });
        okButton.setOnAction(e -> {
            settingsPage.saveChanges();
            closeWindow();
        });

        contentPane.setContent(settingsPage);
    }
}
