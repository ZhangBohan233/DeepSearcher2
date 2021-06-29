package trashsoftware.deepSearcher2.fxml;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import trashsoftware.deepSearcher2.searcher.Searcher;
import trashsoftware.deepSearcher2.util.Util;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ResourceBundle;

public class ResultInfoView implements Initializable {

    @FXML
    Label timeUsedLabel, timeUnitLabel;

    @FXML
    Label filesSearchedLabel, foldersSearchedLabel, contentsSearchedLabel, archivesSearchedLabel;

    @FXML
    Label filesSearchedSizeLabel, contentSearchedSizeLabel, archiveSearchedSizeLabel;

    private ResourceBundle bundle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
    }

    public void setup(Searcher.SearchInfoCollector infoCollector, long timeUsedMs) {
        DecimalFormat format = (DecimalFormat) NumberFormat.getInstance();

        timeUsedLabel.setText(String.format("%.1f", (double) timeUsedMs / 1000));
        timeUnitLabel.setText(bundle.getString("secondUnit"));

        filesSearchedLabel.setText(format.format(infoCollector.getFilesSearched()));
        foldersSearchedLabel.setText(format.format(infoCollector.getFoldersSearched()));
        contentsSearchedLabel.setText(format.format(infoCollector.getContentsSearched()));
        archivesSearchedLabel.setText(format.format(infoCollector.getArchivesSearched()));

        filesSearchedSizeLabel.setText(
                Util.sizeToReadable(infoCollector.getFilesSearchedSize(), bundle.getString("bytes")));
        contentSearchedSizeLabel.setText(
                Util.sizeToReadable(infoCollector.getContentsSearchedSize(), bundle.getString("bytes")));
        archiveSearchedSizeLabel.setText(
                Util.sizeToReadable(infoCollector.getArchivesSearchedSize(), bundle.getString("bytes")));
    }
}
