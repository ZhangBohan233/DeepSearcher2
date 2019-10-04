package trashsoftware.deepSearcher2.controllers.settingsPages;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import trashsoftware.deepSearcher2.Main;
import trashsoftware.deepSearcher2.util.Configs;

import java.io.IOException;
import java.util.ResourceBundle;

public class AdvancedSearchingPage extends SettingsPage {

    @FXML
    ComboBox<AlgorithmBundle> algorithmBox, wordAlgorithmBox, regexAlgorithmBox;

    public AdvancedSearchingPage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/trashsoftware/deepSearcher2/fxml/settingsPages/advancedSearching.fxml"),
                Main.getBundle());
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
//        algorithmBox.getItems().addAll(
//                new AlgorithmBundle("algNative", Main.getBundle().getString("algNative")),
//                new AlgorithmBundle("algNaive", Main.getBundle().getString("algNaive"))
//        );
//        String currentAlg = Configs.getCurrentSearchingAlgorithm();
//        algorithmBox.getSelectionModel().select(new AlgorithmBundle(currentAlg, Main.getBundle().getString(currentAlg)));
//        statusSaver.store(algorithmBox);
//
//        wordAlgorithmBox.getItems().addAll(
//                new AlgorithmBundle("algNative", Main.getBundle().getString("algNative")),
//                new AlgorithmBundle("algNaive", Main.getBundle().getString("algNaive"))
//        );
//        String currentWordAlg = Configs.getCurrentWordSearchingAlgorithm();
//        wordAlgorithmBox.getSelectionModel().select(
//                new AlgorithmBundle(currentWordAlg, Main.getBundle().getString(currentWordAlg)));
//        statusSaver.store(wordAlgorithmBox);
//
//        regexAlgorithmBox.getItems().addAll(
//                new AlgorithmBundle("algNative", Main.getBundle().getString("algNative"))
//        );
//        String currentRegexAlg = Configs.getCurrentRegexSearchingAlgorithm();
//        regexAlgorithmBox.getSelectionModel().select(
//                new AlgorithmBundle(currentRegexAlg, Main.getBundle().getString(currentRegexAlg)));
//        statusSaver.store(regexAlgorithmBox);
        setAlgorithmBox(algorithmBox, Configs.getCurrentSearchingAlgorithm(),
                new AlgorithmBundle("algNative", Main.getBundle().getString("algNative")),
                new AlgorithmBundle("algNaive", Main.getBundle().getString("algNaive"))
        );

        setAlgorithmBox(wordAlgorithmBox, Configs.getCurrentWordSearchingAlgorithm(),
                new AlgorithmBundle("algNative", Main.getBundle().getString("algNative")),
                new AlgorithmBundle("algNaive", Main.getBundle().getString("algNaive"))
        );

        setAlgorithmBox(regexAlgorithmBox, Configs.getCurrentRegexSearchingAlgorithm(),
                new AlgorithmBundle("algNative", Main.getBundle().getString("algNative"))
        );
    }

    private void setAlgorithmBox(ComboBox<AlgorithmBundle> algorithmBox, String currentAlg,
                                 AlgorithmBundle... algorithmBundles) {
        algorithmBox.getItems().addAll(algorithmBundles);
        algorithmBox.getSelectionModel().select(
                new AlgorithmBundle(currentAlg, Main.getBundle().getString(currentAlg)));
        statusSaver.store(algorithmBox);
    }

    private static class AlgorithmBundle {
        private String algCode;
        private String showingName;

        AlgorithmBundle(String algCode, String showingName) {
            this.algCode = algCode;
            this.showingName = showingName;
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
