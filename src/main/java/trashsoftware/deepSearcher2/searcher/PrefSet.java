package trashsoftware.deepSearcher2.searcher;

import trashsoftware.deepSearcher2.searcher.matchers.MatchMode;
import trashsoftware.deepSearcher2.util.Configs;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

/**
 * A class that holds the current search preferences set by user.
 */
public class PrefSet {
    private static final Map<String, String> ESCAPES = Map.of(
            "\\0", "\0",
            "\\b", "\b",
            "\\n", "\n",
            "\\r", "\r",
            "\\f", "\f",
            "\\t", "\t"
    );

    private boolean matchAll;
    private List<File> searchDirs;
    private List<String> targets;
    private boolean fileName;
    private boolean dirName;
    private boolean matchCase;
    private boolean includePathName;
    private boolean showHidden;
    private boolean matchRegex;
    private boolean matchWord;
    private Set<String> extensions;  // null if not searching content
    private Algorithm.Regular matchingAlg;
    private Algorithm.Word wordMatchingAlg;
    private Algorithm.Regex regexMatchingAlg;
    private Set<String> excludedDirs;
    private Set<String> excludedFormats;
    private int maxSearchDepth;
    private boolean limitDepth;
    private boolean wholeContent;
    private boolean escapes;
    private int depthFirstIndicator = -1;  // -1 for not read, 0 for breadth first 1, for depth first

    /**
     * Private constructor, avoiding constructing from outside.
     * <p>
     * Please use {@code PrefSet.PrefSetBuilder} to create instances of this class.
     */
    private PrefSet() {
    }

    public List<File> getSearchDirs() {
        return searchDirs;
    }

    public Set<String> getExtensions() {
        return extensions;
    }

    /**
     * @return a list of all targets, if not {@code isCaseSensitive()}, all targets are in lower case already.
     */
    public List<String> getTargets() {
        return targets;
    }

    /**
     * @return {@code true} if match all, {@code false} if match any
     */
    public boolean isMatchAll() {
        return matchAll;
    }

    public boolean isCaseSensitive() {
        return matchCase;
    }

    /**
     * @return is searching directory names
     */
    public boolean isDirName() {
        return dirName;
    }

    public boolean isFileName() {
        return fileName;
    }

    /**
     * Whether to search full path name instead of the last name of a file.
     * <p>
     * For example, "C:\Program Files\Java" matches "Files\Java" only when this method returns true.
     *
     * @return is including full path names
     */
    public boolean isIncludePathName() {
        return includePathName;
    }

    public boolean notShowHidden() {
        return !showHidden;
    }

    /**
     * @return the maximum traversal depth
     */
    public int getMaxSearchDepth() {
        return maxSearchDepth;
    }

    /**
     * @return whether to limit search depth
     */
    public boolean isLimitDepth() {
        return limitDepth;
    }

    /**
     * Whether to search every file's content as a whole string, not line-by-line.
     *
     * @return whether to search every file's content as a whole string, not line-by-line
     */
    public boolean isWholeContent() {
        return wholeContent;
    }

    /**
     * Whether to search escape characters, e.g. \n, \t, \b, \r
     *
     * @return {@code true} if search escape characters
     */
    public boolean isEscapes() {
        return escapes;
    }

    public MatchMode getMatchMode() {
        if (matchWord) return MatchMode.WORD;
        else if (matchRegex) return MatchMode.REGEX;
        else return MatchMode.NORMAL;
    }

    public boolean isDepthFirst() {
        if (depthFirstIndicator == -1) {
            depthFirstIndicator = Configs.getConfigs().isDepthFirst() ? 1 : 0;
        }
        return depthFirstIndicator == 1;
    }

    public Algorithm.Regular getMatchingAlgorithm() {
        if (matchingAlg == null) {
            matchingAlg = Configs.getConfigs().getCurrentSearchingAlgorithm();
        }
        return matchingAlg;
    }

    public Algorithm.Word getWordMatchingAlgorithm() {
        if (wordMatchingAlg == null) {
            wordMatchingAlg = Configs.getConfigs().getCurrentWordSearchingAlgorithm();
        }
        return wordMatchingAlg;
    }

    public Algorithm.Regex getRegexAlgorithm() {
        if (regexMatchingAlg == null) {
            regexMatchingAlg = Configs.getConfigs().getCurrentRegexSearchingAlgorithm();
        }
        return regexMatchingAlg;
    }

    Set<String> getExcludedDirs() {
        if (excludedDirs == null) excludedDirs = Configs.getConfigs().getAllExcludedDirs();
        return excludedDirs;
    }

    Set<String> getExcludedFormats() {
        if (excludedFormats == null) excludedFormats = Configs.getConfigs().getAllExcludedFormats();
        return excludedFormats;
    }

    private static List<String> replaceEscapes(List<String> targets) {
        List<String> result = new ArrayList<>();
        for (String s : targets) {
            for (Map.Entry<String, String> entry : ESCAPES.entrySet()) {
                s = s.replace(entry.getKey(), entry.getValue());
            }
            result.add(s);
        }
        return result;
    }

    public static class PrefSetBuilder {

        private final PrefSet prefSet = new PrefSet();

        public PrefSetBuilder setMatchAll(boolean matchAll) {
            prefSet.matchAll = matchAll;
            return this;
        }

        public PrefSetBuilder caseSensitive(boolean matchCase) {
            prefSet.matchCase = matchCase;
            return this;
        }

        public PrefSetBuilder searchFileName(boolean searchFileName) {
            prefSet.fileName = searchFileName;
            return this;
        }

        public PrefSetBuilder searchDirName(boolean searchDirName) {
            prefSet.dirName = searchDirName;
            return this;
        }

        public PrefSetBuilder matchWord(boolean matchWord) {
            prefSet.matchWord = matchWord;
            return this;
        }

        public PrefSetBuilder matchRegex(boolean matchRegex) {
            prefSet.matchRegex = matchRegex;
            return this;
        }

        public PrefSetBuilder directSetMatchMode(MatchMode matchMode) {
            if (matchMode == MatchMode.NORMAL) {
                prefSet.matchWord = false;
                prefSet.matchRegex = false;
            } else if (matchMode == MatchMode.WORD) {
                prefSet.matchWord = true;
                prefSet.matchRegex = false;
            } else if (matchMode == MatchMode.REGEX) {
                prefSet.matchWord = false;
                prefSet.matchRegex = true;
            } else {
                throw new RuntimeException("No such match mode");
            }
            return this;
        }

        /**
         * Sets up the directories to search.
         * <p>
         * Precondition: input list contains no duplicate files.
         * This method removes the children directories of any directories of other directories, if there
         * is no searching depth limit.
         * <p>
         * Reason for not removing children directories when there exists depth limit:
         * Consider two directories, "A" and "A/B". If the target file is "A/B/x.txt" and the depth limit is 1,
         * removing "A/B" would result in the absence of "A/B/x.txt".
         *
         * @param searchDirs all unique directories to search
         * @return this builder
         */
        public PrefSetBuilder setSearchDirs(List<File> searchDirs) {
            prefSet.limitDepth = Configs.getConfigs().isLimitDepth();
            prefSet.maxSearchDepth = Configs.getConfigs().getMaxSearchDepth();
            if (prefSet.limitDepth) {
                prefSet.searchDirs = new ArrayList<>(searchDirs);
                return this;
            }
            // uses a trie-like data structure to remove all children directories of added directories
            FileTree fileTree = new FileTree(searchDirs);
            prefSet.searchDirs = fileTree.getAllNoDup();
            return this;
        }

        public PrefSetBuilder setTargets(List<String> targets) {
            prefSet.targets = targets;
            return this;
        }

        public PrefSetBuilder setExtensions(Set<String> extensions) {
            prefSet.extensions = extensions;
            return this;
        }

        /**
         * Builds a new {@code PrefSet} with all options specified and some other options reads from config.
         *
         * @return a new {@code PrefSet}
         * @throws SearchTargetNotSetException if search target is not set
         * @throws SearchDirNotSetException    if no search directory is set
         * @throws SearchPrefNotSetException   if nothing to search
         */
        public PrefSet build()
                throws SearchTargetNotSetException, SearchDirNotSetException, SearchPrefNotSetException {
            if (prefSet.targets == null || prefSet.targets.isEmpty() || areTargetsAllEmpty()) {
                throw new SearchTargetNotSetException("No searching targets");
            }
            if (prefSet.searchDirs == null || prefSet.searchDirs.isEmpty()) {
                throw new SearchDirNotSetException("No searching directories");
            }
            if (noPrefSelected()) {
                throw new SearchPrefNotSetException("No searching things");
            }
            if (!prefSet.matchCase) {  // if not match case, convert to all lower case
                for (int i = 0; i < prefSet.targets.size(); i++) {
                    prefSet.targets.set(i, prefSet.targets.get(i).toLowerCase());
                }
            }
            prefSet.showHidden = Configs.getConfigs().isShowHidden();
            prefSet.includePathName = Configs.getConfigs().isIncludePathName();
            prefSet.wholeContent = Configs.getConfigs().isWholeContent();
            prefSet.escapes = Configs.getConfigs().isSearchEscapes();
            // depth limits are set in 'addSearchDirs'
            if (prefSet.escapes) {
                prefSet.targets = replaceEscapes(prefSet.targets);
            }
            return prefSet;
        }

        private boolean noPrefSelected() {
            return !prefSet.fileName &&
                    !prefSet.dirName &&
                    (prefSet.extensions == null || prefSet.extensions.isEmpty());
        }

        private boolean areTargetsAllEmpty() {
            for (String s : prefSet.targets) {
                if (s.length() > 0) return false;
            }
            return true;
        }
    }

    private static class FileTree {
        private final FileTreeNode root = new FileTreeNode();

        private FileTree(List<File> files) {
            for (File f : files) add(f);
        }

        private void add(File file) {
            String[] parts = file.getAbsolutePath().split(Pattern.quote(File.separator));
            FileTreeNode cur = root;
            for (String part : parts) {
                FileTreeNode node = cur.children.get(part);
                if (node == null) {
                    node = new FileTreeNode();
                    cur.children.put(part, node);
                }
                cur = node;
            }
            cur.isEnd = true;
        }

        /**
         * @return returns all outermost files that are real paths, i.e. marked as "isEnd=true"
         */
        private List<File> getAllNoDup() {
            List<File> res = new ArrayList<>();
            root.fillCompleteFile(res, "", "");
            return res;
        }

        @Override
        public String toString() {
            FileTreeNode.spaceCount = 0;
            return root.toString(null);
        }
    }

    /**
     * A trie-like data structure, recording an added directory.
     */
    private static class FileTreeNode {
        private static int spaceCount;  // only used for "toString"
        private final Map<String, FileTreeNode> children = new HashMap<>();
        private boolean isEnd = false;  // whether this node represents a real file

        private void fillCompleteFile(List<File> files, String nameOfThis, String added) {
            String thisPath = added + nameOfThis + File.separator;
            if (isEnd) files.add(new File(thisPath));
            else for (Map.Entry<String, FileTreeNode> entry : children.entrySet())
                entry.getValue().fillCompleteFile(files, entry.getKey(), thisPath);
        }

        private String toString(String nameOfThis) {
            StringBuilder sb = new StringBuilder()
                    .append(" ".repeat(spaceCount))
                    .append(nameOfThis)
                    .append(" (")
                    .append(isEnd)
                    .append(")")
                    .append('\n');
            spaceCount += 2;
            for (Map.Entry<String, FileTreeNode> entry : children.entrySet()) {
                sb.append(entry.getValue().toString(entry.getKey()));
            }
            spaceCount -= 2;
            return sb.toString();
        }
    }
}
