package trashsoftware.deepSearcher2.guiItems;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;
import trashsoftware.deepSearcher2.controllers.MatchInfoViewController;
import trashsoftware.deepSearcher2.controllers.SettingsPanelController;
import trashsoftware.deepSearcher2.searcher.ContentSearchingResult;
import trashsoftware.deepSearcher2.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

public class ResultItem {

//    public static final int MATCH_NAME = 1;
//    public static final int MATCH_CONTENT = 2;

    private final File file;
    private final boolean[] matchModes;

    private ContentSearchingResult contentRes;

    private final ResourceBundle bundle;
    private final ResourceBundle fileTypeBundle;

    public ResultItem(File file, boolean matchName, boolean matchContent,
                      ResourceBundle bundle, ResourceBundle fileTypeBundle) {
        this.file = file;
        this.matchModes = new boolean[]{matchName, matchContent};
        this.bundle = bundle;
        this.fileTypeBundle = fileTypeBundle;
    }

    public void setContentRes(ContentSearchingResult contentRes) {
        this.contentRes = contentRes;
        this.matchModes[1] = true;
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
        if (file.isDirectory()) return bundle.getString("folder");
        String name = file.getName();
        String ext = Util.getFileExtension(name);
        if (ext.equals("")) return bundle.getString("file");
        else if (fileTypeBundle.containsKey(ext)) return fileTypeBundle.getString(ext);
        else return ext.toUpperCase() + " " + bundle.getString("file");
    }

//    @FXML
//    public Hyperlink getInfo() {
//        if (contentRes == null) {
//            return null;
//        }
//        Hyperlink hyperlink = new Hyperlink(bundle.getString("locationInfo"));
//        hyperlink.setOnAction(e -> {
//            try {
//                openMatchInfoView();
//            } catch (IOException ioException) {
//                ioException.printStackTrace();
//            }
//        });
//
//        return hyperlink;
//    }

//    private void openMatchInfoView() throws IOException {
//        FXMLLoader loader = new FXMLLoader(
//                getClass().getResource("/trashsoftware/deepSearcher2/fxml/matchInfoView.fxml"), bundle);
//        Parent root = loader.load();
//        Stage stage = new Stage();
//        stage.setTitle(bundle.getString("matchInfo"));
//        stage.setScene(new Scene(root));
//
//        MatchInfoViewController controller = loader.getController();
//        controller.show(contentRes);
//
//        stage.show();
//    }

    public File getFile() {
        return file;
    }

    public String showInfo() {
        if (contentRes != null) {
            StringBuilder builder = new StringBuilder();
            if (contentRes.getKey1() != null) {
                List<Integer> v1 = contentRes.getValues1();
                String showK1 = getShowStringByKey(contentRes.getKey1());
                String ordNum = bundle.getString("ordNum");
                if (contentRes.getKey2() != null) {
                    List<Integer> v2 = contentRes.getValues2();
                    if (v1.size() != v2.size()) throw new RuntimeException("Unexpected unequal size.");
                    String showK2 = getShowStringByKey(contentRes.getKey2());
                    for (int i = 0; i < v1.size(); i++) {
                        builder.append(ordNum)
                                .append(v1.get(i))
                                .append(showK1)
                                .append(", ")
                                .append(ordNum)
                                .append(v2.get(i))
                                .append(showK2)
                                .append('\n');
                    }
                } else {
                    for (Integer integer : v1) {
                        builder.append(ordNum)
                                .append(integer)
                                .append(showK1)
                                .append('\n');
                    }
                }
            }
            return builder.toString();
        } else {
            return null;
        }
    }

    private String getShowStringByKey(String key) {
        switch (key) {
            case ContentSearchingResult.LINES_KEY:
                return bundle.getString("lines");
            case ContentSearchingResult.PARAGRAPHS_KEY:
                return bundle.getString("paragraphs");
            case ContentSearchingResult.PAGES_KEY:
                return bundle.getString("pages");
            case ContentSearchingResult.CHARS_KEY:
                return bundle.getString("characters");
            default:
                throw new RuntimeException("No such key");
        }
    }
}
