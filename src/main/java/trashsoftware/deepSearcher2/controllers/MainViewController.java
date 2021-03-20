package trashsoftware.deepSearcher2.controllers;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringWrapper;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.json.JSONArray;
import org.json.JSONObject;
import trashsoftware.deepSearcher2.controllers.settingsPages.SearchingOptionsPage;
import trashsoftware.deepSearcher2.controllers.widgets.FormatTable;
import trashsoftware.deepSearcher2.controllers.widgets.TextFieldList;
import trashsoftware.deepSearcher2.guiItems.FormatFilterItem;
import trashsoftware.deepSearcher2.guiItems.FormatItem;
import trashsoftware.deepSearcher2.guiItems.FormatType;
import trashsoftware.deepSearcher2.guiItems.ResultItem;
import trashsoftware.deepSearcher2.searcher.*;
import trashsoftware.deepSearcher2.util.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.*;

public class MainViewController implements Initializable, CacheObservable {

    private final ResourceBundle fileTypeBundle = Client.getFileTypeBundle();
    @FXML
    GridPane basePane;
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
    CheckBox searchFileNameBox, searchDirNameBox, searchContentBox;
    @FXML
    CheckBox matchCaseBox, matchWordBox, matchRegexBox;
    @FXML
    CheckBox selectAllBox;
    @FXML
    CheckMenuItem showFullPathMenu, showExtMenu;
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
    private Stage thisStage;
    private boolean isSearching;
    private File dirDialogInitFile;

    private SearchService service;

    private ChangeListener<Number> fileCountListener;

    private boolean showingSelectAll = true;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bundle = resourceBundle;

        setDirListFactory();
        setResultTableFactory();
        setFormatTableFactory();

        addFileNameColumnHoverListener();
        addMatchingModeColumnHoverListener();
        addFormatNameColumnHoverListener();
        addResultTableClickListeners();
        addTargetListListener();

        addCheckBoxesListeners();
        addCheckMenuListeners();
        addFilterBoxListener();

        addSearchItem();  // Add a default search field

        fillFormatTable();
        fillFilterBox();
        filterBox.getSelectionModel().select(0);  // this step must run after 'fillFormatTable()'

        initContextMenus();

        // restore saved status
        // this should be called after all listeners are set
        loadFromCache();
    }

    public void setStage(Stage stage) {
        thisStage = stage;
    }

    public void rescaleUi(int fontSize) {
        double defaultC1 = basePane.getColumnConstraints().get(0).getPercentWidth();
        double expectedTimes = 1 + ((double) fontSize / 12 - 1) * 0.75;
        double times = Math.max(0.75, Math.min(2, expectedTimes));
        double percentageWidth1 = times * defaultC1;
        double percentageWidth2 = 100 - percentageWidth1;
        basePane.getColumnConstraints().get(1).setPercentWidth(percentageWidth2);
        basePane.getColumnConstraints().get(0).setPercentWidth(percentageWidth1);
    }

    // Controls

    @FXML
    void addSearchDir() {
        DirectoryChooser dc = new DirectoryChooser();
        if (dirDialogInitFile != null && dirDialogInitFile.exists())
            dc.setInitialDirectory(dirDialogInitFile);
        File dir = dc.showDialog(thisStage);
        if (dir != null) {
            if (!dirList.getItems().contains(dir)) {
                dirList.getItems().add(dir);
                dirDialogInitFile = dir.getParentFile();
            }
        }
    }

    @FXML
    void addSearchItem() {
        TextField textField = new TextField();
        textField.setPromptText(bundle.getString("searchPrompt"));
        textField.setOnAction(e -> {  // the action of pressing 'Enter' in text field
            if (!isSearching) startSearching();
        });
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
        openSettings();
    }

    @FXML
    void openHistoryAction() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/trashsoftware/deepSearcher2/fxml/historyListView.fxml"), bundle);
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.initOwner(thisStage);
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle(bundle.getString("history"));
        stage.getIcons().add(Client.getIconImage());

        Scene scene = new Scene(root);
        if (Configs.getConfigs().isUseCustomFont()) {
            Configs.getConfigs().applyCustomFont(scene);
        }
        stage.setScene(scene);

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
        stage.initOwner(thisStage);
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle(bundle.getString("appName"));
        stage.getIcons().add(Client.getIconImage());
        stage.setScene(new Scene(root));

        stage.show();
    }

    @FXML
    void moreOptionAction() throws IOException {
        SettingsPanelController controller = openSettings();
        controller.expandUntil(SearchingOptionsPage.class);
    }

    @FXML
    void exitAction() {
        thisStage.close();
    }

    @FXML
    void restartAction() {
        Client.restartClient();
    }

    @Override
    public void putCache(JSONObject rootObject) {
        rootObject.put("searchFileName", searchFileNameBox.isSelected());
        rootObject.put("searchDirName", searchDirNameBox.isSelected());

        rootObject.put("searchContent", searchContentBox.isSelected());
        rootObject.put("matchCase", matchCaseBox.isSelected());
        rootObject.put("matchWord", matchWordBox.isSelected());
        rootObject.put("matchRegex", matchRegexBox.isSelected());
        rootObject.put("matchAll", matchAllRadioBtn.isSelected());
        rootObject.put("showFullPath", showFullPathMenu.isSelected());
        rootObject.put("showExt", showExtMenu.isSelected());

        if (dirDialogInitFile != null) rootObject.put("dirDialogInit", dirDialogInitFile.getAbsolutePath());

        rootObject.put(Cache.OPENED_DIRS_KEY, new JSONArray(dirList.getItems()));

        JSONArray selectedFmts = new JSONArray();
        for (FormatItem fi : formatTable.getItems()) {
            if (fi.getCheckBox().isSelected()) selectedFmts.put(fi.getExtension());
        }
        rootObject.put(Cache.FORMATS_KEY, selectedFmts);
    }

    @Override
    public void loadFromCache(Cache cache) {
        restoreSavedFormats(cache);
        restoreLastOpenedDirs(cache);
        loadRadioButtonsInitialStatus(cache);
        loadSavedCheckBoxesStatus(cache);
        loadSavedCheckMenusStatus(cache);
        String initD = cache.getStringCache("dirDialogInit");
        if (initD != null) dirDialogInitFile = new File(initD);
    }

    /**
     * Stops the current running searcher, if exists.
     * <p>
     * This method is used when the program is closed by user while a searcher is running.
     */
    public void stopActiveSearcher() {
        if (service != null) {
            service.getSearcher().stop();
        }
    }

    private void loadFromCache() {
        loadFromCache(Cache.getCache());
    }

    private void clearSearchDirs() {
        dirList.getItems().clear();
    }

    private void clearSearchItems() {
        searchItemsList.getTextFields().clear();
    }

    private SettingsPanelController openSettings() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/trashsoftware/deepSearcher2/fxml/settingsPanel.fxml"), bundle);
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.initOwner(thisStage);
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle(bundle.getString("settings"));
        stage.getIcons().add(Client.getIconImage());
        Scene scene = new Scene(root);
        if (Configs.getConfigs().isUseCustomFont()) {
            Configs.getConfigs().applyCustomFont(scene);
        }
        stage.setScene(scene);

        SettingsPanelController controller = loader.getController();
        controller.setStage(stage, this);

        stage.show();
        return controller;
    }

    // Factories and listeners

    private void setDirListFactory() {
        dirList.setCellFactory(TextFieldListCell.forListView(new StringConverter<>() {
            @Override
            public String toString(File object) {
                return object.getAbsolutePath();
            }

            @Override
            public File fromString(String string) {
                return new File(string);
            }
        }));
        dirList.setEditable(true);
        dirList.setOnEditCommit(event -> {
            File nv = event.getNewValue();
            if (nv.exists()) {
                dirList.getItems().set(event.getIndex(), nv);
            }
        });
        dirList.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) ->
                deleteDirButton.setDisable(newValue.intValue() == -1));
    }

    private void setResultTableFactory() {
        TableColumn<ResultItem, ?> fileSizeCol = resultTable.getColumns().get(1);
        TableColumn<ResultItem, ?> fileTypeCol = resultTable.getColumns().get(2);
        TableColumn<ResultItem, ?> matchModeCol = resultTable.getColumns().get(3);

        fileNameCol.setCellValueFactory(param -> {
//            File file = param.getValue().getFile();
            ResultItem resultItem = param.getValue();
            String name = showFullPathMenu.isSelected() ?
                    resultItem.getFullPath() : resultItem.getSimpleName();
            if (!showExtMenu.isSelected() && !resultItem.isDir()) {
                int dotIndex = name.lastIndexOf('.');
                if (dotIndex > 0) name = name.substring(0, dotIndex);
            }
            return new ReadOnlyStringWrapper(name);
        });
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
        extCol.setCellValueFactory(new PropertyValueFactory<>("DottedExtension"));
    }

    private void addFileNameColumnHoverListener() {
        fileNameCol.setCellFactory(new ResTableCallback<>(resultTable) {
            @Override
            protected String tooltipText(ResultItem cellData) {
                return cellData.getFullPath();
            }
        });
    }

    private void addFormatNameColumnHoverListener() {
        formatNameCol.setCellFactory(new ResTableCallback<>(formatTable) {
            @Override
            protected String tooltipText(FormatItem cellData) {
                return cellData.getDescription();
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
                                    String tips;
                                    if (res != null && (tips = res.showInfo()) != null) {
                                        Tooltip tp = new Tooltip();
                                        tp.setShowDuration(new Duration(10000));
                                        tp.setText(tips);
                                        resultTable.setTooltip(tp);
                                        return;
                                    }
                                }
                                resultTable.setTooltip(null);
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
            openFileMenu.setOnAction(event -> row.getItem().open());
            openLocationMenu.setOnAction(event -> row.getItem().openParentDir());
            contextMenu.getItems().addAll(openFileMenu, openLocationMenu);
            // Set context menu on row, but use a binding to make it only show for non-empty rows:
            row.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
                    row.getItem().open();
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

    private void addTargetListListener() {
        searchItemsList.selectedIndexProperty().addListener(((observableValue, number, t1) ->
                deleteTargetButton.setDisable(t1.intValue() == -1)));
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

    private void addCheckMenuListeners() {
        showFullPathMenu.selectedProperty().addListener(((observable, oldValue, newValue) ->
                resultTable.refresh()));
        showExtMenu.selectedProperty().addListener(((observable, oldValue, newValue) ->
                resultTable.refresh()));
    }

    // Helper functions

    private JSONArray lastOpenedDirs(Cache cache) {
        return cache.getArrayCache(Cache.OPENED_DIRS_KEY);
    }

    private void loadRadioButtonsInitialStatus(Cache cache) {
        boolean isAll = cache.getBooleanCache("matchAll", false);
        matchAllRadioBtn.setSelected(isAll);
        matchAnyRadioBtn.setSelected(!isAll);
    }

    private void loadSavedCheckBoxesStatus(Cache cache) {
        setBoxInitialStatus(searchFileNameBox, "searchFileName", cache);
        setBoxInitialStatus(searchDirNameBox, "searchDirName", cache);
        setBoxInitialStatus(searchContentBox, "searchContent", cache);
        setBoxInitialStatus(matchCaseBox, "matchCase", cache);
        setBoxInitialStatus(matchWordBox, "matchWord", cache);
        setBoxInitialStatus(matchRegexBox, "matchRegex", cache);
    }

    private void loadSavedCheckMenusStatus(Cache cache) {
        showFullPathMenu.setSelected(cache.getBooleanCache("showFullPath", true));
        showExtMenu.setSelected(cache.getBooleanCache("showExt", true));
    }

    private void setBoxInitialStatus(CheckBox checkBox, String key, Cache cache) {
        boolean checked = cache.getBooleanCache(key, false);
        checkBox.setSelected(checked);
    }

    public void refreshFormatTable() {
        Set<String> selected = formatTable.getSelectedFormats();
        formatTable.initialize();
        fillFormatTable();
        formatTable.selectFormats(selected);
        // manually trigger the change listener of filters
        int index = filterBox.getSelectionModel().getSelectedIndex();
        filterBox.getSelectionModel().select(index == 0 ? 1 : 0);
        filterBox.getSelectionModel().select(index);
    }

    private void fillFormatTable() {
        Enumeration<String> keys = fileTypeBundle.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            FormatItem formatItem = new FormatItem(key, fileTypeBundle.getString(key));
            formatTable.addItem(formatItem);
        }
        Map<String, String> customFormats = Configs.getConfigs().getAllCustomFormats();
        for (Map.Entry<String, String> extDes : customFormats.entrySet()) {
            FormatItem formatItem = new FormatItem(extDes.getKey(), extDes.getValue());
            formatTable.addItem(formatItem);
        }
        formatTable.setCustomFormats(customFormats);
        Collections.sort(formatTable.getAllItems());
    }

    private void fillFilterBox() {
        filterBox.getItems().clear();
        filterBox.getItems().addAll(
                new FormatFilterItem(FormatType.ALL, bundle.getString("allFiles"), formatTable),
                new FormatFilterItem(FormatType.TEXT, bundle.getString("textFiles"), formatTable),
                new FormatFilterItem(FormatType.CODES, bundle.getString("sourceCodeFiles"), formatTable),
                new FormatFilterItem(FormatType.MS_OFFICE, bundle.getString("officeFiles"), formatTable),
                new FormatFilterItem(FormatType.DOCUMENTS, bundle.getString("documentFiles"), formatTable),
                new FormatFilterItem(FormatType.OTHERS, bundle.getString("otherFiles"), formatTable),
                new FormatFilterItem(FormatType.CUSTOMS, bundle.getString("custom"), formatTable)
        );
    }

    private void initContextMenus() {
        MenuItem addD = new MenuItem(bundle.getString("addItem"));
        addD.setOnAction(e -> addSearchDir());
        MenuItem clearD = new MenuItem(bundle.getString("clearItems"));
        clearD.setOnAction(e -> clearSearchDirs());
        MenuItem deleteDirMenu = new MenuItem(bundle.getString("deleteItem"));
        deleteDirMenu.setDisable(true);
        deleteDirMenu.setOnAction(e -> {
            int selected = dirList.getSelectionModel().getSelectedIndex();
            if (selected >= 0) dirList.getItems().remove(selected);
        });
        dirList.setContextMenu(new ContextMenu(deleteDirMenu, addD, clearD));

        dirList.setOnContextMenuRequested(e -> {
            Node node = e.getPickResult().getIntersectedNode();
            boolean enabled = (node instanceof TextFieldListCell && ((TextFieldListCell<?>) node).getItem() != null)
                    || node instanceof Text;
            deleteDirMenu.setDisable(!enabled);
        });

        MenuItem addS = new MenuItem(bundle.getString("addItem"));
        addS.setOnAction(e -> addSearchItem());
        MenuItem clearS = new MenuItem(bundle.getString("clear"));
        clearS.setOnAction(e -> clearSearchItems());
        searchItemsList.setContextMenu(new ContextMenu(addS, clearS));
    }

    private void restoreSavedFormats(Cache cache) {
        JSONArray array = cache.getArrayCache(Cache.FORMATS_KEY);
        Set<String> savedFormats = new HashSet<>();
        for (Object obj : array) {
            savedFormats.add((String) obj);
        }
        for (FormatItem formatItem : formatTable.getAllItems())
            if (savedFormats.contains(formatItem.getExtension()))
                formatItem.getCheckBox().setSelected(true);
    }

    private void restoreLastOpenedDirs(Cache cache) {
        JSONArray lastOpens = lastOpenedDirs(cache);
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
                    .caseSensitive(matchCaseBox.isSelected())
                    .matchWord(matchWordBox.isSelected())
                    .matchRegex(matchRegexBox.isSelected())
                    .setTargets(getTargets())
                    .setSearchDirs(dirList.getItems())
                    .setExtensions(getExtensions())
                    .build();

            resultTable.getItems().clear();
            setInSearchingUi();

            Searcher searcher = new Searcher(
                    prefSet,
                    resultTable,
                    formatTable.getCustomFormats());
            service = new SearchService(searcher);

            service.setOnSucceeded(e -> {
                unbindListeners();
                finishSearching(searcher.isNormalFinish());
                setTimerTexts(System.currentTimeMillis() - beginTime);
                resultTable.setPlaceholder(new Label(bundle.getString("resTablePlaceHolder")));
                System.gc();
            });

            service.setOnFailed(e -> {
                unbindListeners();
                searchingFailed();
                e.getSource().getException().printStackTrace();
                EventLogger.log(e.getSource().getException());
                resultTable.setPlaceholder(new Label(bundle.getString("resTablePlaceHolder")));
                System.gc();
            });

            Configs.addHistory(prefSet);
            resultTable.setPlaceholder(new Label(bundle.getString("isSearching")));
            service.start();
        } catch (SearchTargetNotSetException e) {
            showSearchButtonMsg("targetNotSet", searchButton);
        } catch (SearchDirNotSetException e) {
            showSearchButtonMsg("dirNotSet", searchButton);
        } catch (SearchPrefNotSetException e) {
            showSearchButtonMsg("prefNotSet", searchButton);
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
        searchButton.setDisable(true);
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
        searchButton.setDisable(false);
        progressIndicator.setVisible(false);
        progressIndicator.setManaged(false);
        searchingStatusText.setText(statusMsg);
        statusSuffixText.setText(bundle.getString("searchDoneSuffix"));
    }

    private void showSearchButtonMsg(String textKey, Node parent) {
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
     * @return the set of all selected file extensions if "search content" is selected, {@code null} otherwise.
     */
    private Set<String> getExtensions() {
        if (searchContentBox.isSelected()) {
            return formatTable.getSelectedFormats();
        } else
            return null;
    }

    private void unbindListeners() {
        service.getSearcher().resultCountProperty().removeListener(fileCountListener);
    }

    private abstract static class ResTableCallback<T> implements
            Callback<TableColumn<T, String>, TableCell<T, String>> {

        private final TableView<T> table;

        private ResTableCallback(TableView<T> table) {
            this.table = table;
        }

        protected abstract String tooltipText(T cellData);

        @Override
        public TableCell<T, String> call(TableColumn<T, String> param) {
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
                                Tooltip tt = new Tooltip(tooltipText(getTableRow().getItem()));
                                tt.setShowDuration(new Duration(10000));
                                table.setTooltip(tt);
                            } else {
                                table.setTooltip(null);
                            }
                        });
                    }
                }
            };
        }
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

        public Searcher getSearcher() {
            return searcher;
        }
    }
}
