package trashsoftware.deepSearcher2.searcher;

import trashsoftware.deepSearcher2.searcher.matchers.MatchMode;
import trashsoftware.deepSearcher2.util.Configs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PrefSet {
    private boolean matchAll;
    private List<File> searchDirs;
    private List<String> targets;
    private boolean fileName;
    private boolean dirName;
    private boolean matchCase;
    private boolean includePathName;
    private boolean matchRegex;
    private boolean matchWord;
    private Set<String> extensions;  // null if not searching content
    private String matchingAlg;
    private String wordMatchingAlg;
    private String regexMatchingAlg;
    private Set<String> excludedDirs;
    private Set<String> excludedFormats;

    public List<File> getSearchDirs() {
        return searchDirs;
    }

    public Set<String> getExtensions() {
        return extensions;
    }

    /**
     * @return a list of all targets, if {@code isCaseSensitive()}, all targets are in lower case already.
     */
    public List<String> getTargets() {
        return targets;
    }

    public boolean isMatchAll() {
        return matchAll;
    }

    public boolean isCaseSensitive() {
        return matchCase;
    }

    public boolean isDirName() {
        return dirName;
    }

    public boolean isFileName() {
        return fileName;
    }

    public boolean isIncludePathName() {
        return includePathName;
    }

    public MatchMode getMatchMode() {
        if (matchWord) return MatchMode.WORD;
        else if (matchRegex) return MatchMode.REGEX;
        else return MatchMode.NORMAL;
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

        public PrefSetBuilder includePathName(boolean includePathName) {
            prefSet.includePathName = includePathName;
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
         * Sets up the directories to search
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
//            System.out.println(addedFiles);
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

        public PrefSet build() throws SearchTargetNotSetException, SearchDirNotSetException {
            if (prefSet.targets == null || prefSet.targets.isEmpty() || areTargetsAllEmpty()) {
                throw new SearchTargetNotSetException("No searching targets");
            }
            if (prefSet.searchDirs == null || prefSet.searchDirs.isEmpty()) {
                throw new SearchDirNotSetException("No searching directories");
            }
            if (!prefSet.matchCase) {  // if not match case, convert to all lower case
                for (int i = 0; i < prefSet.targets.size(); i++) {
                    prefSet.targets.set(i, prefSet.targets.get(i).toLowerCase());
                }
            }
            return prefSet;
        }

        private boolean areTargetsAllEmpty() {
            for (String s : prefSet.targets) {
                if (s.length() > 0) return false;
            }
            return true;
        }
    }
}
