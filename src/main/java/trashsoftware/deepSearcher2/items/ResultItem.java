package trashsoftware.deepSearcher2.items;

import javafx.fxml.FXML;
import trashsoftware.deepSearcher2.util.Util;

import java.io.File;
import java.util.ResourceBundle;

public class ResultItem {

    public static final int MATCH_NAME = 1;
    public static final int MATCH_CONTENT = 2;

    private File file;
    private int[] matchModes = new int[2];

    private ResourceBundle bundle;
    private ResourceBundle fileTypeBundle;

    public ResultItem(File file, int firstMatchMode, ResourceBundle bundle, ResourceBundle fileTypeBundle) {
        this.file = file;
        this.matchModes[0] = firstMatchMode;
        this.bundle = bundle;
        this.fileTypeBundle = fileTypeBundle;
    }

    public void addMatchMode(int secondMatchMode) {
        this.matchModes[1] = secondMatchMode;
    }

    @FXML
    public String getName() {
        return file.getAbsolutePath();
    }

    @FXML
    public String getMode() {
        return "12";
    }

    @FXML
    public String getSize() {
        return Util.sizeToReadable(file.length());
    }

    @FXML
    public String getType() {
        String name = file.getName();
        int extIndex = name.lastIndexOf(".");
        if (extIndex == -1) {
            return bundle.getString("file");
        }
        String ext = name.substring(extIndex + 1).toLowerCase();
        if (fileTypeBundle.containsKey(ext)) {
            return fileTypeBundle.getString(ext);
        } else {
            return ext.toUpperCase() + " " + bundle.getString("file");
        }
    }
}
