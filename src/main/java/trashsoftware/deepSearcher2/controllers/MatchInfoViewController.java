package trashsoftware.deepSearcher2.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import trashsoftware.deepSearcher2.searcher.ContentSearchingResult;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MatchInfoViewController implements Initializable {

    @FXML
    Label textLabel;

    private ResourceBundle bundle;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.bundle = resourceBundle;
    }

//    public void show(ContentSearchingResult csr) {
//        showInfo(csr);
//    }
//
//    private void showInfo(ContentSearchingResult csr) {
//        StringBuilder builder = new StringBuilder();
//        if (csr.getKey1() != null) {
//            List<Integer> v1 = csr.getValues1();
//            String showK1 = getShowStringByKey(csr.getKey1());
//            String ordNum = bundle.getString("ordNum");
//            if (csr.getKey2() != null) {
//                List<Integer> v2 = csr.getValues2();
//                if (v1.size() != v2.size()) throw new RuntimeException("Unexpected unequal size.");
//                String showK2 = getShowStringByKey(csr.getKey2());
//                for (int i = 0; i < v1.size(); i++) {
//                    builder.append(ordNum)
//                            .append(v1.get(i))
//                            .append(showK1)
//                            .append(", ")
//                            .append(ordNum)
//                            .append(v2.get(i))
//                            .append(showK2)
//                            .append('\n');
//                }
//            } else {
//                for (Integer integer : v1) {
//                    builder.append(ordNum)
//                            .append(integer)
//                            .append(showK1)
//                            .append('\n');
//                }
//            }
//        }
//        textLabel.setText(builder.toString());
//    }
//
//    private String getShowStringByKey(String key) {
//        switch (key) {
//            case ContentSearchingResult.LINES_KEY:
//                return bundle.getString("lines");
//            case ContentSearchingResult.PARAGRAPHS_KEY:
//                return bundle.getString("paragraphs");
//            case ContentSearchingResult.PAGES_KEY:
//                return bundle.getString("pages");
//            default:
//                throw new RuntimeException("No such key");
//        }
//    }
}
