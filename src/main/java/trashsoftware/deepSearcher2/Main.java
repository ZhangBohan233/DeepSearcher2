package trashsoftware.deepSearcher2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import trashsoftware.deepSearcher2.controllers.Client;
import trashsoftware.deepSearcher2.util.Configs;

import java.util.ResourceBundle;

public class Main {

    public static void main(String[] args) throws Exception {
        Client.startClient();
    }
}
