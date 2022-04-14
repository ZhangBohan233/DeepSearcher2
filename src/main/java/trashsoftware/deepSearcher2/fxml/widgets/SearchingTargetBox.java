package trashsoftware.deepSearcher2.fxml.widgets;

import javafx.scene.control.ComboBox;

public class SearchingTargetBox extends ComboBox<String> {
    
    private final SearchingTargetList parent;
    
    public SearchingTargetBox(SearchingTargetList parent) {
        this.parent = parent;
        
        setEditable(true);
        prefWidthProperty().bind(parent.widthProperty());
    }
}
