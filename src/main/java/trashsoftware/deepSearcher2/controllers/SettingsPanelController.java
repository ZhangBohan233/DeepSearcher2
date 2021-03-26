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
import java.util.*;

public class SettingsPanelController implements Initializable {

    private final StatusSaver statusSaver = new StatusSaver();
    private final Collection<ComboBox<?>> comboBoxes = new HashSet<>();
    private final Collection<CheckBox> checkBoxes = new HashSet<>();
    private final Collection<TextField> textFields = new HashSet<>();
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

    /**
     * @return the status saver of the whole settings panel.
     */
    public StatusSaver getStatusSaver() {
        return statusSaver;
    }

    /**
     * Adds all controllable {@code Control}'s that need to be monitored for changes to page.
     * <p>
     * This method should be called just after {@code FXMLLoader.load} in the constructor of any sub-classes of this.
     *
     * @param controls array of controllable {@code Control}'s
     * @throws RuntimeException if the control type is not supported
     */
    public void addControls(Control... controls) {
        for (Control control : controls) {
            if (control instanceof ComboBox) comboBoxes.add((ComboBox<?>) control);
            else if (control instanceof CheckBox) checkBoxes.add((CheckBox) control);
            else if (control instanceof TextField) textFields.add((TextField) control);

            else throw new RuntimeException("Unrecognizable Control");
        }
    }

    /**
     * Removes a {@code Control} from the monitoring set.
     *
     * @param control the control to be removed
     * @throws RuntimeException if the control is not managed
     */
    public void removeControl(Control control) {
        if (control instanceof ComboBox) comboBoxes.remove(control);
        else if (control instanceof CheckBox) checkBoxes.remove(control);
        else if (control instanceof TextField) textFields.remove(control);

        else throw new RuntimeException("Unrecognizable Control");
    }

    /**
     * Expands the left tree view until a specific page
     *
     * @param targetPage the target page
     */
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

    @FXML
    void applyAction() {
        applyAllChanges(treeView.getRoot());
        applyButton.setDisable(true);
    }

    @FXML
    void okAction() {
        applyAllChanges(treeView.getRoot());
        closeWindow();
    }

    private void applyAllChanges(TreeItem<SettingsItem> item) {
        Page page = item.getValue().getPage();
        if (page instanceof SettingsPage) {
            ((SettingsPage) page).saveChanges();
        }
        for (TreeItem<SettingsItem> child : item.getChildren()) {
            applyAllChanges(child);
        }
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

            ExtensionManagerPage extensionManagerPage = new ExtensionManagerPage(this);
            root.getChildren().add(new TreeItem<>(
                    new SettingsItem(bundle.getString("extensionProg"), extensionManagerPage)
            ));

            TreeItem<SettingsItem> searchingRoot = new TreeItem<>();
            NavigatorPage searchingMainPage = new NavigatorPage(this);
            searchingRoot.setValue(new SettingsItem(bundle.getString("searchSettings"), searchingMainPage));
            root.getChildren().add(searchingRoot);

            SearchingOptionsPage searchingOptionsPage = new SearchingOptionsPage(this);
            searchingRoot.getChildren().add(new TreeItem<>(
                    new SettingsItem(bundle.getString("searchingOptions"), searchingOptionsPage)
            ));

            CmpFileSearchPage cmpFileSearchPage = new CmpFileSearchPage(this);
            searchingRoot.getChildren().add(new TreeItem<>(
                    new SettingsItem(bundle.getString("cmpFile"), cmpFileSearchPage)
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
            searchingRoot.setExpanded(true);

            OthersPage othersPage = new OthersPage(this);
            root.getChildren().add(new TreeItem<>(
                    new SettingsItem(bundle.getString("others"), othersPage)));
        } catch (IOException ioe) {
            ioe.printStackTrace();
            EventLogger.log(ioe);
        }
        setApplyButtonStatusChanger();
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
        contentPane.setContent(settingsPage);
    }

    /**
     * Sets the enable/disable status listener of apply button.
     * <p>
     * The apply button should be enabled when any managed controls have changed their selection.
     */
    private void setApplyButtonStatusChanger() {
        for (ComboBox<?> comboBox : comboBoxes) {
            comboBox.getSelectionModel().selectedIndexProperty().addListener(((observableValue, number, t1) ->
                    applyButton.setDisable(noStatusChanged())));
        }
        for (CheckBox checkBox : checkBoxes) {
            checkBox.selectedProperty().addListener(((observableValue, aBoolean, t1) ->
                    applyButton.setDisable(noStatusChanged())));
        }
        for (TextField textField : textFields) {
            textField.textProperty().addListener(((observableValue, aBoolean, t1) ->
                    applyButton.setDisable(noStatusChanged())));
        }
        // do not set textfield listeners
    }

    private boolean noStatusChanged() {
        for (ComboBox<?> comboBox : comboBoxes)
            if (statusSaver.hasChanged(comboBox)) return false;
        for (CheckBox checkBox : checkBoxes)
            if (statusSaver.hasChanged(checkBox)) return false;
        for (TextField textField : textFields)
            if (statusSaver.hasChanged(textField)) return false;
        return true;
    }
}
