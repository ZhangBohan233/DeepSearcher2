package trashsoftware.deepSearcher2.guiItems;

import javafx.fxml.FXML;
import trashsoftware.deepSearcher2.searcher.ContentResult;
import trashsoftware.deepSearcher2.util.Util;

import java.io.File;
import java.util.Map;
import java.util.ResourceBundle;

public class ResultItem {

    private final File file;
    private final FileSizeItem fileSizeItem;
    private final boolean[] matchModes;
    private final ResourceBundle bundle;
    private final ResourceBundle fileTypeBundle;
    private final Map<String, String> customFormats;
    private ContentResult contentRes;

    private ResultItem(File file,
                       boolean matchName,
                       boolean matchContent,
                       ResourceBundle bundle,
                       ResourceBundle fileTypeBundle,
                       ContentResult contentRes,
                       Map<String, String> customFormats) {
        this.file = file;
        this.matchModes = new boolean[]{matchName, matchContent};
        this.bundle = bundle;
        this.fileTypeBundle = fileTypeBundle;
        this.fileSizeItem = file.isDirectory() ? null : new FileSizeItem(file.length(), bundle);
        this.contentRes = contentRes;
        this.customFormats = customFormats;
    }

    public static ResultItem createNameMatch(File file,
                                             ResourceBundle bundle,
                                             ResourceBundle fileTypeBundle,
                                             Map<String, String> customFormats) {
        return new ResultItem(
                file, true, false, bundle, fileTypeBundle, null, customFormats);
    }

    public static ResultItem createContentMatch(File file,
                                                ResourceBundle bundle,
                                                ResourceBundle fileTypeBundle,
                                                ContentResult contentRes,
                                                Map<String, String> customFormats) {
        return new ResultItem(
                file, false, true, bundle, fileTypeBundle, contentRes, customFormats);
    }

    public void setContentRes(ContentResult contentRes) {
        this.contentRes = contentRes;
        this.matchModes[1] = true;
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
    public FileSizeItem getSize() {
        return fileSizeItem;  // nullable
    }

    @FXML
    public String getType() {
        if (file.isDirectory()) return bundle.getString("folder");
        String name = file.getName();
        String ext = Util.getFileExtension(name);
        if (ext.equals("")) return bundle.getString("file");
        else if (fileTypeBundle.containsKey(ext)) return fileTypeBundle.getString(ext);
        else if (customFormats.containsKey(ext)) return customFormats.get(ext);
        else return ext.toUpperCase() + " " + bundle.getString("file");
    }

    public File getFile() {
        return file;
    }

    public String showInfo() {
        if (contentRes != null) {
            return contentRes.getAsString(bundle);
        } else {
            return null;
        }
    }
}
