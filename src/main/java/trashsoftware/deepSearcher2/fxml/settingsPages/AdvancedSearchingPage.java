package trashsoftware.deepSearcher2.fxml.settingsPages;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import trashsoftware.deepSearcher2.fxml.Client;
import trashsoftware.deepSearcher2.fxml.SettingsPanelController;
import trashsoftware.deepSearcher2.searcher.Algorithm;
import trashsoftware.deepSearcher2.util.Configs;

import java.io.IOException;

public class AdvancedSearchingPage extends SettingsPage {
    @FXML
    ComboBox<Algorithm.Regular> algorithmBox;
    @FXML
    ComboBox<Algorithm.Word> wordAlgorithmBox;
    @FXML
    ComboBox<Algorithm.Regex> regexAlgorithmBox;
    @FXML
    ComboBox<Integer> cpuThreadsBox;
    @FXML
    ComboBox<TraversalOrder> traversalOrderBox;
    @FXML
    CheckBox wholeContentBox;
    @FXML
    CheckBox escapeBox;

    public AdvancedSearchingPage(SettingsPanelController controller) throws IOException {
        super(controller);

        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/trashsoftware/deepSearcher2/fxml/settingsPages/advancedSearching.fxml"),
                Client.getBundle());
        loader.setRoot(this);
        loader.setController(this);

        loader.load();
        controller.addControls(algorithmBox, wordAlgorithmBox, regexAlgorithmBox, cpuThreadsBox, traversalOrderBox,
                wholeContentBox, escapeBox);

        initAlgorithmBoxes();
        initCheckBoxes();
        initThreadsBox();
        initTraversalOrderBox();
    }

    @Override
    public void saveChanges() {
        if (getStatusSaver().hasChanged(algorithmBox)) {
            Configs.getConfigs().writeConfig("alg",
                    algorithmBox.getSelectionModel().getSelectedItem().name());
            getStatusSaver().store(algorithmBox);
        }
        if (getStatusSaver().hasChanged(wordAlgorithmBox)) {
            Configs.getConfigs().writeConfig("wordAlg",
                    wordAlgorithmBox.getSelectionModel().getSelectedItem().name());
            getStatusSaver().store(wordAlgorithmBox);
        }
        if (getStatusSaver().hasChanged(regexAlgorithmBox)) {
            Configs.getConfigs().writeConfig("regexAlg",
                    regexAlgorithmBox.getSelectionModel().getSelectedItem().name());
            getStatusSaver().store(regexAlgorithmBox);
        }
        if (getStatusSaver().hasChanged(wholeContentBox)) {
            Configs.getConfigs().writeConfig("wholeContent", String.valueOf(wholeContentBox.isSelected()));
            getStatusSaver().store(wholeContentBox);
        }
        if (getStatusSaver().hasChanged(escapeBox)) {
            Configs.getConfigs().writeConfig("escapes", String.valueOf(escapeBox.isSelected()));
            getStatusSaver().store(escapeBox);
        }
        if (getStatusSaver().hasChanged(cpuThreadsBox)) {
            Configs.getConfigs().writeConfig("cpuThreads", String.valueOf(cpuThreadsBox.getSelectionModel().getSelectedItem()));
            getStatusSaver().store(cpuThreadsBox);
        }
        if (getStatusSaver().hasChanged(traversalOrderBox)) {
            Configs.getConfigs().setDepthFirst(traversalOrderBox.getSelectionModel().getSelectedItem() == TraversalOrder.DEPTH_FIRST);
            getStatusSaver().store(traversalOrderBox);
        }
    }

    private void initThreadsBox() {
        int curThreadNum = Configs.getConfigs().getCurrentCpuThreads();
        int maxThreadLimit = Runtime.getRuntime().availableProcessors();
        if (curThreadNum > maxThreadLimit) curThreadNum = maxThreadLimit;
        for (int i = 1; i <= maxThreadLimit; i++) {
            cpuThreadsBox.getItems().add(i);
        }
        cpuThreadsBox.getSelectionModel().select(Integer.valueOf(curThreadNum));
        getStatusSaver().store(cpuThreadsBox);
    }

    private void initTraversalOrderBox() {
        traversalOrderBox.getItems().addAll(TraversalOrder.DEPTH_FIRST, TraversalOrder.BREADTH_FIRST);
        traversalOrderBox.getSelectionModel().select(Configs.getConfigs().isDepthFirst() ? 0 : 1);
        getStatusSaver().store(traversalOrderBox);
    }

    private void initCheckBoxes() {
        wholeContentBox.selectedProperty().addListener(((observable, oldValue, newValue) ->
                escapeBox.setDisable(!newValue)));

        escapeBox.setSelected(Configs.getConfigs().isSearchEscapes());
        wholeContentBox.setSelected(Configs.getConfigs().isWholeContent());

        getStatusSaver().store(wholeContentBox);
        getStatusSaver().store(escapeBox);
    }

    private void initAlgorithmBoxes() {
        setAlgorithmBox(algorithmBox, Configs.getConfigs().getCurrentSearchingAlgorithm(),
                Algorithm.Regular.AUTO,
                Algorithm.Regular.NATIVE,
                Algorithm.Regular.NAIVE,
                Algorithm.Regular.KMP,
                Algorithm.Regular.SUNDAY
        );

        setAlgorithmBox(wordAlgorithmBox, Configs.getConfigs().getCurrentWordSearchingAlgorithm(),
                Algorithm.Word.NAIVE,
                Algorithm.Word.HASH
        );

        setAlgorithmBox(regexAlgorithmBox, Configs.getConfigs().getCurrentRegexSearchingAlgorithm(),
                Algorithm.Regex.NATIVE
        );
    }

    @SafeVarargs
    private <T extends Algorithm> void setAlgorithmBox(ComboBox<T> algorithmBox,
                                                       T currentAlg,
                                                       T... algorithms) {
        algorithmBox.getItems().addAll(algorithms);
        algorithmBox.getSelectionModel().select(currentAlg);
        getStatusSaver().store(algorithmBox);
    }

    private enum TraversalOrder {
        DEPTH_FIRST("depthFirst"),
        BREADTH_FIRST("breadthFirst");

        private final String nameKey;

        TraversalOrder(String nameKey) {
            this.nameKey = nameKey;
        }

        @Override
        public String toString() {
            return Client.getBundle().getString(nameKey);
        }
    }
}
