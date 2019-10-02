package trashsoftware.deepSearcher2.items;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

public class FormatItem {

    private String extension;
    private String description;
    private CheckBox checkBox = new CheckBox();

    public FormatItem(String extension, String description) {
        this.extension = extension;
        this.description = description;
    }

    @FXML
    public String getDescription() {
        return description;
    }

    @FXML
    public String getExtension() {
        return extension;
    }

    @FXML
    public CheckBox getCheckBox() {
        return checkBox;
    }
}
