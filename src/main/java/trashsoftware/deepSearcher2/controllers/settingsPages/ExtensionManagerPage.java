package trashsoftware.deepSearcher2.controllers.settingsPages;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import trashsoftware.deepSearcher2.controllers.Client;
import trashsoftware.deepSearcher2.controllers.SettingsPanelController;
import trashsoftware.deepSearcher2.extensionLoader.ExtensionJar;
import trashsoftware.deepSearcher2.extensionLoader.ExtensionLoader;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ExtensionManagerPage extends SettingsPage {
    @FXML
    Button uninstallButton;

    @FXML
    TreeTableView<ExtensionItem> extensionTable;

    @FXML
    TreeTableColumn<ExtensionItem, CheckBox> extCheckBoxCol;

    @FXML
    TreeTableColumn<ExtensionItem, String> extJarNameCol;

    private final TreeItem<ExtensionItem> rootItem = new TreeItem<>(new RootItem());

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

    }

    public void refreshExtensions() {
        rootItem.getChildren().clear();
        List<ExtensionJar> extensionJars = ExtensionLoader.listExternalJars();
        for (ExtensionJar jar : extensionJars) {
            ExtensionItem item = new JarItem(jar);
            rootItem.getChildren().add(new TreeItem<>(item));
        }
        rootItem.setExpanded(true);
    }

    private void setTableListeners() {
        extCheckBoxCol.setCellValueFactory(p -> {
            ExtensionItem item = p.getValue().getValue();
            return new ReadOnlyObjectWrapper<>(item.getBox());
        });
        extJarNameCol.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getValue().getName()));
    }

    public abstract static class ExtensionItem {
        @FXML
        abstract CheckBox getBox();

        @FXML
        abstract String getName();
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
            return extensionJar.getFile().getName();
        }
    }

    public static class SubItem extends ExtensionItem {

        @Override
        CheckBox getBox() {
            return null;
        }

        @Override
        String getName() {
            return null;
        }
    }
}
