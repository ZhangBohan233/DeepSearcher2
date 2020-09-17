package trashsoftware.deepSearcher2.guiItems;

import javafx.fxml.FXML;
import trashsoftware.deepSearcher2.Main;
import trashsoftware.deepSearcher2.controllers.Client;
import trashsoftware.deepSearcher2.searcher.PrefSet;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.stream.Collectors;

public class HistoryItem {

    private final PrefSet prefSet;
    private final Date date;

    private final static DateFormat SHOWN_TIME_FORMAT = DateFormat.getDateTimeInstance(
            DateFormat.SHORT,
            DateFormat.SHORT,
            Client.getBundle().getLocale());

    public HistoryItem(PrefSet prefSet, Date date) {
        this.prefSet = prefSet;
        this.date = date;
    }

    @FXML
    public String getPattern() {
        return mergeString(prefSet.getTargets(), ",");
    }

    @FXML
    public String getDirSearched() {
        return mergeString(prefSet.getSearchDirs().stream()
                .map(File::getAbsolutePath)
                .collect(Collectors.toList()), ",");
    }

    private static String mergeString(Iterable<String> iterable, String connector) {
        StringBuilder builder = new StringBuilder();
        for (String p : iterable) {
            builder.append(p).append(connector);
        }
        builder.setLength(builder.length() - connector.length());
        return builder.toString();
    }

    public String getPatternLines() {
        return mergeString(prefSet.getTargets(), "\n");
    }

    public String getDirSearchedLines() {
        return mergeString(prefSet.getSearchDirs().stream()
                .map(File::getAbsolutePath)
                .collect(Collectors.toList()), "\n");
    }

    public String getTime() {
        return SHOWN_TIME_FORMAT.format(date);
    }
}
