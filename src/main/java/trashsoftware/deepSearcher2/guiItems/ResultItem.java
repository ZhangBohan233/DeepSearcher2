package trashsoftware.deepSearcher2.guiItems;

import javafx.fxml.FXML;
import trashsoftware.deepSearcher2.searcher.ContentSearchingResult;
import trashsoftware.deepSearcher2.util.Util;

import java.io.File;
import java.util.List;
import java.util.ResourceBundle;

public class ResultItem {

//    public static final int MATCH_NAME = 1;
//    public static final int MATCH_CONTENT = 2;

    private final File file;
    private final FileSizeItem fileSizeItem;
    private final boolean[] matchModes;

    private ContentSearchingResult contentRes;

    private final ResourceBundle bundle;
    private final ResourceBundle fileTypeBundle;

    private ResultItem(File file, boolean matchName, boolean matchContent,
                      ResourceBundle bundle, ResourceBundle fileTypeBundle,
                      ContentSearchingResult contentRes) {
        this.file = file;
        this.matchModes = new boolean[]{matchName, matchContent};
        this.bundle = bundle;
        this.fileTypeBundle = fileTypeBundle;
        this.fileSizeItem = new FileSizeItem(file.length(), bundle);
        this.contentRes = contentRes;
    }

    public static ResultItem createNameMatch(File file, ResourceBundle bundle, ResourceBundle fileTypeBundle) {
        return new ResultItem(file, true, false, bundle, fileTypeBundle, null);
    }

    public static ResultItem createContentMatch(File file, ResourceBundle bundle, ResourceBundle fileTypeBundle,
                                                ContentSearchingResult contentRes) {
        return new ResultItem(file, false, true, bundle, fileTypeBundle, contentRes);
    }

    public void setContentRes(ContentSearchingResult contentRes) {
        this.contentRes = contentRes;
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
    public FileSizeItem getSize() {
        return fileSizeItem;
    }

    @FXML
    public String getType() {
        if (file.isDirectory()) return bundle.getString("folder");
        String name = file.getName();
        String ext = Util.getFileExtension(name);
        if (ext.equals("")) return bundle.getString("file");
        else if (fileTypeBundle.containsKey(ext)) return fileTypeBundle.getString(ext);
        else return ext.toUpperCase() + " " + bundle.getString("file");
    }

    public File getFile() {
        return file;
    }

    public String showInfo() {
        if (contentRes != null) {
            return contentRes.getAsString(bundle);
//            StringBuilder builder = new StringBuilder();
//            if (contentRes.getKey1() != null) {
//                List<Integer> v1 = contentRes.getValues1();
//                String showK1 = getShowStringByKey(contentRes.getKey1());
//                String ordNum = bundle.getString("ordNum");
//                if (contentRes.getKey2() != null) {
//                    List<Integer> v2 = contentRes.getValues2();
//                    if (v1.size() != v2.size()) throw new RuntimeException("Unexpected unequal size.");
//                    String showK2 = getShowStringByKey(contentRes.getKey2());
//                    for (int i = 0; i < v1.size(); i++) {
//                        builder.append(ordNum)
//                                .append(v1.get(i))
//                                .append(showK1)
//                                .append(", ")
//                                .append(ordNum)
//                                .append(v2.get(i))
//                                .append(showK2)
//                                .append('\n');
//                    }
//                } else {
//                    for (Integer integer : v1) {
//                        builder.append(ordNum)
//                                .append(integer)
//                                .append(showK1)
//                                .append('\n');
//                    }
//                }
//            }
//            return builder.toString();
        } else {
            return null;
        }
    }

//    private String getShowStringByKey(String key) {
//        switch (key) {
//            case ContentSearchingResult.LINES_KEY:
//                return bundle.getString("lineNum");
//            case ContentSearchingResult.PARAGRAPHS_KEY:
//                return bundle.getString("paragraphNum");
//            case ContentSearchingResult.PAGES_KEY:
//                return bundle.getString("pageNum");
//            case ContentSearchingResult.CHARS_KEY:
//                return bundle.getString("characterNum");
//            case ContentSearchingResult.BLOCKS_KEY:
//                return bundle.getString("blockNum");
//            default:
//                throw new RuntimeException("No such key");
//        }
//    }
}
