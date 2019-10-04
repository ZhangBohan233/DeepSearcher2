package trashsoftware.deepSearcher2.items;

import javafx.fxml.FXML;
import trashsoftware.deepSearcher2.util.Util;

import java.io.File;
import java.util.ResourceBundle;

public class ResultItem {

//    public static final int MATCH_NAME = 1;
//    public static final int MATCH_CONTENT = 2;

    private File file;
    private boolean[] matchModes;

    private ResourceBundle bundle;
    private ResourceBundle fileTypeBundle;

    public ResultItem(File file, boolean matchName, boolean matchContent,
                      ResourceBundle bundle, ResourceBundle fileTypeBundle) {
        this.file = file;
        this.matchModes = new boolean[]{matchName, matchContent};
        this.bundle = bundle;
        this.fileTypeBundle = fileTypeBundle;
    }

    public void addMatchContent() {
        this.matchModes[1] = true;
    }

    public boolean isSameFileAs(File file) {
        return file.equals(this.file);
    }

    @FXML
    public String getName() {
        return file.getAbsolutePath();
    }

    @FXML
    public String getMode() {
        if (matchModes[0]) {
            if (matchModes[1]) {
                return bundle.getString("matchedName") + ", " + bundle.getString("matchedContent");
            } else {
                return bundle.getString("matchedName");
            }
        } else if (matchModes[1]) {
            return bundle.getString("matchedContent");
        } else {
            throw new RuntimeException("Result that does not match any could not be here, must be a bug");
        }
    }

    @FXML
    public String getSize() {
        return Util.sizeToReadable(file.length(), bundle.getString("bytes"));
    }

    @FXML
    public String getType() {
        String name = file.getName();
        String ext = Util.getFileExtension(name);
        if (ext.equals("")) {
            return bundle.getString("file");
        } else if (fileTypeBundle.containsKey(ext)) {
            return fileTypeBundle.getString(ext);
        } else {
            return ext.toUpperCase() + " " + bundle.getString("file");
        }
    }

    public File getFile() {
        return file;
    }
}
