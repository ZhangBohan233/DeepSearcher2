package trashsoftware.deepSearcher2.controllers;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.json.JSONArray;
import trashsoftware.deepSearcher2.controllers.widgets.FormatTable;
import trashsoftware.deepSearcher2.controllers.widgets.TextFieldList;
import trashsoftware.deepSearcher2.guiItems.FormatFilterItem;
import trashsoftware.deepSearcher2.guiItems.FormatItem;
import trashsoftware.deepSearcher2.guiItems.ResultItem;
import trashsoftware.deepSearcher2.searcher.PrefSet;
import trashsoftware.deepSearcher2.searcher.SearchDirNotSetException;
import trashsoftware.deepSearcher2.searcher.SearchTargetNotSetException;
import trashsoftware.deepSearcher2.searcher.Searcher;
import trashsoftware.deepSearcher2.util.Configs;
import trashsoftware.deepSearcher2.util.Util;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

public class MainViewController implements Initializable {

    @FXML
    TableView<ResultItem> resultTable;

    @FXML
    TableColumn<ResultItem, String> fileNameCol;

    @FXML
    TableColumn<ResultItem, String> matchingModeCol;

    @FXML
    TableColumn<FormatItem, String> formatNameCol;

    @FXML
    FormatTable formatTable;

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
    ComboBox<FormatFilterItem> filterBox;

    @FXML
    Button searchButton;

    @FXML
    Button deleteDirButton, deleteTargetButton;

    @FXML
    Label searchingStatusText, resultNumberText, statusSuffixText, timeUsedLabelText, timeUsedText, timeUnitText;

    @FXML
    ProgressIndicator progressIndicator;

    private ResourceBundle bundle;

    private final ResourceBundle fileTypeBundle =
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

        addFileNameColumnHoverListener();
        addMatchingModeColumnHoverListener();
        addFormatNameColumnHoverListener();
        addResultTableClickListeners();
        addDirListListener();
        addTargetListListener();

        addRadioButtonsListeners();
        loadRadioButtonsInitialStatus();

        addCheckBoxesListeners();
        loadSavedCheckBoxesStatus();

        addFilterBoxListener();

        addSearchItem();  // Add a default search field

        fillFormatTable();
        fillFilterBox();
        filterBox.getSelectionModel().select(0);  // this step must run after 'fillFormatTable()'

        // restore saved status
        restoreSavedFormats();
        restoreLastOpenedDirs();
    }

    // Controls

    @FXML
    void addSearchDir() {
        DirectoryChooser dc = new DirectoryChooser();
        File lastOpenDir = getLastOpenedDir();
        if (lastOpenDir != null)
            dc.setInitialDirectory(lastOpenDir.getParentFile());
        File dir = dc.showDialog(null);
        if (dir != null) {
            addOpenedDir(dir);
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
        File removed = dirList.getItems().remove(index);
        deleteOpenedDir(removed);
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

    @FXML
    void openHistoryAction() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/trashsoftware/deepSearcher2/fxml/historyListView.fxml"), bundle);
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle(bundle.getString("history"));
        stage.setScene(new Scene(root));

        HistoryListController controller = loader.getController();
        controller.setStage(stage);

        stage.show();
    }

    @FXML
    void openAboutAction() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/trashsoftware/deepSearcher2/fxml/aboutView.fxml"), bundle);
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle(bundle.getString("title"));
        stage.setScene(new Scene(root));

        stage.show();
    }

    // Factories and listeners

    private void setResultTableFactory() {
        TableColumn<ResultItem, ?> fileSizeCol = resultTable.getColumns().get(1);
        TableColumn<ResultItem, ?> fileTypeCol = resultTable.getColumns().get(2);
        TableColumn<ResultItem, ?> matchModeCol = resultTable.getColumns().get(3);
//        TableColumn<ResultItem, ?> infoCol = resultTable.getColumns().get(4);

        fileNameCol.setCellValueFactory(new PropertyValueFactory<>("Name"));
        fileSizeCol.setCellValueFactory(new PropertyValueFactory<>("Size"));
        fileTypeCol.setCellValueFactory(new PropertyValueFactory<>("Type"));
        matchModeCol.setCellValueFactory(new PropertyValueFactory<>("Mode"));
//        infoCol.setCellValueFactory(new PropertyValueFactory<>("Info"));
    }

    private void setFormatTableFactory() {
        TableColumn<FormatItem, ?> checkCol = formatTable.getColumns().get(0);
        TableColumn<FormatItem, ?> descriptionCol = formatTable.getColumns().get(1);
        TableColumn<FormatItem, ?> extCol = formatTable.getColumns().get(2);

        checkCol.setCellValueFactory(new PropertyValueFactory<>("CheckBox"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("Description"));
        extCol.setCellValueFactory(new PropertyValueFactory<>("DottedExtension"));
    }

    private void addFileNameColumnHoverListener() {
        fileNameCol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<ResultItem, String> call(TableColumn<ResultItem, String> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item);

                            hoverProperty().addListener((ObservableValue<? extends Boolean> obs, Boolean wasHovered,
                                                         Boolean isNowHovered) -> {
                                if (isNowHovered && !isEmpty()) {
                                    Tooltip tp = new Tooltip();
                                    tp.setText(getText());

                                    resultTable.setTooltip(tp);
                                } else {
                                    resultTable.setTooltip(null);
                                }
                            });
                        }
                    }
                };
            }
        });
    }

    private void addFormatNameColumnHoverListener() {
        formatNameCol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<FormatItem, String> call(TableColumn<FormatItem, String> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item);

                            hoverProperty().addListener((ObservableValue<? extends Boolean> obs, Boolean wasHovered,
                                                         Boolean isNowHovered) -> {
                                if (isNowHovered && !isEmpty()) {
                                    Tooltip tp = new Tooltip();
                                    tp.setText(getText());

                                    formatTable.setTooltip(tp);
                                } else {
                                    formatTable.setTooltip(null);
                                }
                            });
                        }
                    }
                };
            }
        });
    }

    private void addMatchingModeColumnHoverListener() {
        matchingModeCol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<ResultItem, String> call(TableColumn<ResultItem, String> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item);

                            hoverProperty().addListener((ObservableValue<? extends Boolean> obs, Boolean wasHovered,
                                                         Boolean isNowHovered) -> {
                                if (isNowHovered && !isEmpty()) {
                                    ResultItem res = getTableRow().getItem();
                                    if (res != null) {
                                        String tips = res.showInfo();
                                        Tooltip tp = new Tooltip();
                                        tp.setText(tips);

                                        resultTable.setTooltip(tp);
                                    }
                                } else {
                                    resultTable.setTooltip(null);
                                }
                            });
                        }
                    }
                };
            }
        });
    }

    private void addFilterBoxListener() {
        filterBox.getSelectionModel().selectedItemProperty().addListener(((observableValue, formatFilterItem, t1) ->
                formatTable.setFilter(t1)));
    }

    private void addResultTableClickListeners() {
        resultTable.setRowFactory(tableView -> {
            final TableRow<ResultItem> row = new TableRow<>();
            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem openFileMenu = new MenuItem(bundle.getString("openFile"));
            final MenuItem openLocationMenu = new MenuItem(bundle.getString("openFileLocation"));
            openFileMenu.setOnAction(event -> openFile(row.getItem().getFile()));
            openLocationMenu.setOnAction(event -> openFile(row.getItem().getFile().getParentFile()));
            contextMenu.getItems().addAll(openFileMenu, openLocationMenu);
            // Set context menu on row, but use a binding to make it only show for non-empty rows:
            row.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
                    openFile(row.getItem().getFile());
                }
            });
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu)
            );
            return row;
        });
    }

    private void addDirListListener() {
        dirList.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            deleteDirButton.setDisable(newValue.intValue() == -1);
        });
    }

    private void addTargetListListener() {
        searchItemsList.selectedIndexProperty().addListener(((observableValue, number, t1) -> {
            deleteTargetButton.setDisable(t1.intValue() == -1);
        }));
    }

    private void addCheckBoxesListeners() {
        searchContentBox.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (t1) {
                formatTable.setDisable(false);
                selectAllBox.setDisable(false);
                filterBox.setDisable(false);
            } else {
                formatTable.setDisable(true);
                selectAllBox.setDisable(true);
                filterBox.setDisable(true);
            }
            Configs.writeStringCache("searchContent", String.valueOf(t1));
        });
        matchWordBox.selectedProperty().addListener(((observableValue, aBoolean, t1) -> {
            if (t1) {
                matchRegexBox.setSelected(false);
            }
            Configs.writeStringCache("matchWord", String.valueOf(t1));
        }));
        matchRegexBox.selectedProperty().addListener(((observableValue, aBoolean, t1) -> {
            if (t1) {
                matchWordBox.setSelected(false);
            }
            Configs.writeStringCache("matchRegex", String.valueOf(t1));
        }));
        searchFileNameBox.selectedProperty().addListener(((observableValue, aBoolean, t1) -> {
            if (t1) {
                includeDirNameBox.setDisable(false);
            } else if (!searchDirNameBox.isSelected()) {
                includeDirNameBox.setDisable(true);
            }
        }));
        searchDirNameBox.selectedProperty().addListener(((observableValue, aBoolean, t1) -> {
            if (t1) {
                includeDirNameBox.setDisable(false);
            } else if (!searchFileNameBox.isSelected()) {
                includeDirNameBox.setDisable(true);
            }
        }));

        addCheckBoxBasicListener(searchFileNameBox, "searchFileName");
        addCheckBoxBasicListener(searchDirNameBox, "searchDirName");
        addCheckBoxBasicListener(includeDirNameBox, "includePathName");
        addCheckBoxBasicListener(matchCaseBox, "matchCase");
    }

    private void addRadioButtonsListeners() {
        matchAllRadioBtn.selectedProperty().addListener(((observableValue, aBoolean, t1) ->
                Configs.writeStringCache("matchAll", String.valueOf(t1))));
    }

    // Helper functions

    private void addOpenedDir(File file) {
        Configs.addToArrayCacheNoDup(Configs.OPENED_DIRS_KEY, file.getAbsolutePath());
    }

    private void deleteOpenedDir(File file) {
        Configs.removeFromArrayCache(Configs.OPENED_DIRS_KEY, file.getAbsolutePath());
    }

    private File getLastOpenedDir() {
        JSONArray lastOpens = lastOpenedDirs();
        return lastOpens.isEmpty() ? null : new File(lastOpens.getString(lastOpens.length() - 1));
    }

    private JSONArray lastOpenedDirs() {
        return Configs.getArrayCache(Configs.OPENED_DIRS_KEY);
    }

    private void loadRadioButtonsInitialStatus() {
        String value = Configs.getStringCache("matchAll");
        if (value != null) {
            boolean isAll = Boolean.parseBoolean(value);
            matchAllRadioBtn.setSelected(isAll);
            matchAnyRadioBtn.setSelected(!isAll);
        }
    }

    private void loadSavedCheckBoxesStatus() {
        setBoxInitialStatus(searchFileNameBox, "searchFileName");
        setBoxInitialStatus(searchDirNameBox, "searchDirName");
        setBoxInitialStatus(searchContentBox, "searchContent");
        setBoxInitialStatus(includeDirNameBox, "includePathName");
        setBoxInitialStatus(matchCaseBox, "matchCase");
        setBoxInitialStatus(matchWordBox, "matchWord");
        setBoxInitialStatus(matchRegexBox, "matchRegex");
    }

    private void setBoxInitialStatus(CheckBox checkBox, String key) {
        String value = Configs.getStringCache(key);
        if (value != null) {
            boolean checked = Boolean.parseBoolean(value);
            checkBox.setSelected(checked);
        }
    }

    private void addCheckBoxBasicListener(CheckBox checkBox, String key) {
        checkBox.selectedProperty().addListener(((observableValue, aBoolean, t1) ->
                Configs.writeStringCache(key, String.valueOf(t1))));
    }

    private void fillFormatTable() {
        Enumeration<String> keys = fileTypeBundle.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            FormatItem formatItem = new FormatItem(key, fileTypeBundle.getString(key));
            formatTable.addItem(formatItem);
        }
        Collections.sort(formatTable.getAllItems());
    }

    private void fillFilterBox() {
        filterBox.getItems().clear();
        filterBox.getItems().addAll(
                new FormatFilterItem(FormatFilterItem.FILTER_ALL, bundle.getString("allFiles")),
                new FormatFilterItem(FormatFilterItem.FILTER_TEXT, bundle.getString("textFiles")),
                new FormatFilterItem(FormatFilterItem.FILTER_CODES, bundle.getString("sourceCodeFiles")),
                new FormatFilterItem(FormatFilterItem.FILTER_MS_OFFICE, bundle.getString("officeFiles")),
                new FormatFilterItem(FormatFilterItem.FILTER_OTHERS, bundle.getString("otherFiles"))
        );
    }

    private void restoreSavedFormats() {
        Set<String> savedFormats = Configs.getAllFormats();
        for (FormatItem formatItem : formatTable.getAllItems())
            if (savedFormats.contains(formatItem.getExtension()))
                formatItem.getCheckBox().setSelected(true);
    }

    private void restoreLastOpenedDirs() {
        JSONArray lastOpens = lastOpenedDirs();
        for (Object dirObj : lastOpens) {
            dirList.getItems().add(new File((String) dirObj));
        }
    }

    private void selectAllFormats() {
        for (FormatItem formatItem : formatTable.getShowingItems()) {
            formatItem.getCheckBox().setSelected(true);
        }
    }

    private void deselectAllFormats() {
        for (FormatItem formatItem : formatTable.getShowingItems()) {
            formatItem.getCheckBox().setSelected(false);
        }
    }

    private void startSearching() {
        try {
            long beginTime = System.currentTimeMillis();
            PrefSet prefSet = new PrefSet.PrefSetBuilder()
                    .caseSensitive(false)
                    .setMatchAll(matchAllRadioBtn.isSelected())
                    .searchFileName(searchFileNameBox.isSelected())
                    .searchDirName(searchDirNameBox.isSelected())
                    .includePathName(includeDirNameBox.isSelected())
                    .caseSensitive(matchCaseBox.isSelected())
                    .matchWord(matchWordBox.isSelected())
                    .matchRegex(matchRegexBox.isSelected())
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
                searchingFailed();
                e.getSource().getException().printStackTrace();
            });

            Configs.addHistory(prefSet);
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
        setNotInSearchingUi(normalFinish ?
                bundle.getString("searchDone") : bundle.getString("searchAbort"));
    }

    private void searchingFailed() {
        setNotInSearchingUi(bundle.getString("searchFailed"));
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

    private void setNotInSearchingUi(String statusMsg) {
        isSearching = false;
        searchButton.setText(bundle.getString("search"));
        progressIndicator.setVisible(false);
        progressIndicator.setManaged(false);
        searchingStatusText.setText(statusMsg);
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

    private void openFile(File file) {
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> getTargets() {
        List<String> list = new ArrayList<>();
        for (Node node : searchItemsList.getTextFields()) {
            list.add(((TextField) node).getText());
        }
        return list;
    }

    /**
     * @return the set of all selected file extensions if "search content" is selected, {@code null} otherwise.
     */
    private Set<String> getExtensions() {
        if (searchContentBox.isSelected()) {
            Set<String> set = new HashSet<>();
            for (FormatItem formatItem : formatTable.getAllItems()) {
                if (formatItem.getCheckBox().isSelected()) {
                    set.add(formatItem.getExtension());
                }
            }
            return set;
        } else
            return null;
    }

    private class SearchService extends Service<Void> {
        private final Searcher searcher;

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
