package trashsoftware.deepSearcher2.controllers;

import javafx.application.Platform;
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
import javafx.stage.Window;
import trashsoftware.deepSearcher2.util.Configs;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * A utility window that ask user to confirm.
 * <p>
 * This window provides a message box, a confirm button, and a cancel button.
 */
public class ConfirmBox implements Initializable {

    @FXML
    Label messageBox;

    @FXML
    Button confirmButton;

    private Stage stage;

    private Runnable onConfirmed;

    private Runnable onCancelled;

    /**
     * Creates a new confirm box, but does not show it.
     *
     * @param ownerWindow the parent window, which will be blocked by this confirm box
     * @return the new instance
     */
    public static ConfirmBox createConfirmBox(Window ownerWindow) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ConfirmBox.class.getResource("/trashsoftware/deepSearcher2/fxml/confirmBox.fxml"),
                    Client.getBundle());
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(Client.getBundle().getString("pleaseConfirm"));
            stage.initStyle(StageStyle.UTILITY);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(ownerWindow);
            Scene scene = new Scene(root);
            if (Configs.getConfigs().isUseCustomFont()) {
                Configs.getConfigs().applyCustomFont(scene);
            }
            stage.setScene(scene);

            ConfirmBox controller = loader.getController();
            controller.stage = stage;

            return controller;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

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

    /**
     * Sets the text shown on the confirm button.
     *
     * @param text text to show
     */
    public void setConfirmButtonText(String text) {
        confirmButton.setText(text);
    }

    /**
     * Shows the confirm box until user selects a result or closes the window.
     */
    public void show() {
        stage.showAndWait();
    }

    /**
     * Sets the message shown in the message box
     *
     * @param message text to show
     */
    public void setMessage(String message) {
        messageBox.setText(message);
    }

    /**
     * Sets the action if confirm button is clicked
     *
     * @param eventHandler on confirm action
     */
    public void setOnConfirmed(Runnable eventHandler) {
        this.onConfirmed = eventHandler;
    }

    /**
     * Sets the action if cancel button is clicked
     *
     * @param eventHandler on cancel action
     */
    @SuppressWarnings("unused")
    public void setOnCancelled(Runnable eventHandler) {
        this.onCancelled = eventHandler;
    }
}
