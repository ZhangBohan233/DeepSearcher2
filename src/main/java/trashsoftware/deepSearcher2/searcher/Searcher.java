package trashsoftware.deepSearcher2.searcher;

import dsApi.api.FileFormatReader;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.scene.control.TableView;
import trashsoftware.deepSearcher2.guiItems.ResultItem;
import trashsoftware.deepSearcher2.searcher.archiveSearchers.*;
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

/**
 * The core of searching.
 * <p>
 * This class mainly does the file system traversal and allocate proper content searcher.
 */
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
    private final SearchingOptions options;
    private final ExecutorService contentService;

    private final ReadOnlyIntegerWrapper resultCountWrapper = new ReadOnlyIntegerWrapper();

    private final TableView<ResultItem> table;

    private final Map<File, ResultItem> resultFilesMap = new HashMap<>();

    private final MatcherFactory nameMatcherFactory;
    private final MatcherFactory contentMatcherFactory;
    private boolean searching = true;
    private boolean finished = false;

    /**
     * Constructor.
     *
     * @param options       the pref set, recording all search preferences and is immutable.
     * @param table         the javafx table of the result {@code TableView}
     * @param customFormats all custom formats, immutable after
     */
    public Searcher(SearchingOptions options,
                    TableView<ResultItem> table,
                    Map<String, String> customFormats) {
        this.options = options;
        this.table = table;
        this.nameMatcherFactory = MatcherFactory.createFactoryByPrefSet(options);
        this.contentMatcherFactory = MatcherFactory.createFactoryByPrefSet(options);
        // This is wrapped by a new map to avoid situations that the user modifies custom formats while searching
        this.customFormats = new HashMap<>(customFormats);

        contentService = Executors.newFixedThreadPool(Configs.getConfigs().getCurrentCpuThreads());
    }

    /**
     * Start searching
     */
    public void search() {
        boolean depthFirst = options.isDepthFirst();
        if (depthFirst) {
            for (File f : options.getSearchDirs()) {
                depthFirstSearch(f);
            }
        } else {
            for (File f : options.getSearchDirs()) {
                breadthFirstSearch(f);
            }
        }
        contentService.shutdown();
        try {
            if (!contentService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS)) {
                // error
                // Note: a G-class main sequence star keeps brightening in its life
                // The Sun has brightened about 30% comparing to the time when it was born (5 billion years ago)
                EventLogger.log("292,471,208 years have passed! Even the Sun has brightened another 3%!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finished = true;
    }

    public SearchingOptions getOptions() {
        return options;
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
        boolean notShowHidden = options.notShowHidden();
        int maxDepth = options.isLimitDepth() ? options.getMaxSearchDepth() : Integer.MAX_VALUE;
        Deque<DepthFile> stack = new ArrayDeque<>();
        stack.addLast(new DepthFile(rootFile, 0));

        while (!stack.isEmpty()) {
            if (!searching) return;

            DepthFile file = stack.removeLast();
            if (notShowHidden && file.file.isHidden()) continue;
            if (file.file.isDirectory()) {
                // Check if this directory is excluded
                if (options.getExcludedDirs().contains(file.file.getAbsolutePath())) continue;

                // Check dir is selected
                if (options.isDirName()) {
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
        boolean notShowHidden = options.notShowHidden();
        int maxDepth = options.isLimitDepth() ? options.getMaxSearchDepth() : Integer.MAX_VALUE;
        Deque<DepthFile> stack = new ArrayDeque<>();
        stack.addLast(new DepthFile(rootFile, 0));

        while (!stack.isEmpty()) {
            if (!searching) return;

            DepthFile file = stack.removeFirst();
            if (notShowHidden && file.file.isHidden()) continue;
            if (file.file.isDirectory()) {
                // Check if this directory is excluded
                if (options.getExcludedDirs().contains(file.file.getAbsolutePath())) continue;

                // Check dir is selected
                if (options.isDirName()) {
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

    public void searchOneFile(File file) {
        // Check if this format is excluded
        if (options.getExcludedFormats().contains(Util.getFileExtension(file.getName()))) return;

        // check file name is selected
        if (options.isFileName()) {
            matchName(file);
        }
        // check file content is selected
        if (options.getExtensions() != null) {
            matchFileContent(file);
        }
        // check search compressed files is selected
        if (options.isSearchCmpFile()) {
            searchArchiveFile(file);
        }
    }

    private void searchArchiveFile(File file) {
        searchArchiveFile(file, file, "", true);
    }

    /**
     * Searches an archive file
     *
     * @param realFile         the real file on disk, ready to read, may be a temp file
     * @param outermostArchive the outermost archive file, permanently existing on disk
     * @param internalPath     path between the outermost archive and the current processing file
     */
    public void searchArchiveFile(File realFile, File outermostArchive, String internalPath) {
        searchArchiveFile(realFile, outermostArchive, internalPath, false);
    }

    private void searchArchiveFile(File realFile, File outermostArchive, String internalPath, boolean needThread) {
        if (outermostArchive == null) outermostArchive = realFile;
        ArchiveSearcher archiveSearcher = makeArchiveSearcher(realFile, outermostArchive, internalPath);
        if (archiveSearcher != null) {
            if (needThread) contentService.execute(new SearchArchiveTask(archiveSearcher));
            else archiveSearcher.search();
        }
    }

    private void matchName(File file) {
        if (options.isMatchAll()) matchNameAll(file, null);
        else matchNameAny(file, null);
    }

    public void matchName(FileInArchive fileInArchive) {
        if (options.isMatchAll()) matchNameAll(fileInArchive.getFakeFile(), fileInArchive);
        else matchNameAny(fileInArchive.getFakeFile(), fileInArchive);
    }

    private void matchNameAll(File file, FileInArchive fileInArchive) {
        String name = getSearchingFileName(file);

        StringMatcher matcher = nameMatcherFactory.createMatcher(name);
        for (String target : options.getTargets()) {
            if (matcher.search(target) < 0) return;
        }

        addNameResult(file, fileInArchive);
    }

    private void matchNameAny(File file, FileInArchive fileInArchive) {
        String name = getSearchingFileName(file);

        StringMatcher matcher = nameMatcherFactory.createMatcher(name);
        for (String target : options.getTargets()) {
            if (matcher.search(target) >= 0) {
                addNameResult(file, fileInArchive);
                return;
            }
        }
    }

    private ContentSearcher createContentSearcher(File file) {
        String ext = Util.getFileExtension(file.getName());
        if (options.getExtensions().contains(ext)) {
            Class<? extends ContentSearcher> searcherClass = FORMAT_MAP.get(ext);
            if (searcherClass != null) {
                try {
                    return searcherClass
                            .getDeclaredConstructor(File.class, MatcherFactory.class, boolean.class)
                            .newInstance(file, contentMatcherFactory, options.isCaseSensitive());
                } catch (InvocationTargetException |
                        NoSuchMethodException |
                        InstantiationException |
                        IllegalAccessException e) {
                    throw new InvalidClassException("Unexpected file content searcher. ", e);
                }
            }
            FileFormatReader formatReader = options.getExternalReaders().get(ext);
            if (formatReader != null) {
                return new FormatReaderSearcher(file, formatReader, contentMatcherFactory, options.isCaseSensitive(),
                        options.getLocale());
            }
            return new PlainTextSearcher(file, contentMatcherFactory, options.isCaseSensitive());
        }
        return null;
    }

    private void matchFileContent(File file) {
        ContentSearcher searcher = createContentSearcher(file);
        if (searcher != null) {
            contentService.execute(new SearchContentTask(file, searcher));
        }
    }

    public void matchFileContent(File uncompressed, FileInArchive fileInArchive) {
        ContentSearcher searcher = createContentSearcher(uncompressed);
        if (searcher != null) {
            ContentResult result;
            if (options.isWholeContent()) {
                if (options.isMatchAll()) result = searcher.searchAllWhole(options.getTargets());
                else result = searcher.searchAnyWhole(options.getTargets());
            } else {
                if (options.isMatchAll()) result = searcher.searchAll(options.getTargets());
                else result = searcher.searchAny(options.getTargets());
            }
            if (result != null) addContentResult(fileInArchive.getFakeFile(), fileInArchive, result);
        }
    }

    private String getSearchingFileName(File file) {
        if (options.isIncludePathName()) {
            if (options.isCaseSensitive()) return file.getAbsolutePath();
            else return file.getAbsolutePath().toLowerCase();
        } else {
            if (options.isCaseSensitive()) return file.getName();
            else return file.getName().toLowerCase();
        }
    }

    private ArchiveSearcher makeArchiveSearcher(File realArchive, File outermostArchive, String internalPath) {
        String ext = Util.getFileExtension(realArchive.getName()).toLowerCase();
        if (options.getCmpFileFormats().contains(ext)) {
            switch (ext) {
                case "zip":
                    return new ZipSearcher(realArchive, outermostArchive, internalPath, this);
                case "7z":
                    return new SevenZSearcher(realArchive, outermostArchive, internalPath, this);
                case "rar":
                    return new RarSearcher(realArchive, outermostArchive, internalPath, this);
                case "gz":
                    return new GzSearcher(realArchive, outermostArchive, internalPath, this);
                case "tar":
                    return new TarSearcher(realArchive, outermostArchive, internalPath, this);
                case "xz":
                    return new XzSearcher(realArchive, outermostArchive, internalPath, this);
                case "bz2":
                    return new Bz2Searcher(realArchive, outermostArchive, internalPath, this);
            }
        }
        return null;
    }

    private void updateResultCount() {
        resultCountWrapper.setValue(table.getItems().size());
    }

    private synchronized void addContentResult(File file, FileInArchive fileInArchive, ContentResult csr) {
        // check if previous some result is already added
        // This situation occurs when this file is already matched by name successfully
        ResultItem item = resultFilesMap.get(file);
        if (item != null) {
            item.setContentRes(csr);
            table.refresh();
        } else {
            ResultItem resultItem;
            if (fileInArchive == null)
                resultItem = ResultItem.createContentMatch(file, csr, customFormats);
            else
                resultItem = ResultItem.createContentMatchInArchive(fileInArchive, csr, customFormats);
            table.getItems().add(resultItem);
            resultFilesMap.put(file, resultItem);
            updateResultCount();
        }
    }

    private void addNameResult(File file, FileInArchive fileInArchive) {
        // check duplicate
        // duplicate may happens when a depth limit is set, so prefSet does not remove parent-children directories.
        if (!resultFilesMap.containsKey(file)) {
            ResultItem resultItem;
            if (fileInArchive == null)
                resultItem = ResultItem.createNameMatch(file, customFormats);
            else
                resultItem = ResultItem.RegularItem.createNameMatchInArchive(fileInArchive, customFormats);
            table.getItems().add(resultItem);
            resultFilesMap.put(file, resultItem);
            updateResultCount();
        }
    }

    public boolean isNormalFinish() {
        return finished && searching;
    }

    public boolean isSearching() {
        return !finished && searching;
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

        @Override
        public String toString() {
            return "DepthFile{" + file.getAbsolutePath() + " at depth " + depth + '}';
        }
    }

    private static class SearchArchiveTask implements Runnable {

        private final ArchiveSearcher archiveSearcher;

        private SearchArchiveTask(ArchiveSearcher archiveSearcher) {
            this.archiveSearcher = archiveSearcher;
        }

        @Override
        public void run() {
            archiveSearcher.search();
        }
    }

    /**
     * A class that runs file content searching in background.
     */
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
            if (options.isWholeContent()) {
                if (options.isMatchAll()) result = searcher.searchAllWhole(options.getTargets());
                else result = searcher.searchAnyWhole(options.getTargets());
            } else {
                if (options.isMatchAll()) result = searcher.searchAll(options.getTargets());
                else result = searcher.searchAny(options.getTargets());
            }
            if (result != null) addContentResult(file, null, result);
        }
    }
}
