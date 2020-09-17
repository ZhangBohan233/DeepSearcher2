package trashsoftware.deepSearcher2.controllers.settingsPages;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import trashsoftware.deepSearcher2.Main;
import trashsoftware.deepSearcher2.controllers.Client;
import trashsoftware.deepSearcher2.controllers.SettingsPanelController;
import trashsoftware.deepSearcher2.util.Configs;

import java.io.IOException;

public class AdvancedSearchingPage extends SettingsPage {

    @FXML
    ComboBox<AlgorithmBundle> algorithmBox, wordAlgorithmBox, regexAlgorithmBox;

    public AdvancedSearchingPage(SettingsPanelController controller) throws IOException {
        super(controller);

        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/trashsoftware/deepSearcher2/fxml/settingsPages/advancedSearching.fxml"),
                Client.getBundle());
        loader.setRoot(this);
        loader.setController(this);

        loader.load();
        addControls(algorithmBox, wordAlgorithmBox, regexAlgorithmBox);

        initAlgorithmBoxes();
    }

    @Override
    public void saveChanges() {
        if (statusSaver.hasChanged(algorithmBox)) {
            Configs.writeConfig("alg", algorithmBox.getSelectionModel().getSelectedItem().algCode);
            statusSaver.store(algorithmBox);
        }
        if (statusSaver.hasChanged(wordAlgorithmBox)) {
            Configs.writeConfig("wordAlg", wordAlgorithmBox.getSelectionModel().getSelectedItem().algCode);
            statusSaver.store(wordAlgorithmBox);
        }
        if (statusSaver.hasChanged(regexAlgorithmBox)) {
            Configs.writeConfig("regexAlg", regexAlgorithmBox.getSelectionModel().getSelectedItem().algCode);
            statusSaver.store(regexAlgorithmBox);
        }
    }

    private void initAlgorithmBoxes() {
        setAlgorithmBox(algorithmBox, Configs.getCurrentSearchingAlgorithm(),
                new AlgorithmBundle("algAuto"),
                new AlgorithmBundle("algNative"),
                new AlgorithmBundle("algNaive"),
                new AlgorithmBundle("algKmp"),
                new AlgorithmBundle("algSunday")
        );

        setAlgorithmBox(wordAlgorithmBox, Configs.getCurrentWordSearchingAlgorithm(),
                new AlgorithmBundle("algNaive"),
                new AlgorithmBundle("algHash")
        );

        setAlgorithmBox(regexAlgorithmBox, Configs.getCurrentRegexSearchingAlgorithm(),
                new AlgorithmBundle("algNative")
        );
    }

    private void setAlgorithmBox(ComboBox<AlgorithmBundle> algorithmBox, String currentAlg,
                                 AlgorithmBundle... algorithmBundles) {
        algorithmBox.getItems().addAll(algorithmBundles);
        algorithmBox.getSelectionModel().select(
                new AlgorithmBundle(currentAlg));
        statusSaver.store(algorithmBox);
    }

    private static class AlgorithmBundle {
        private final String algCode;
        private final String showingName;

        AlgorithmBundle(String algCode) {
            this.algCode = algCode;
            this.showingName = Client.getBundle().getString(algCode);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof AlgorithmBundle && ((AlgorithmBundle) obj).algCode.equals(algCode);
        }

        @Override
        public String toString() {
            return showingName;
        }
    }
}
