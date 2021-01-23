package trashsoftware.deepSearcher2.controllers;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import trashsoftware.deepSearcher2.util.Cache;
import trashsoftware.deepSearcher2.util.Configs;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

public class Client extends Application {

    /**
     * Name of a file which marks the program is running. Create on launch and delete on exit.
     */
    private static final String RUNNING_MARK = ".running";

    private static ResourceBundle bundle;

    private static Stage currentStage;

    public static void startClient() {
        launch();
    }

    public static void restartClient() {
        currentStage.close();
        Platform.runLater(() -> {
            try {
                new Client().showMainUi(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static ResourceBundle getBundle() {
        return bundle;
    }

    private static void createRunningMarkFile() throws IOException {
        File file = new File(RUNNING_MARK);
        if (!file.createNewFile()) throw new IOException("Cannot create mark file. ");
    }

    private static boolean isRunning() {
        File file = new File(RUNNING_MARK);
        return file.exists();
    }

    private static void deleteMarkFile() {
        File file = new File(RUNNING_MARK);
        if (!file.delete()) {
            System.gc();
            if (!file.delete()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(bundle.getString("error"));
                alert.setHeaderText(bundle.getString("cannotDeleteFile"));
                alert.setContentText(bundle.getString("pleaseManualDelete") + " " + file.getAbsolutePath());
                alert.show();
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        showMainUi(primaryStage);
    }

    private void showMainUi(Stage stage) throws Exception {
        bundle = ResourceBundle.getBundle("trashsoftware.deepSearcher2.bundles.LangBundle",
                Configs.getCurrentLocale());

        if (isRunning()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(bundle.getString("title"));
            alert.setHeaderText(bundle.getString("warning"));
            alert.setContentText(bundle.getString("title") + " " + bundle.getString("alreadyRunning"));
            alert.show();
            return;
        }

        createRunningMarkFile();
        Cache.startCache(List.of());

        currentStage = stage;
        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/trashsoftware/deepSearcher2/fxml/mainView.fxml"),
                        bundle);
        Parent root = loader.load();

        stage.setTitle("Deep Searcher 2");
        stage.setScene(new Scene(root));

        MainViewController controller = loader.getController();
        Cache.getCache().addObservable(controller);

        stage.setOnHidden(e -> {
            deleteMarkFile();
            Cache.stopCache();
        });

        stage.show();
    }
}
