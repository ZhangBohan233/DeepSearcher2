package trashsoftware.deepSearcher2.controllers.settingsPages;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import trashsoftware.deepSearcher2.util.Configs;

import java.io.IOException;
import java.util.ResourceBundle;

public class AdvancedSearchingPage extends SettingsPage {

    @FXML
    ComboBox<AlgorithmBundle> algorithmBox;

    private ResourceBundle bundle;

    public AdvancedSearchingPage(ResourceBundle bundle) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/trashsoftware/deepSearcher2/fxml/settingsPages/advancedSearching.fxml"), bundle);
        loader.setRoot(this);
        loader.setController(this);

        loader.load();

        this.bundle = bundle;

        initAlgorithmBox();
    }

    @Override
    public void saveChanges() {
        Configs.writeConfig("alg", algorithmBox.getSelectionModel().getSelectedItem().algCode);
    }

    @Override
    public void setApplyButtonStatusChanger(Button applyButton) {
        algorithmBox.getSelectionModel().selectedIndexProperty().addListener(((observableValue, number, t1) -> {
            if (number.intValue() != t1.intValue()) applyButton.setDisable(false);
        }));
    }

    private void initAlgorithmBox() {
        algorithmBox.getItems().addAll(
                new AlgorithmBundle("algNative", bundle.getString("algNative")),
                new AlgorithmBundle("algNaive", bundle.getString("algNaive"))
        );
        String currentAlg = Configs.getCurrentSearchingAlgorithm();
        algorithmBox.getSelectionModel().select(new AlgorithmBundle(currentAlg, bundle.getString(currentAlg)));
    }

    private static class AlgorithmBundle {
        private String algCode;
        private String showingName;

        AlgorithmBundle(String algCode, String showingName) {
            this.algCode = algCode;
            this.showingName = showingName;
        }

        @Override
        public String toString() {
            return showingName;
        }
    }
}
