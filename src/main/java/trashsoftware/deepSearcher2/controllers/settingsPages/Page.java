package trashsoftware.deepSearcher2.controllers.settingsPages;

import javafx.scene.layout.VBox;
import trashsoftware.deepSearcher2.controllers.SettingsPanelController;

public abstract class Page extends VBox {

    private final SettingsPanelController controller;

    public Page(SettingsPanelController controller) {
        this.controller = controller;
    }

    protected SettingsPanelController getController() {
        return controller;
    }
}
