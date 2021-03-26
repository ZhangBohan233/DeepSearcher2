package trashsoftware.deepSearcher2.controllers.settingsPages;

import dsApi.api.FileFormatReader;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.json.JSONObject;
import trashsoftware.deepSearcher2.controllers.Client;
import trashsoftware.deepSearcher2.controllers.SettingsPanelController;
import trashsoftware.deepSearcher2.controllers.ConfirmBox;
import trashsoftware.deepSearcher2.extensionLoader.ExtensionJar;
import trashsoftware.deepSearcher2.extensionLoader.ExtensionLoader;
import trashsoftware.deepSearcher2.util.Cache;
import trashsoftware.deepSearcher2.util.CacheObservable;
import trashsoftware.deepSearcher2.util.Configs;
import trashsoftware.deepSearcher2.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExtensionManagerPage extends SettingsPage implements CacheObservable {
    private final TreeItem<ExtensionItem> rootItem = new TreeItem<>(new RootItem());
    @FXML
    Button uninstallButton;
    @FXML
    TreeTableView<ExtensionItem> extensionTable;
    @FXML
    TreeTableColumn<ExtensionItem, CheckBox> extCheckBoxCol;
    @FXML
    TreeTableColumn<ExtensionItem, String> extJarNameCol, extDescriptionCol;
    private final List<CheckBox> checkBoxes = new ArrayList<>();
    private File jarDialogInit;

    public ExtensionManagerPage(SettingsPanelController controller) throws IOException {
        super(controller);

        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/trashsoftware/deepSearcher2/fxml/settingsPages/extensionManager.fxml"),
                Client.getBundle());
        loader.setRoot(this);
        loader.setController(this);

        loader.load();

        extensionTable.setRoot(rootItem);
        setTableListeners();
        refreshExtensions();

        loadFromCache(Cache.getCache());
    }

    @FXML
    void installExtension() {
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter filter =
                new FileChooser.ExtensionFilter(
                        Client.getBundle().getString("jarDescription"), "*.jar");
        chooser.setSelectedExtensionFilter(filter);
        chooser.setInitialDirectory(jarDialogInit);

        File jarFile = chooser.showOpenDialog(getController().getStage());
        if (jarFile != null) {
            jarDialogInit = jarFile.getParentFile();
            String destPath = ExtensionLoader.EXT_JAR_DIR + File.separator + jarFile.getName();
            if (!Util.copyFile(destPath, jarFile)) {
                ConfirmBox infoBox = ConfirmBox.createInfoBox(
                        getController().getStage(), Client.getBundle().getString("error"));
                infoBox.setConfirmButtonText(Client.getBundle().getString("confirm"));
                infoBox.setMessage(Client.getBundle().getString("cannotInstall"));
                infoBox.show();
            } else {
                refreshExtensions();
            }
        }
    }

    @FXML
    void uninstallExtension() {
        ConfirmBox confirmBox = ConfirmBox.createConfirmBox(getController().getStage());
        confirmBox.setMessage(Client.getBundle().getString("confirmUninstall"));
        confirmBox.setConfirmButtonText(Client.getBundle().getString("confirm"));
        confirmBox.setOnConfirmed(this::uninstallSelectedJar);
        confirmBox.show();
    }

    private void uninstallSelectedJar() {
        JarItem selected = (JarItem) extensionTable.getSelectionModel().getSelectedItem().getValue();
        String path = selected.extensionJar.getPathName();
        File file = new File(path);
        if (!file.delete()) {
            ConfirmBox infoBox = ConfirmBox.createInfoBox(
                    getController().getStage(), Client.getBundle().getString("error"));
            infoBox.setConfirmButtonText(Client.getBundle().getString("confirm"));
            infoBox.setMessage(Client.getBundle().getString("cannotUninstall"));
            infoBox.show();
        }
        refreshExtensions();
    }

    @Override
    public void saveChanges() {
        Set<String> selected = new HashSet<>();
        for (TreeItem<ExtensionItem> treeItem : rootItem.getChildren()) {
            ExtensionItem ei = treeItem.getValue();
            if (ei instanceof JarItem) {
                if (ei.getBox().isSelected()) selected.add(ei.getName());
            }
        }
        Configs.getConfigs().setEnabledJars(selected);
        getController().getMainView().refreshFormatTable();
    }

    public void refreshExtensions() {
        clearCheckBoxes();
        List<ExtensionJar> extensionJars = ExtensionLoader.listExternalJars();
        Set<String> enabledJars = Configs.getConfigs().getEnabledJars();
        for (ExtensionJar jar : extensionJars) {
            ExtensionItem item = new JarItem(jar);
            addCheckBox(item.getBox());
            if (enabledJars.contains(item.getName())) {
                item.getBox().setSelected(true);
            }
            getStatusSaver().store(item.getBox());
            TreeItem<ExtensionItem> treeItem = new TreeItem<>(item);
            List<FileFormatReader> formatReaders = ExtensionLoader.createFormatReaderInstances(jar);
            if (!formatReaders.isEmpty()) {
                for (FileFormatReader formatReader : formatReaders) {
                    treeItem.getChildren().add(new TreeItem<>(new FormatItem(formatReader)));
                }
            }
            rootItem.getChildren().add(treeItem);
        }
        rootItem.setExpanded(true);
    }

    private void clearCheckBoxes() {
        for (CheckBox checkBox : checkBoxes) {
            getController().removeControl(checkBox);
            getStatusSaver().remove(checkBox);
        }
        checkBoxes.clear();
        rootItem.getChildren().clear();
    }

    private void addCheckBox(CheckBox checkBox) {
        checkBoxes.add(checkBox);
        getController().addControls(checkBox);
    }

    private void setTableListeners() {
        extCheckBoxCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getValue().getBox()));
        extJarNameCol.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getValue().getName()));
        extDescriptionCol.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getValue().getDescription()));
        extensionTable.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) ->
                uninstallButton.setDisable(!(newValue != null && newValue.getValue() instanceof JarItem))));
    }

    @Override
    public void putCache(JSONObject rootObject) {
        if (jarDialogInit != null) rootObject.put("jarDialogInit", jarDialogInit.getAbsolutePath());
    }

    @Override
    public void loadFromCache(Cache cache) {
        String initD = cache.getStringCache("jarDialogInit");
        if (initD != null) jarDialogInit = new File(initD);
    }

    public abstract static class ExtensionItem {
        @FXML
        abstract CheckBox getBox();

        @FXML
        abstract String getName();

        @FXML
        String getDescription() {
            return "";
        }
    }

    public static class JarItem extends ExtensionItem {
        private final ExtensionJar extensionJar;
        private final CheckBox enableBox = new CheckBox();

        private JarItem(ExtensionJar extensionJar) {
            this.extensionJar = extensionJar;
        }

        @Override
        CheckBox getBox() {
            return enableBox;
        }

        String getName() {
            return extensionJar.getJarName();
        }
    }

    public static class FormatItem extends ExtensionItem {
        private final FileFormatReader formatReader;

        private FormatItem(FileFormatReader formatReader) {
            this.formatReader = formatReader;
        }

        @Override
        CheckBox getBox() {
            return null;
        }

        @Override
        String getName() {
            return String.format(Client.getBundle().getString("thirdPartyReaderFmt"),
                    String.join(", ", formatReader.extensions()));
        }

        @Override
        String getDescription() {
            return formatReader.description(Configs.getConfigs().getCurrentLocale());
        }
    }

    public class RootItem extends ExtensionItem {
        private final CheckBox rootBox = new CheckBox();

        private RootItem() {
            rootBox.selectedProperty().addListener(((observable, oldValue, newValue) -> {
                for (TreeItem<ExtensionItem> child : rootItem.getChildren()) {
                    child.getValue().getBox().setSelected(newValue);
                }
            }));
        }

        @Override
        CheckBox getBox() {
            return rootBox;
        }

        @Override
        String getName() {
            return Client.getBundle().getString("installedExtensions");
        }
    }
}
