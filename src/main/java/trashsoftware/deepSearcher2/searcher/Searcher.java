package trashsoftware.deepSearcher2.searcher;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.collections.ObservableList;
import trashsoftware.deepSearcher2.guiItems.ResultItem;
import trashsoftware.deepSearcher2.searcher.contentSearchers.*;
import trashsoftware.deepSearcher2.searcher.matchers.MatcherFactory;
import trashsoftware.deepSearcher2.searcher.matchers.StringMatcher;
import trashsoftware.deepSearcher2.util.Util;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Searcher {

    private static final Set<String> PLAIN_TEXT_FORMAT = Set.of(
            "bat", "c", "cmd", "cpp", "h", "java", "js", "log", "py", "txt"
    );

    private static final Map<String, Class<? extends ContentSearcher>> FORMAT_MAP = Map.of(
            "pdf", PdfSearcher.class,
            "doc", DocSearcher.class,
            "docx", DocxSearcher.class,
            "ppt", PptSearcher.class,
            "pptx", PptxSearcher.class,
            "xls", XlsSearcher.class,
            "xlsx", XlsxSearcher.class
    );

    private final PrefSet prefSet;

    private final ReadOnlyIntegerWrapper resultCountWrapper = new ReadOnlyIntegerWrapper();

    private final ObservableList<ResultItem> tableList;

    /**
     * This list tracks the added order, since the {@code tableList} will be sorted if user select 'sort' on
     * TableView.
     */
    private final Deque<ResultItem> orderTrackingList = new ArrayDeque<>();

    private final ResourceBundle bundle;
    private final ResourceBundle fileTypeBundle;

    private boolean searching = true;

    private final MatcherFactory nameMatcherFactory;

    public Searcher(PrefSet prefSet, ObservableList<ResultItem> tableList, ResourceBundle bundle,
                    ResourceBundle fileTypeBundle) {
        this.prefSet = prefSet;
        this.tableList = tableList;
        this.bundle = bundle;
        this.fileTypeBundle = fileTypeBundle;
        this.nameMatcherFactory = MatcherFactory.createFactoryByPrefSet(prefSet);
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
        if (!searching) return;  // Check if searching is cancelled

        if (file.isDirectory()) {
            // Check if this directory is excluded
            if (prefSet.getExcludedDirs().contains(file.getAbsolutePath())) return;

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
            // Check if this format is excluded
            if (prefSet.getExcludedFormats().contains(Util.getFileExtension(file.getName()))) return;

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

        StringMatcher matcher = nameMatcherFactory.createMatcher(name);
        for (String target : prefSet.getTargets()) {
            if (matcher.search(target) < 0) return;
        }

        addNameResult(file);
    }

    private void matchNameAny(File file) {
        String name = getSearchingFileName(file);

        StringMatcher matcher = nameMatcherFactory.createMatcher(name);
        for (String target : prefSet.getTargets()) {
            if (matcher.search(target) >= 0) {
                addNameResult(file);
                return;
            }
        }
    }

    private void matchFileContent(File file) {
        String ext = Util.getFileExtension(file.getName());
        MatcherFactory matcherFactory = MatcherFactory.createFactoryByPrefSet(prefSet);
        if (prefSet.getExtensions().contains(ext)) {
            ContentSearcher searcher;
            if (PLAIN_TEXT_FORMAT.contains(ext)) {
                searcher = new PlainTextSearcher(file, matcherFactory, prefSet.isCaseSensitive());
            } else if (FORMAT_MAP.containsKey(ext)) {
                try {
                    searcher = FORMAT_MAP.get(ext)
                            .getDeclaredConstructor(File.class, MatcherFactory.class, boolean.class)
                            .newInstance(file, matcherFactory, prefSet.isCaseSensitive());
                } catch (InvocationTargetException |
                        NoSuchMethodException |
                        InstantiationException |
                        IllegalAccessException e) {
                    throw new InvalidClassException("Unexpected file content searcher. ", e);
                }
            } else {
                throw new RuntimeException("Unknown format");
            }
            ContentSearchingResult result;
            if (prefSet.isMatchAll()) {
                result = searcher.searchAll(prefSet.getTargets());
            } else {
                result = searcher.searchAny(prefSet.getTargets());
            }
            if (result != null) addContentResult(file, result);
        }
    }

    private String getSearchingFileName(File file) {
        if (prefSet.isIncludePathName()) {
            if (prefSet.isCaseSensitive()) return file.getAbsolutePath();
            else return file.getAbsolutePath().toLowerCase();
        } else {
            if (prefSet.isCaseSensitive()) return file.getName();
            else return file.getName().toLowerCase();
        }
    }

//    private Class<? extends StringMatcher> getMatcherClass() {
//        if (prefSet.getMatchMode() == PrefSet.NORMAL) {
//            switch (prefSet.getMatchingAlgorithm()) {
//                case "algNative":
//                    return NativeMatcher.class;
//                case "algNaive":
//                    return NaiveMatcher.class;
//                case "algKmp":
//                    return KMPMatcher.class;
//                case "algSunday":
//                    return SundayMatcher.class;
//                default:
//                    throw new RuntimeException("Not a valid matching algorithm");
//            }
//        } else if (prefSet.getMatchMode() == PrefSet.WORD) {
//            switch (prefSet.getWordMatchingAlgorithm()) {
//                case "algNaive":
//                    return NaiveWordMatcher.class;
//                case "algHash":
//                    return HashedWordMatcher.class;
//                default:
//                    throw new RuntimeException("Not a valid matching algorithm for words");
//            }
//        } else if (prefSet.getMatchMode() == PrefSet.REGEX) {
//            if (prefSet.getRegexAlgorithm().equals("algNative")) {
//                return NativeRegexMatcher.class;
//            } else {
//                throw new RuntimeException("Not a valid matching algorithm for regex");
//            }
//        } else {
//            throw new RuntimeException("Invalid match mode");
//        }
//    }

    private void updateResultCount() {
        resultCountWrapper.setValue(tableList.size());
    }

    private void addContentResult(File file, ContentSearchingResult csr) {
        // check if previous one is this one
        // This situation occurs when this file is already matched by name successfully
        if (!orderTrackingList.isEmpty()) {
            ResultItem lastItem = orderTrackingList.getLast();
            if (lastItem.isSameFileAs(file)) {
                lastItem.setContentRes(csr);
                return;
            }
        }

        ResultItem resultItem = ResultItem.createContentMatch(file, bundle, fileTypeBundle, csr);
        tableList.add(resultItem);
        orderTrackingList.addLast(resultItem);
        updateResultCount();
    }

    private void addNameResult(File file) {
        ResultItem resultItem = ResultItem.createNameMatch(file, bundle, fileTypeBundle);
        tableList.add(resultItem);
        orderTrackingList.addLast(resultItem);
        updateResultCount();
    }

    public boolean isNormalFinish() {
        return searching;
    }

    public ReadOnlyIntegerProperty resultCountProperty() {
        return resultCountWrapper;
    }
}
