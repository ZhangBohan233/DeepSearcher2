module DeepSearcher {
    requires javafx.fxml;
    requires javafx.controls;
    requires java.desktop;

    opens trashsoftware.deepSearcher2;
    opens trashsoftware.deepSearcher2.items;
    opens trashsoftware.deepSearcher2.controllers;
    opens trashsoftware.deepSearcher2.controllers.widgets;
    opens trashsoftware.deepSearcher2.controllers.settingsPages;

    exports trashsoftware.deepSearcher2.controllers;
}