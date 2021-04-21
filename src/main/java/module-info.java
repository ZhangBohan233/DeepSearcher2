module DeepSearcher {
    requires javafx.fxml;
    requires javafx.controls;
    requires java.desktop;
    requires org.json;
    requires org.apache.commons.codec;
    requires org.apache.commons.collections4;
    requires org.apache.commons.compress;
    requires org.apache.pdfbox;
    requires poi.ooxml;
    requires poi.scratchpad;
    requires poi;
    requires org.tukaani.xz;
    requires junrar;
    requires dsApi;

    opens trashsoftware.deepSearcher2;
    opens trashsoftware.deepSearcher2.guiItems;
    opens trashsoftware.deepSearcher2.fxml;
    opens trashsoftware.deepSearcher2.fxml.widgets;
    opens trashsoftware.deepSearcher2.fxml.settingsPages;

    exports trashsoftware.deepSearcher2.fxml;
    exports trashsoftware.deepSearcher2.searcher;
    exports trashsoftware.deepSearcher2.searcher.matchers;
    exports trashsoftware.deepSearcher2.searcher.archiveSearchers;
    exports trashsoftware.deepSearcher2.guiItems;
    exports trashsoftware.deepSearcher2.util;
    exports trashsoftware.deepSearcher2.fxml.widgets;
    exports trashsoftware.deepSearcher2.fxml.settingsPages;
}