package trashsoftware.deepSearcher2.searcher;

import trashsoftware.deepSearcher2.searcher.matchers.MatchMode;
import trashsoftware.deepSearcher2.util.Configs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A class that holds the current search preferences set by user.
 */
public class PrefSet {
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
    private String matchingAlg;
    private String wordMatchingAlg;
    private String regexMatchingAlg;
    private Set<String> excludedDirs;
    private Set<String> excludedFormats;
    private int maxSearchDepth;
    private boolean limitDepth;
    private int depthFirstIndicator = -1;  // -1 for not read, 0 for breadth first 1, for depth first

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

    public MatchMode getMatchMode() {
        if (matchWord) return MatchMode.WORD;
        else if (matchRegex) return MatchMode.REGEX;
        else return MatchMode.NORMAL;
    }

    public boolean isDepthFirst() {
        if (depthFirstIndicator == -1) {
            depthFirstIndicator = Configs.isDepthFirst() ? 1 : 0;
        }
        return depthFirstIndicator == 1;
    }

    public String getMatchingAlgorithm() {
        if (matchingAlg == null) {
            matchingAlg = Configs.getCurrentSearchingAlgorithm();
        }
        return matchingAlg;
    }

    public String getWordMatchingAlgorithm() {
        if (wordMatchingAlg == null) {
            wordMatchingAlg = Configs.getCurrentWordSearchingAlgorithm();
        }
        return wordMatchingAlg;
    }

    public String getRegexAlgorithm() {
        if (regexMatchingAlg == null) {
            regexMatchingAlg = Configs.getCurrentRegexSearchingAlgorithm();
        }
        return regexMatchingAlg;
    }

    Set<String> getExcludedDirs() {
        if (excludedDirs == null) excludedDirs = Configs.getAllExcludedDirs();
        return excludedDirs;
    }

    Set<String> getExcludedFormats() {
        if (excludedFormats == null) excludedFormats = Configs.getAllExcludedFormats();
        return excludedFormats;
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
         * This method eliminates duplicate directories, including sub-directories of existing directory.
         *
         * @param searchDirs all directories to search
         * @return this builder
         */
        public PrefSetBuilder setSearchDirs(List<File> searchDirs) {
            List<String> addedDirs = new ArrayList<>();
            List<File> addedFiles = new ArrayList<>();
            for (File f : searchDirs) {
                String absDir = f.getAbsolutePath();
                boolean foundParent = false;
                for (String added : addedDirs) {
                    if (absDir.startsWith(added)) {  // added is not the parent of absDir
                        foundParent = true;
                        break;
                    }
                }
                if (!foundParent) {
                    addedDirs.add(absDir);
                    addedFiles.add(f);
                }
            }
            prefSet.searchDirs = addedFiles;
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
            prefSet.showHidden = Configs.isShowHidden();
            prefSet.includePathName = Configs.isIncludePathName();
            prefSet.limitDepth = Configs.isLimitDepth();
            prefSet.maxSearchDepth = Configs.getMaxSearchDepth();
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
}
