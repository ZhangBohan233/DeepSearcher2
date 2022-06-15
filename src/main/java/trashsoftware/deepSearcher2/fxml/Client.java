package trashsoftware.deepSearcher2.fxml;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import trashsoftware.deepSearcher2.extensionLoader.ExtensionLoader;
import trashsoftware.deepSearcher2.util.Cache;
import trashsoftware.deepSearcher2.util.Configs;
import trashsoftware.deepSearcher2.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Objects;
import java.util.ResourceBundle;

public class Client extends Application {
    public static final String COPYRIGHT_NAME_ZH = "潜望镜文件搜索软件";
    public static final String COPYRIGHT_NAME_EN = "Deep Search 2";
    public static final String COPYRIGHT_YEAR = "2021";
    public static final String AUTHOR_ZH = "张博涵";
    public static final String AUTHOR_EN = "Bohan Zhang";

    public static final String VERSION = "1.1.6";

    /**
     * Name of a file which marks the program is running. Create on launch and delete on exit.
     */
    private static final String RUNNING_MARK = ".running";

    /**
     * Occupy the ".running" file on start.
     */
    private static RandomAccessFile runningOccupation;

    private static ResourceBundle bundle;
    private static ResourceBundle fileTypeBundle;

    private static Image iconImage;
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
                Log.severe(e);
                e.printStackTrace();
            }
        });
    }

    public static ResourceBundle getBundle() {
        return bundle;
    }

    public static ResourceBundle getFileTypeBundle() {
        return fileTypeBundle;
    }

    public static Image getIconImage() {
        return iconImage;
    }

    private static boolean lockRunningFile() {
        File file = new File(RUNNING_MARK);
        try {
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    return false;
                }
            }
            runningOccupation = new RandomAccessFile(file, "rw");
            runningOccupation.write(1);
            runningOccupation.getChannel().lock();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private static void releaseRunningFile() {
        try {
            runningOccupation.close();
        } catch (IOException e) {
            Log.severe(e);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        showMainUi(primaryStage);
    }

    private void showMainUi(Stage stage) throws Exception {
        Configs.startConfig();

        bundle = ResourceBundle.getBundle("trashsoftware.deepSearcher2.bundles.LangBundle",
                Configs.getConfigs().getCurrentLocale());
        fileTypeBundle = ResourceBundle.getBundle("trashsoftware.deepSearcher2.bundles.FileTypeBundle",
                Configs.getConfigs().getCurrentLocale());

        if (!lockRunningFile()) {
            Configs.stopConfig();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(bundle.getString("appName"));
            alert.setHeaderText(bundle.getString("warning"));
            alert.setContentText(bundle.getString("appName") + " " + bundle.getString("alreadyRunning"));
            alert.show();
            return;
        }

        ExtensionLoader.startLoader();
        Cache.startCache();

        currentStage = stage;
        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/trashsoftware/deepSearcher2/fxml/mainView.fxml"),
                        bundle);
        Parent root = loader.load();
        MainViewController controller = loader.getController();

        iconImage = new Image(
                Objects.requireNonNull(
                        getClass().getResourceAsStream("/trashsoftware/deepSearcher2/images/icon.bmp")));

        Scene rootScene = new Scene(root);
        if (Configs.getConfigs().isUseCustomFont()) {
            controller.rescaleUi(Configs.getConfigs().getFontSize(12));
        }
        Configs.getConfigs().applyThemeAndFont(rootScene);

        stage.setTitle(bundle.getString("appName"));
        stage.getIcons().add(iconImage);
        stage.setScene(rootScene);

        controller.setStage(stage);
        Cache.getCache().addObservable(controller);

        stage.setOnHidden(e -> {
            controller.stopActiveSearcher();
            releaseRunningFile();
            ExtensionLoader.stopLoader();
            Cache.stopCache();
            Configs.stopConfig();
        });

        stage.show();
    }
}
