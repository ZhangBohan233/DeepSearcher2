package trashsoftware.deepSearcher2.guiItems;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

public class FormatItem implements Comparable<FormatItem> {

    private final String extension;
    private final String description;
    private final CheckBox checkBox = new CheckBox();

    /**
     * Constructor.
     *
     * @param extension   the file extension without dot
     * @param description the description of this format
     */
    public FormatItem(String extension, String description) {
        this.extension = extension;
        this.description = description;
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

    @Override
    public int compareTo(FormatItem o) {
        return extension.compareTo(o.extension);
    }

    @Override
    public String toString() {
        return "FormatItem{" + extension + ", " + description + '}';
    }
}
