package trashsoftware.deepSearcher2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import trashsoftware.deepSearcher2.util.Configs;

import java.util.ResourceBundle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        ResourceBundle bundle = ResourceBundle.getBundle("trashsoftware.deepSearcher2.bundles.LangBundle",
                Configs.getLocale());
        Parent root = FXMLLoader.load(
                getClass().getResource("/trashsoftware/deepSearcher2/fxml/mainView.fxml"), bundle);
        primaryStage.setTitle("Deep Searcher 2");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
