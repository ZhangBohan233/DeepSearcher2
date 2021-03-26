package trashsoftware.deepSearcher2.controllers.settingsPages;

import dsApi.api.FileFormatReader;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import trashsoftware.deepSearcher2.controllers.Client;
import trashsoftware.deepSearcher2.controllers.SettingsPanelController;
import trashsoftware.deepSearcher2.extensionLoader.ExtensionJar;
import trashsoftware.deepSearcher2.extensionLoader.ExtensionLoader;
import trashsoftware.deepSearcher2.util.Configs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExtensionManagerPage extends SettingsPage {
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
    }

    @FXML
    void installExtension() {

    }

    @FXML
    void uninstallExtension() {

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
