package trashsoftware.deepSearcher2.controllers.settingsPages;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import trashsoftware.deepSearcher2.controllers.Client;
import trashsoftware.deepSearcher2.controllers.SettingsPanelController;
import trashsoftware.deepSearcher2.util.Configs;

import java.io.IOException;

public class AdvancedSearchingPage extends SettingsPage {

    @FXML
    ComboBox<AlgorithmBundle> algorithmBox, wordAlgorithmBox, regexAlgorithmBox;

    @FXML
    ComboBox<Integer> cpuThreadsBox;

    @FXML
    ComboBox<TraversalOrder> traversalOrderBox;

    public AdvancedSearchingPage(SettingsPanelController controller) throws IOException {
        super(controller);

        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/trashsoftware/deepSearcher2/fxml/settingsPages/advancedSearching.fxml"),
                Client.getBundle());
        loader.setRoot(this);
        loader.setController(this);

        loader.load();
        controller.addControls(algorithmBox, wordAlgorithmBox, regexAlgorithmBox, cpuThreadsBox, traversalOrderBox);

        initAlgorithmBoxes();
        initThreadsBox();
        initTraversalOrderBox();
    }

    @Override
    public void saveChanges() {
        if (getStatusSaver().hasChanged(algorithmBox)) {
            Configs.getConfigs().writeConfig("alg", algorithmBox.getSelectionModel().getSelectedItem().algCode);
            getStatusSaver().store(algorithmBox);
        }
        if (getStatusSaver().hasChanged(wordAlgorithmBox)) {
            Configs.getConfigs().writeConfig("wordAlg", wordAlgorithmBox.getSelectionModel().getSelectedItem().algCode);
            getStatusSaver().store(wordAlgorithmBox);
        }
        if (getStatusSaver().hasChanged(regexAlgorithmBox)) {
            Configs.getConfigs().writeConfig("regexAlg", regexAlgorithmBox.getSelectionModel().getSelectedItem().algCode);
            getStatusSaver().store(regexAlgorithmBox);
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

    private void initAlgorithmBoxes() {
        setAlgorithmBox(algorithmBox, Configs.getConfigs().getCurrentSearchingAlgorithm(),
                new AlgorithmBundle("algAuto"),
                new AlgorithmBundle("algNative"),
                new AlgorithmBundle("algNaive"),
                new AlgorithmBundle("algKmp"),
                new AlgorithmBundle("algSunday")
        );

        setAlgorithmBox(wordAlgorithmBox, Configs.getConfigs().getCurrentWordSearchingAlgorithm(),
                new AlgorithmBundle("algNaive"),
                new AlgorithmBundle("algHash")
        );

        setAlgorithmBox(regexAlgorithmBox, Configs.getConfigs().getCurrentRegexSearchingAlgorithm(),
                new AlgorithmBundle("algNative")
        );
    }

    private void setAlgorithmBox(ComboBox<AlgorithmBundle> algorithmBox, String currentAlg,
                                 AlgorithmBundle... algorithmBundles) {
        algorithmBox.getItems().addAll(algorithmBundles);
        algorithmBox.getSelectionModel().select(
                new AlgorithmBundle(currentAlg));
        getStatusSaver().store(algorithmBox);
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
