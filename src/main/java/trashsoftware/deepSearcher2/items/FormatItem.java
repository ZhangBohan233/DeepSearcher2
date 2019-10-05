package trashsoftware.deepSearcher2.items;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import trashsoftware.deepSearcher2.util.Configs;

public class FormatItem {

    private String extension;
    private String description;
    private CheckBox checkBox = new CheckBox();

    public FormatItem(String extension, String description) {
        this.extension = extension;
        this.description = description;

        addCheckBoxListener();
    }

    private void addCheckBoxListener() {
        checkBox.selectedProperty().addListener(((observableValue, aBoolean, t1) -> {
            if (t1) Configs.addFormat(extension);
            else Configs.removeFormat(extension);
        }));
    }

    @FXML
    public String getDescription() {
        return description;
    }

    @FXML
    public String getDottedExtension() {
        return "." + extension;
    }

    @FXML
    public CheckBox getCheckBox() {
        return checkBox;
    }

    public String getExtension() {
        return extension;
    }
}
