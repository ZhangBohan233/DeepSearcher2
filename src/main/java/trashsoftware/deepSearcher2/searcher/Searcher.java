package trashsoftware.deepSearcher2.searcher;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.collections.ObservableList;
import trashsoftware.deepSearcher2.guiItems.ResultItem;
import trashsoftware.deepSearcher2.util.Util;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public class Searcher {

    private static final Set<String> PLAIN_TEXT_FORMAT = Set.of(
            "bat", "c", "cmd", "cpp", "h", "java", "js", "log", "py", "txt"
    );

    private static final Map<String, Class<? extends ContentSearcher>> FORMAT_MAP = Map.of(
            "pdf", PdfReader.class,
            "doc", DocReader.class,
            "docx", DocxReader.class
    );

    private final PrefSet prefSet;

    private final ReadOnlyIntegerWrapper resultCountWrapper = new ReadOnlyIntegerWrapper();

    private final ObservableList<ResultItem> tableList;
    private final ResourceBundle bundle;
    private final ResourceBundle fileTypeBundle;

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
                try {
                    matchFileContent(file);
                } catch (InstantiationException |
                        IllegalAccessException |
                        InvocationTargetException |
                        NoSuchMethodException e) {
                    throw new RuntimeException("Unexpected error when creating content searcher", e);
                }
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
            if (matcher.search(target) < 0) return;
        }

        addNameResult(file);
        updateResultCount();
    }

    private void matchNameAny(File file) {
        String name = getSearchingFileName(file);

        StringMatcher matcher = createMatcher(name);
        for (String target : prefSet.getTargets()) {
            if (matcher.search(target) >= 0) {
                addNameResult(file);
                updateResultCount();
                return;
            }
        }
    }

    private void matchFileContent(File file)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String ext = Util.getFileExtension(file.getName());
        Class<? extends StringMatcher> matcherClass = getMatcherClass();
        if (prefSet.getExtensions().contains(ext)) {
            ContentSearcher searcher;
            if (PLAIN_TEXT_FORMAT.contains(ext)) {
                searcher = new PlainTextSearcher(file, matcherClass, prefSet.isCaseSensitive());
            } else if (FORMAT_MAP.containsKey(ext)) {
                searcher = FORMAT_MAP.get(ext).getDeclaredConstructor(File.class, Class.class, boolean.class)
                        .newInstance(file, matcherClass, prefSet.isCaseSensitive());
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
//            if (prefSet.isMatchAll()) matchContentAll(content, file);
//            else matchContentAny(content, file);
        }
    }

//    private void matchContentAll(String string, File file) {
//        StringMatcher matcher = createMatcher(string);
//        for (String target : prefSet.getTargets()) {
//            if (!matcher.contains(target)) return;
//        }
//
//        addResult(file, false, true);
//        updateResultCount();
//    }
//
//    private void matchContentAny(String string, File file) {
//        StringMatcher matcher = createMatcher(string);
//        for (String target : prefSet.getTargets()) {
//            if (matcher.contains(target)) {
//                addResult(file, false, true);
//                updateResultCount();
//                return;
//            }
//        }
//    }

//    private String readPlainTextFromFile(File file) {
//        try {
//            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
//            StringBuilder builder = new StringBuilder();
//            String line;
//            while ((line = bufferedReader.readLine()) != null) {
//                builder.append(line).append('\n');
//            }
//            bufferedReader.close();
//            return builder.toString();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }

    private String getSearchingFileName(File file) {
        if (prefSet.isIncludePathName()) {
            if (prefSet.isCaseSensitive()) return file.getAbsolutePath();
            else return file.getAbsolutePath().toLowerCase();
        } else {
            if (prefSet.isCaseSensitive()) return file.getName();
            else return file.getName().toLowerCase();
        }
    }

    private Class<? extends StringMatcher> getMatcherClass() {
        if (prefSet.getMatchMode() == PrefSet.NORMAL) {
            switch (prefSet.getMatchingAlgorithm()) {
                case "algNative":
                    return NativeMatcher.class;
                case "algNaive":
                    return NaiveMatcher.class;
                case "algKmp":
                    return KMPMatcher.class;
                case "algSunday":
                    return SundayMatcher.class;
                default:
                    throw new RuntimeException("Not a valid matching algorithm");
            }
        } else if (prefSet.getMatchMode() == PrefSet.WORD) {
            switch (prefSet.getWordMatchingAlgorithm()) {
                case "algNaive":
                    return NaiveWordMatcher.class;
                case "algHash":
                    return HashMapWordSplitter.class;
                default:
                    throw new RuntimeException("Not a valid matching algorithm for words");
            }
        } else if (prefSet.getMatchMode() == PrefSet.REGEX) {
            if (prefSet.getRegexAlgorithm().equals("algNative")) {
                return NativeRegexMatcher.class;
            } else {
                throw new RuntimeException("Not a valid matching algorithm for regex");
            }
        } else {
            throw new RuntimeException("Invalid match mode");
        }
    }

    private StringMatcher createMatcher(String string) {
        if (prefSet.getMatchMode() == PrefSet.NORMAL) {
            switch (prefSet.getMatchingAlgorithm()) {
                case "algNative":
                    return new NativeMatcher(string);
                case "algNaive":
                    return new NaiveMatcher(string);
                case "algKmp":
                    return new KMPMatcher(string);
                case "algSunday":
                    return new SundayMatcher(string);
                default:
                    throw new RuntimeException("Not a valid matching algorithm");
            }
        } else if (prefSet.getMatchMode() == PrefSet.WORD) {
            switch (prefSet.getWordMatchingAlgorithm()) {
                case "algNaive":
                    return new NaiveWordMatcher(string);
                case "algHash":
                    return new HashMapWordSplitter(string);
                default:
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

    private void addContentResult(File file, ContentSearchingResult csr) {
        if (!tableList.isEmpty()) {
            ResultItem lastItem = tableList.get(tableList.size() - 1);
            if (lastItem.isSameFileAs(file)) {
                lastItem.setContentRes(csr);
                lastItem.addMatchContent();
                return;
            }
        }

        ResultItem resultItem = new ResultItem(file, false, true, bundle, fileTypeBundle);
        resultItem.setContentRes(csr);
        tableList.add(resultItem);
        updateResultCount();
    }

    private void addNameResult(File file) {
        // check if previous one is this one
        if (!tableList.isEmpty()) {
            ResultItem lastItem = tableList.get(tableList.size() - 1);
            if (lastItem.isSameFileAs(file)) {
                lastItem.addMatchContent();
                return;
            }
        }

        ResultItem resultItem = new ResultItem(file, true, false, bundle, fileTypeBundle);
        tableList.add(resultItem);
    }

    public boolean isNormalFinish() {
        return searching;
    }

    public ReadOnlyIntegerProperty resultCountProperty() {
        return resultCountWrapper;
    }
}
