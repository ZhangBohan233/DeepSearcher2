package trashsoftware.deepSearcher2.controllers;

import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import trashsoftware.deepSearcher2.Main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ConfirmBox implements Initializable {

    @FXML
    Label messageBox;

    @FXML
    Button confirmButton;

    private Stage stage;

    private Runnable onConfirmed;

    private Runnable onCancelled;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    void onConfirmed() {
        if (onConfirmed != null) Platform.runLater(onConfirmed);
        stage.close();
    }

    @FXML
    void onCancelled() {
        if (onCancelled != null) Platform.runLater(onCancelled);
        stage.close();
    }

    @SuppressWarnings("WeakerAccess")
    public static ConfirmBox createConfirmBox(Object parent, Stage ownerStage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    parent.getClass().getResource("/trashsoftware/deepSearcher2/fxml/confirmBox.fxml"),
                    Client.getBundle());
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(Client.getBundle().getString("pleaseConfirm"));
            stage.initStyle(StageStyle.UTILITY);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(ownerStage);
            stage.setScene(new Scene(root));

            ConfirmBox controller = loader.getController();
            controller.stage = stage;

            return controller;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void setConfirmButtonText(String text) {
        confirmButton.setText(text);
    }

    public void show() {
        stage.showAndWait();
    }

    public void setMessage(String message) {
        messageBox.setText(message);
    }

    public void setOnConfirmed(Runnable eventHandler) {
        this.onConfirmed = eventHandler;
    }

    public void setOnCancelled(Runnable eventHandler) {
        this.onCancelled = eventHandler;
    }
}
