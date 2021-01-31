package trashsoftware.deepSearcher2.searcher;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.collections.ObservableList;
import trashsoftware.deepSearcher2.guiItems.ResultItem;
import trashsoftware.deepSearcher2.searcher.contentSearchers.*;
import trashsoftware.deepSearcher2.searcher.matchers.MatcherFactory;
import trashsoftware.deepSearcher2.searcher.matchers.StringMatcher;
import trashsoftware.deepSearcher2.util.Configs;
import trashsoftware.deepSearcher2.util.EventLogger;
import trashsoftware.deepSearcher2.util.Util;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Searcher {
    private static final Map<String, Class<? extends ContentSearcher>> FORMAT_MAP = Map.of(
            "pdf", PdfSearcher.class,
            "doc", DocSearcher.class,
            "docx", DocxSearcher.class,
            "ppt", PptSearcher.class,
            "pptx", PptxSearcher.class,
            "xls", XlsSearcher.class,
            "xlsx", XlsxSearcher.class
    );
    private final Map<String, String> customFormats;
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
                    ResourceBundle fileTypeBundle, Map<String, String> customFormats) {
        this.prefSet = prefSet;
        this.tableList = tableList;
        this.bundle = bundle;
        this.fileTypeBundle = fileTypeBundle;
        this.nameMatcherFactory = MatcherFactory.createFactoryByPrefSet(prefSet);
        this.contentMatcherFactory = MatcherFactory.createFactoryByPrefSet(prefSet);
        this.customFormats = customFormats;

        contentService = Executors.newFixedThreadPool(Configs.getCurrentCpuThreads());
    }

    /**
     * Start searching
     */
    public void search() {
        boolean depthFirst = prefSet.isDepthFirst();
        if (depthFirst) {
            for (File f : prefSet.getSearchDirs()) {
                depthFirstSearch(f);
            }
        } else {
            for (File f : prefSet.getSearchDirs()) {
                breadthFirstSearch(f);
            }
        }
        contentService.shutdown();
        try {
            if (!contentService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS)) {
                EventLogger.log("292,471,208 years have passed! The sun has brightened another 3%!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Interrupt the search process.
     * <p>
     * This method does not shut down the search process immediately. Instead, it marks this searcher as stopped.
     * All subsequent file traversal and file reading tasks are cancelled.
     */
    public void stop() {
        searching = false;
        contentService.shutdownNow();
    }

    private void depthFirstSearch(File rootFile) {
        boolean notShowHidden = prefSet.notShowHidden();
        int maxDepth = prefSet.isLimitDepth() ? prefSet.getMaxSearchDepth() : Integer.MAX_VALUE;
        Deque<DepthFile> stack = new ArrayDeque<>();
        stack.addLast(new DepthFile(rootFile, 0));

        while (!stack.isEmpty()) {
            if (!searching) return;

            DepthFile file = stack.removeLast();
            if (notShowHidden && file.file.isHidden()) continue;
            if (file.file.isDirectory()) {
                // Check if this directory is excluded
                if (prefSet.getExcludedDirs().contains(file.file.getAbsolutePath())) continue;

                // Check dir is selected
                if (prefSet.isDirName()) {
                    matchName(file.file);
                }

                if (file.depth >= maxDepth) continue;
                File[] subFiles = file.file.listFiles();
                if (subFiles == null) continue;
                for (int i = subFiles.length - 1; i >= 0; i--) {
                    stack.addLast(new DepthFile(subFiles[i], file.depth + 1));
                }
            } else {
                searchOneFile(file.file);
            }
        }
    }

    private void breadthFirstSearch(File rootFile) {
        boolean notShowHidden = prefSet.notShowHidden();
        int maxDepth = prefSet.isLimitDepth() ? prefSet.getMaxSearchDepth() : Integer.MAX_VALUE;
        Deque<DepthFile> stack = new ArrayDeque<>();
        stack.addLast(new DepthFile(rootFile, 0));

        while (!stack.isEmpty()) {
            if (!searching) return;

            DepthFile file = stack.removeFirst();
            if (notShowHidden && file.file.isHidden()) continue;
            if (file.file.isDirectory()) {
                // Check if this directory is excluded
                if (prefSet.getExcludedDirs().contains(file.file.getAbsolutePath())) continue;

                // Check dir is selected
                if (prefSet.isDirName()) {
                    matchName(file.file);
                }

                if (file.depth >= maxDepth) continue;
                File[] subFiles = file.file.listFiles();
                if (subFiles == null) continue;
                for (File f : subFiles) {
                    stack.addLast(new DepthFile(f, file.depth + 1));
                }
            } else {
                searchOneFile(file.file);
            }
        }
    }

    private void searchOneFile(File file) {
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

    private synchronized void addContentResult(File file, ContentResult csr) {
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
        // check duplicate
        // duplicate may happens when a depth limit is set, so prefSet does not remove parent-children directories.
        if (!resultFilesMap.containsKey(file)) {
            ResultItem resultItem = ResultItem.createNameMatch(file, bundle, fileTypeBundle, customFormats);
            tableList.add(resultItem);
            resultFilesMap.put(file, resultItem);
            updateResultCount();
        }
    }

    public boolean isNormalFinish() {
        return searching;
    }

    public ReadOnlyIntegerProperty resultCountProperty() {
        return resultCountWrapper;
    }

    private static class DepthFile {
        private final int depth;
        private final File file;

        DepthFile(File file, int depth) {
            this.file = file;
            this.depth = depth;
        }
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
            ContentResult result;
            if (prefSet.isMatchAll()) {
                result = searcher.searchAll(prefSet.getTargets());
            } else {
                result = searcher.searchAny(prefSet.getTargets());
            }
            if (result != null) addContentResult(file, result);
        }
    }
}
