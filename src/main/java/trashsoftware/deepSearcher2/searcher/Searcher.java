package trashsoftware.deepSearcher2.searcher;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.collections.ObservableList;
import trashsoftware.deepSearcher2.guiItems.ResultItem;
import trashsoftware.deepSearcher2.searcher.contentSearchers.*;
import trashsoftware.deepSearcher2.searcher.matchers.MatcherFactory;
import trashsoftware.deepSearcher2.searcher.matchers.StringMatcher;
import trashsoftware.deepSearcher2.util.Configs;
import trashsoftware.deepSearcher2.util.Util;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Searcher {

    private static final Set<String> PLAIN_TEXT_FORMAT = Set.of(
            "bat", "c", "cmd", "cpp", "csv", "h", "java", "js", "json", "log", "py", "r", "rmd", "tex", "txt"
    );
    private final Map<String, String> customFormats;

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
    private final ExecutorService contentService;

    private final ReadOnlyIntegerWrapper resultCountWrapper = new ReadOnlyIntegerWrapper();

    private final ObservableList<ResultItem> tableList;

    private final Map<File, ResultItem> resultFilesMap = new HashMap<>();

    private final ResourceBundle bundle;
    private final ResourceBundle fileTypeBundle;
    private final MatcherFactory nameMatcherFactory;
    private final MatcherFactory contentMatcherFactory;
    private boolean searching = true;

    public Searcher(PrefSet prefSet, ObservableList<ResultItem> tableList, ResourceBundle bundle,
                    ResourceBundle fileTypeBundle, Map<String,String> customFormats) {
        this.prefSet = prefSet;
        this.tableList = tableList;
        this.bundle = bundle;
        this.fileTypeBundle = fileTypeBundle;
        this.nameMatcherFactory = MatcherFactory.createFactoryByPrefSet(prefSet);
        this.contentMatcherFactory = MatcherFactory.createFactoryByPrefSet(prefSet);
        this.customFormats = customFormats;

        contentService = Executors.newFixedThreadPool(Configs.getCurrentCpuThreads());
    }

    public void search() {
        for (File f : prefSet.getSearchDirs()) {
            searchFileIterative(f);
        }
        contentService.shutdown();
        try {
            if (!contentService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS)) {
                System.out.println("Cannot stop!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        searching = false;
        contentService.shutdownNow();
    }

    private void searchFileIterative(File rootFile) {
        boolean depthFirst = prefSet.isDepthFirst();
        boolean notShowHidden = !prefSet.isShowHidden();
        Deque<File> stack = new ArrayDeque<>();
        stack.addLast(rootFile);
        while (!stack.isEmpty()) {
            if (!searching) return;

            File file = depthFirst ? stack.removeLast() : stack.removeFirst();
            if (notShowHidden && file.isHidden()) continue;
            if (file.isDirectory()) {
                // Check if this directory is excluded
                if (prefSet.getExcludedDirs().contains(file.getAbsolutePath())) continue;

                // Check dir is selected
                if (prefSet.isDirName()) {
                    matchName(file);
                }

                File[] subFiles = file.listFiles();
                if (subFiles == null) continue;
                for (File f : subFiles) {
                    stack.addLast(f);
                }
            } else {
                // Check if this format is excluded
                if (prefSet.getExcludedFormats().contains(Util.getFileExtension(file.getName()))) continue;

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
        if (prefSet.getExtensions().contains(ext)) {
            ContentSearcher searcher;
            if (FORMAT_MAP.containsKey(ext)) {
                try {
                    searcher = FORMAT_MAP.get(ext)
                            .getDeclaredConstructor(File.class, MatcherFactory.class, boolean.class)
                            .newInstance(file, contentMatcherFactory, prefSet.isCaseSensitive());
                } catch (InvocationTargetException |
                        NoSuchMethodException |
                        InstantiationException |
                        IllegalAccessException e) {
                    throw new InvalidClassException("Unexpected file content searcher. ", e);
                }
            } else {
                searcher = new PlainTextSearcher(file, contentMatcherFactory, prefSet.isCaseSensitive());
            }
            contentService.execute(new SearchContentTask(file, searcher));
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

    private void updateResultCount() {
        resultCountWrapper.setValue(tableList.size());
    }

    private synchronized void addContentResult(File file, ContentSearchingResult csr) {
        // check if previous some result is already added
        // This situation occurs when this file is already matched by name successfully
        ResultItem item = resultFilesMap.get(file);
        if (item != null) {
            item.setContentRes(csr);
        } else {
            ResultItem resultItem = ResultItem.createContentMatch(file, bundle, fileTypeBundle, csr, customFormats);
            tableList.add(resultItem);
            resultFilesMap.put(file, resultItem);
            updateResultCount();
        }
    }

    private void addNameResult(File file) {
        ResultItem resultItem = ResultItem.createNameMatch(file, bundle, fileTypeBundle, customFormats);
        tableList.add(resultItem);
        resultFilesMap.put(file, resultItem);
        updateResultCount();
    }

    public boolean isNormalFinish() {
        return searching;
    }

    public ReadOnlyIntegerProperty resultCountProperty() {
        return resultCountWrapper;
    }

    private class SearchContentTask implements Runnable {
        private final File file;
        private final ContentSearcher searcher;

        private SearchContentTask(File file, ContentSearcher searcher) {
            this.file = file;
            this.searcher = searcher;
        }

        @Override
        public void run() {
            ContentSearchingResult result;
            if (prefSet.isMatchAll()) {
                result = searcher.searchAll(prefSet.getTargets());
            } else {
                result = searcher.searchAny(prefSet.getTargets());
            }
            if (result != null) addContentResult(file, result);
        }
    }
}
