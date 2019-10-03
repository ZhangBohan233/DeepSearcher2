package trashsoftware.deepSearcher2.controllers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import trashsoftware.deepSearcher2.controllers.widgets.TextFieldList;
import trashsoftware.deepSearcher2.items.FormatItem;
import trashsoftware.deepSearcher2.items.ResultItem;
import trashsoftware.deepSearcher2.searcher.PrefSet;
import trashsoftware.deepSearcher2.searcher.SearchDirNotSetException;
import trashsoftware.deepSearcher2.searcher.SearchTargetNotSetException;
import trashsoftware.deepSearcher2.searcher.Searcher;
import trashsoftware.deepSearcher2.util.Configs;
import trashsoftware.deepSearcher2.util.Util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {

    @FXML
    TableView<ResultItem> resultTable;

    @FXML
    TableView<FormatItem> formatTable;

    @FXML
    TextFieldList searchItemsList;

    @FXML
    ListView<File> dirList;

    @FXML
    ToggleGroup andOrGroup;

    @FXML
    RadioButton matchAllRadioBtn, matchAnyRadioBtn;

    @FXML
    CheckBox searchFileNameBox, searchDirNameBox, searchContentBox, includeDirNameBox;

    @FXML
    CheckBox matchCaseBox, matchWordBox, matchRegexBox;

    @FXML
    CheckBox selectAllBox;

    @FXML
    Button searchButton;

    @FXML
    Button deleteDirButton, deleteTargetButton;

    @FXML
    Label searchingStatusText, resultNumberText, statusSuffixText, timeUsedLabelText, timeUsedText, timeUnitText;

    @FXML
    ProgressIndicator progressIndicator;

    private ResourceBundle bundle;

    private ResourceBundle fileTypeBundle =
            ResourceBundle.getBundle("trashsoftware.deepSearcher2.bundles.FileTypeBundle",
                    Configs.getCurrentLocale());

    private boolean isSearching;

    private SearchService service;

    private ChangeListener<Number> fileCountListener;

    private boolean showingSelectAll = true;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bundle = resourceBundle;

        setResultTableFactory();
        setFormatTableFactory();
        addDirListListener();
        addTargetListListener();
        addCheckBoxesListeners();

        addSearchItem();  // Add a default search field

        fillFormatTable();
    }

    // Controls

    @FXML
    void addSearchDir() {
        DirectoryChooser dc = new DirectoryChooser();
        File dir = dc.showDialog(null);
        if (dir != null) {
            dirList.getItems().add(dir);
        }
    }

    @FXML
    void addSearchItem() {
        TextField textField = new TextField();
        textField.setPromptText(bundle.getString("searchPrompt"));
        searchItemsList.getTextFields().add(textField);
    }

    @FXML
    void deleteSearchDir() {
        int index = dirList.getSelectionModel().getSelectedIndex();
        dirList.getItems().remove(index);
    }

    @FXML
    void deleteSearchItem() {
        int index = searchItemsList.getSelectedIndex();
        searchItemsList.getTextFields().remove(index);
    }

    @FXML
    void searchAction() {
        if (isSearching) {
            cancelSearching();
        } else {
            startSearching();
        }
    }

    @FXML
    void selectAllAction() {
        if (showingSelectAll) {
            selectAllFormats();
            selectAllBox.setText(bundle.getString("deselectAll"));
        } else {
            deselectAllFormats();
            selectAllBox.setText(bundle.getString("selectAll"));
        }
        showingSelectAll = !showingSelectAll;
    }

    @FXML
    void openSettingsAction() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/trashsoftware/deepSearcher2/fxml/settingsPanel.fxml"), bundle);
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle(bundle.getString("settings"));
        stage.setScene(new Scene(root));

        SettingsPanelController controller = loader.getController();
        controller.setStage(stage);

        stage.show();
    }

    // Factories and listeners

    private void setResultTableFactory() {
        TableColumn<ResultItem, ?> fileNameCol = resultTable.getColumns().get(0);
        TableColumn<ResultItem, ?> fileSizeCol = resultTable.getColumns().get(1);
        TableColumn<ResultItem, ?> fileTypeCol = resultTable.getColumns().get(2);
        TableColumn<ResultItem, ?> matchModeCol = resultTable.getColumns().get(3);

        fileNameCol.setCellValueFactory(new PropertyValueFactory<>("Name"));
        fileSizeCol.setCellValueFactory(new PropertyValueFactory<>("Size"));
        fileTypeCol.setCellValueFactory(new PropertyValueFactory<>("Type"));
        matchModeCol.setCellValueFactory(new PropertyValueFactory<>("Mode"));
    }

    private void setFormatTableFactory() {
        TableColumn<FormatItem, ?> checkCol = formatTable.getColumns().get(0);
        TableColumn<FormatItem, ?> descriptionCol = formatTable.getColumns().get(1);
        TableColumn<FormatItem, ?> extCol = formatTable.getColumns().get(2);

        checkCol.setCellValueFactory(new PropertyValueFactory<>("CheckBox"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("Description"));
        extCol.setCellValueFactory(new PropertyValueFactory<>("Extension"));
    }

    private void addDirListListener() {
        dirList.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() == -1) {
                deleteDirButton.setDisable(true);
            } else {
                deleteDirButton.setDisable(false);
            }
        });
    }

    private void addTargetListListener() {
        searchItemsList.selectedIndexProperty().addListener(((observableValue, number, t1) -> {
            if (t1.intValue() == -1) {
                deleteTargetButton.setDisable(true);
            } else {
                deleteTargetButton.setDisable(false);
            }
        }));
    }

    private void addCheckBoxesListeners() {
        searchContentBox.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (t1) {
                formatTable.setDisable(false);
                selectAllBox.setDisable(false);
            } else {
                formatTable.setDisable(true);
                selectAllBox.setDisable(true);
            }
        });
        matchWordBox.selectedProperty().addListener(((observableValue, aBoolean, t1) -> {
            if (t1) {
                matchRegexBox.setSelected(false);
            }
        }));
        matchRegexBox.selectedProperty().addListener(((observableValue, aBoolean, t1) -> {
            if (t1) {
                matchWordBox.setSelected(false);
            }
        }));
    }

    // Helper functions

    private void fillFormatTable() {
        Enumeration<String> keys = fileTypeBundle.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            FormatItem formatItem = new FormatItem("." + key, fileTypeBundle.getString(key));
            formatTable.getItems().add(formatItem);
        }
    }

    private void selectAllFormats() {
        for (FormatItem formatItem : formatTable.getItems()) {
            formatItem.getCheckBox().setSelected(true);
        }
    }

    private void deselectAllFormats() {
        for (FormatItem formatItem : formatTable.getItems()) {
            formatItem.getCheckBox().setSelected(false);
        }
    }

    private void startSearching() {
        try {
            long beginTime = System.currentTimeMillis();
            PrefSet prefSet = new PrefSet.PrefSetBuilder()
                    .matchCase(false)
                    .setMatchAll(matchAllRadioBtn.isSelected())
                    .searchFileName(searchFileNameBox.isSelected())
                    .searchDirName(searchDirNameBox.isSelected())
                    .includeDirName(includeDirNameBox.isSelected())
                    .matchCase(matchCaseBox.isSelected())
                    .matchWord(matchWordBox.isSelected())
                    .matchRegex(matchRegexBox.isSelected())
                    .setMatchingAlgorithm(Configs.getCurrentSearchingAlgorithm())
                    .setTargets(getTargets())
                    .setSearchDirs(dirList.getItems())
                    .setExtensions(getExtensions())
                    .build();

            resultTable.getItems().clear();
            setInSearchingUi();

            Searcher searcher = new Searcher(prefSet, resultTable.getItems(), bundle, fileTypeBundle);
            service = new SearchService(searcher);

            service.setOnSucceeded(e -> {
                unbindListeners();
                finishSearching(searcher.isNormalFinish());
                setTimerTexts(System.currentTimeMillis() - beginTime);
            });

            service.setOnFailed(e -> {
                unbindListeners();

                e.getSource().getException().printStackTrace();
            });

            service.start();

        } catch (SearchTargetNotSetException e) {
            showHoverMessage("targetNotSet", searchButton);
        } catch (SearchDirNotSetException e) {
            showHoverMessage("dirNotSet", searchButton);
        }
    }

    private void setTimerTexts(long timeUsedMs) {
        timeUsedLabelText.setText(bundle.getString("timeUsed"));
        timeUsedText.setText(String.format("%.1f", (double) timeUsedMs / 1000));
        timeUnitText.setText(bundle.getString("secondUnit"));
    }

    private void finishSearching(boolean normalFinish) {
        setNotInSearchingUi(normalFinish);
    }

    private void cancelSearching() {
        service.getSearcher().stop();
    }

    private void setInSearchingUi() {
        isSearching = true;
        searchButton.setText(bundle.getString("cancel"));
        progressIndicator.setVisible(true);
        progressIndicator.setManaged(true);
        resultNumberText.setText("0");
        searchingStatusText.setText(bundle.getString("searching"));
        statusSuffixText.setText(bundle.getString("searchDoneSuffix"));
        timeUsedLabelText.setText("");
        timeUsedText.setText("");
        timeUnitText.setText("");
    }

    private void setNotInSearchingUi(boolean normalFinish) {
        isSearching = false;
        searchButton.setText(bundle.getString("search"));
        progressIndicator.setVisible(false);
        progressIndicator.setManaged(false);
        if (normalFinish) searchingStatusText.setText(bundle.getString("searchDone"));
        else searchingStatusText.setText(bundle.getString("searchAbort"));
        statusSuffixText.setText(bundle.getString("searchDoneSuffix"));
    }

    private void showHoverMessage(String textKey, Node parent) {
        ContextMenu popupMenu = new ContextMenu();
        MenuItem menuItem = new MenuItem(bundle.getString(textKey));
        menuItem.setDisable(true);
        popupMenu.getItems().add(menuItem);

        EventHandler<? super MouseEvent> originalHandler = parent.getOnMouseClicked();

        parent.setOnMouseClicked(e -> {
            popupMenu.show(parent, e.getScreenX(), e.getScreenY());
            parent.setOnMouseClicked(originalHandler);
        });
    }

    private List<String> getTargets() {
        List<String> list = new ArrayList<>();
        for (Node node : searchItemsList.getTextFields()) {
            list.add(((TextField) node).getText());
        }
        return list;
    }

    /**
     * @return the list of all selected file extensions if "search content" is selected, {@code null} otherwise.
     */
    private List<String> getExtensions() {
        if (searchContentBox.isSelected()) {
            List<String> list = new ArrayList<>();
            for (FormatItem formatItem : formatTable.getItems()) {
                if (formatItem.getCheckBox().isSelected()) {
                    list.add(formatItem.getExtension());
                }
            }
            return list;
        } else
            return null;
    }

    private class SearchService extends Service<Void> {
        private Searcher searcher;

        SearchService(Searcher searcher) {
            this.searcher = searcher;
        }

        @Override
        protected Task<Void> createTask() {
            return new Task<>() {
                @Override
                protected Void call() {

                    fileCountListener = (observable, oldValue, newValue) ->
                            Platform.runLater(() ->
                                    resultNumberText.setText(Util.separateInteger(newValue.longValue())));

                    searcher.resultCountProperty().addListener(fileCountListener);

                    searcher.search();

                    return null;
                }
            };
        }

        Searcher getSearcher() {
            return searcher;
        }
    }

    private void unbindListeners() {
        service.getSearcher().resultCountProperty().removeListener(fileCountListener);
    }
}
