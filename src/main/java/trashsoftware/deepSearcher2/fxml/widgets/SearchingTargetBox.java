package trashsoftware.deepSearcher2.fxml.widgets;

import javafx.scene.control.ComboBox;
import trashsoftware.deepSearcher2.util.Configs;

public class SearchingTargetBox extends ComboBox<String> {
    
    private final SearchingTargetList parent;
    
    public SearchingTargetBox(SearchingTargetList parent) {
        getStylesheets().add(getUserAgentStylesheet());
        getStyleClass().add("searching-target-box");
        this.parent = parent;
        
        setEditable(true);
        prefWidthProperty().bind(parent.widthProperty());
    }

    @Override
    public String getUserAgentStylesheet() {
        return Configs.getConfigs().getStyleSheetPath();
    }
}
