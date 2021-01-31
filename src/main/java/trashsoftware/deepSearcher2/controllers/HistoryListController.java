package trashsoftware.deepSearcher2.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import trashsoftware.deepSearcher2.guiItems.HistoryItem;
import trashsoftware.deepSearcher2.util.Configs;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class HistoryListController implements Initializable {

    @FXML
    GridPane rightPane;

    @FXML
    ColumnConstraints rightColumn;

    @FXML
    TableView<HistoryItem> historyTable;

    @FXML
    Label pattern, dirSearched, searchedTime;

    private Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setTableFactory();
        addTableListener();

        fillTable();
    }

    void setStage(Stage stage) {
        this.stage = stage;
    }

    private void setTableFactory() {
        historyTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("pattern"));
        historyTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("dirSearched"));
    }

    private void addTableListener() {
        historyTable.getSelectionModel().selectedItemProperty()
                .addListener(((observableValue, historyItem, t1) -> showItem(t1)));
    }

    @FXML
    void clearHistoryAction() {
        ConfirmBox confirmBox = ConfirmBox.createConfirmBox(stage);
        confirmBox.setMessage(Client.getBundle().getString("confirmClearHistory"));
        confirmBox.setOnConfirmed(() -> {
            Configs.clearAllHistory();
            hideRightPane();
            fillTable();
        });

        confirmBox.show();
    }

    private void showItem(HistoryItem historyItem) {
        showRightPane();

        pattern.setText(historyItem.getPatternLines());
        dirSearched.setText(historyItem.getDirSearchedLines());
        searchedTime.setText(historyItem.getTime());
    }

    private void showRightPane() {
        rightPane.setManaged(true);
        rightPane.setVisible(true);

        stage.sizeToScene();
    }

    private void hideRightPane() {
        rightPane.setManaged(false);
        rightPane.setVisible(false);

        stage.sizeToScene();
    }

    private void fillTable() {
        historyTable.getItems().clear();
        List<HistoryItem> allHistory = Configs.getAllHistory();
        historyTable.getItems().addAll(allHistory);
    }
}
