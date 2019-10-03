package trashsoftware.deepSearcher2.searcher;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.collections.ObservableList;
import trashsoftware.deepSearcher2.items.ResultItem;
import trashsoftware.deepSearcher2.util.Configs;

import java.io.File;
import java.util.List;
import java.util.ResourceBundle;

public class Searcher {

    private PrefSet prefSet;

    private ReadOnlyIntegerWrapper resultCountWrapper = new ReadOnlyIntegerWrapper();

    private ObservableList<ResultItem> tableList;
    private ResourceBundle bundle;
    private ResourceBundle fileTypeBundle;

    private boolean searching = true;

    public Searcher(PrefSet prefSet, ObservableList<ResultItem> tableList, ResourceBundle bundle,
                    ResourceBundle fileTypeBundle) {
        this.prefSet = prefSet;
        this.tableList = tableList;
        this.bundle = bundle;
        this.fileTypeBundle = fileTypeBundle;
    }

    public void search() {
        for (File dir : prefSet.getSearchDirs()) {
            searchFile(dir);
        }
    }

    public void stop() {
        searching = false;
    }

    private void searchFile(File file) {
        if (!searching) return;

        if (file.isDirectory()) {
            // Check dir is selected
            if (prefSet.isDirName()) {
                matchName(file);
            }

            File[] files = file.listFiles();
            if (files == null) return;
            for (File f : files) {
                searchFile(f);
            }

        } else {
            // check file name is selected
            if (prefSet.isFileName()) {
                matchName(file);
            }
        }
    }

    private void matchName(File file) {
        if (prefSet.isMatchAll()) matchNameAll(file);
        else matchNameAny(file);
    }

    private void matchNameAll(File file) {
        String name = getSearchingFileName(file);

        StringMatcher matcher = createMatcher(name);
        for (String target : prefSet.getTargets()) {
            if (!matcher.contains(target)) return;
        }

        addResult(file, ResultItem.MATCH_NAME);
        updateResultCount();
    }

    private void matchNameAny(File file) {
        String name = getSearchingFileName(file);

        StringMatcher matcher = createMatcher(name);
        for (String target : prefSet.getTargets()) {
            if (matcher.contains(target)) {
                addResult(file, ResultItem.MATCH_NAME);
                updateResultCount();
                return;
            }
        }
    }

    private String getSearchingFileName(File file) {
        if (prefSet.isIncludeDirName()) {
            if (prefSet.isMatchCase()) return file.getAbsolutePath();
            else return file.getAbsolutePath().toLowerCase();
        } else {
            if (prefSet.isMatchCase()) return file.getName();
            else return file.getName().toLowerCase();
        }
    }

    private StringMatcher createMatcher(String string) {
        if (prefSet.getMatchingAlgorithm().equals("algNative")) {
            return new NativeMatcher(string);
        } else if (prefSet.getMatchingAlgorithm().equals("algNaive")) {
            return new NaiveMatcher(string);
        } else {
            throw new RuntimeException("Not a valid matching algorithm");
        }
    }

    private void updateResultCount() {
        resultCountWrapper.setValue(tableList.size());
    }

    private void addResult(File file, int matchMode) {
        // check if previous one is this one


        ResultItem resultItem = new ResultItem(file, matchMode, bundle, fileTypeBundle);
        // TODO
        tableList.add(resultItem);
    }

    public boolean isNormalFinish() {
        return searching;
    }

    public ReadOnlyIntegerProperty resultCountProperty() {
        return resultCountWrapper;
    }
}
