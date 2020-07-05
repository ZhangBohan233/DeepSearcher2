package trashsoftware.deepSearcher2.items;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import trashsoftware.deepSearcher2.util.Configs;

public class FormatItem implements Comparable<FormatItem> {

    private final String extension;
    private final String description;
    private final CheckBox checkBox = new CheckBox();

    public FormatItem(String extension, String description) {
        this.extension = extension;
        this.description = description;

        addCheckBoxListener();
    }

    private void addCheckBoxListener() {
        checkBox.selectedProperty().addListener(((observableValue, aBoolean, t1) -> {
            if (t1) Configs.addToArrayCacheNoDup(Configs.FORMATS_KEY, extension);
            else Configs.removeFromArrayCache(Configs.FORMATS_KEY, extension);
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

    @Override
    public int compareTo(FormatItem o) {
        return extension.compareTo(o.extension);
    }
}
