package trashsoftware.deepSearcher2.searcher;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.collections.ObservableList;
import trashsoftware.deepSearcher2.items.ResultItem;
import trashsoftware.deepSearcher2.util.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public class Searcher {

    private static final Set<String> PLAIN_TEXT_FORMAT = Set.of(
            "bat", "c", "cmd", "cpp", "h", "java", "js", "log", "py", "txt"
    );

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
            // check file content is selected
            if (prefSet.getExtensions() != null) {
                matchFileContent(file);
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

        addResult(file, true, false);
        updateResultCount();
    }

    private void matchNameAny(File file) {
        String name = getSearchingFileName(file);

        StringMatcher matcher = createMatcher(name);
        for (String target : prefSet.getTargets()) {
            if (matcher.contains(target)) {
                addResult(file, true, false);
                updateResultCount();
                return;
            }
        }
    }

    private void matchFileContent(File file) {
        String ext = Util.getFileExtension(file.getName());
        if (prefSet.getExtensions().contains(ext)) {
            String content;
            if (PLAIN_TEXT_FORMAT.contains(ext)) {
                content = readPlainTextFromFile(file);
            } else {
                throw new RuntimeException("Unknown format");
            }
            if (prefSet.isMatchAll()) matchContentAll(content, file);
            else matchContentAny(content, file);
        }
    }

    private void matchContentAll(String string, File file) {
        StringMatcher matcher = createMatcher(string);
        for (String target : prefSet.getTargets()) {
            if (!matcher.contains(target)) return;
        }

        addResult(file, false, true);
        updateResultCount();
    }

    private void matchContentAny(String string, File file) {
        StringMatcher matcher = createMatcher(string);
        for (String target : prefSet.getTargets()) {
            if (matcher.contains(target)) {
                addResult(file, false, true);
                updateResultCount();
                return;
            }
        }
    }

    private String readPlainTextFromFile(File file) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line).append('\n');
            }
            bufferedReader.close();
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
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
        if (prefSet.getMatchMode() == PrefSet.NORMAL) {
            if (prefSet.getMatchingAlgorithm().equals("algNative")) {
                return new NativeMatcher(string);
            } else if (prefSet.getMatchingAlgorithm().equals("algNaive")) {
                return new NaiveMatcher(string);
            } else {
                throw new RuntimeException("Not a valid matching algorithm");
            }
        } else if (prefSet.getMatchMode() == PrefSet.WORD) {
            if (prefSet.getWordMatchingAlgorithm().equals("algNative")) {
                return new NativeWordMatcher(string);
            } else if (prefSet.getWordMatchingAlgorithm().equals("algNaive")) {
                return new NaiveWordMatcher(string);
            } else {
                throw new RuntimeException("Not a valid matching algorithm for words");
            }
        } else if (prefSet.getMatchMode() == PrefSet.REGEX) {
            if (prefSet.getRegexAlgorithm().equals("algNative")) {
                return new NativeRegexMatcher(string);
            } else {
                throw new RuntimeException("Not a valid matching algorithm for regex");
            }
        } else {
            throw new RuntimeException("Invalid match mode");
        }
    }

    private void updateResultCount() {
        resultCountWrapper.setValue(tableList.size());
    }

    private void addResult(File file, boolean matchName, boolean matchContent) {
        // check if previous one is this one
        if (!tableList.isEmpty()) {
            ResultItem lastItem = tableList.get(tableList.size() - 1);
            if (lastItem.isSameFileAs(file)) {
                lastItem.addMatchContent();
                return;
            }
        }

        ResultItem resultItem = new ResultItem(file, matchName, matchContent, bundle, fileTypeBundle);
        tableList.add(resultItem);
    }

    public boolean isNormalFinish() {
        return searching;
    }

    public ReadOnlyIntegerProperty resultCountProperty() {
        return resultCountWrapper;
    }
}
