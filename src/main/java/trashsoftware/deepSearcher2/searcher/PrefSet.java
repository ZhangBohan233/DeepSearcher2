package trashsoftware.deepSearcher2.searcher;

import trashsoftware.deepSearcher2.util.Configs;

import java.io.File;
import java.util.List;
import java.util.Set;

public class PrefSet {

    static final int NORMAL = 0;
    static final int WORD = 1;
    static final int REGEX = 2;

    private boolean matchAll;
    private List<File> searchDirs;
    private List<String> targets;
    private boolean fileName;
    private boolean dirName;
    private boolean matchCase;
    private boolean includeDirName;
    private boolean matchRegex;
    private boolean matchWord;
    private Set<String> extensions;  // null if not searching content

    public static class PrefSetBuilder {

        private PrefSet prefSet = new PrefSet();

        public PrefSetBuilder setMatchAll(boolean matchAll) {
            prefSet.matchAll = matchAll;
            return this;
        }

        public PrefSetBuilder matchCase(boolean matchCase) {
            prefSet.matchCase = matchCase;
            return this;
        }

        public PrefSetBuilder includeDirName(boolean includeDirName) {
            prefSet.includeDirName = includeDirName;
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

        public PrefSetBuilder setSearchDirs(List<File> searchDirs) {
            prefSet.searchDirs = searchDirs;
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

    List<File> getSearchDirs() {
        return searchDirs;
    }

    Set<String> getExtensions() {
        return extensions;
    }

    List<String> getTargets() {
        return targets;
    }

    boolean isMatchAll() {
        return matchAll;
    }

    boolean isMatchCase() {
        return matchCase;
    }

    boolean isDirName() {
        return dirName;
    }

    boolean isFileName() {
        return fileName;
    }

    boolean isIncludeDirName() {
        return includeDirName;
    }

    int getMatchMode() {
        if (matchWord) return WORD;
        else if (matchRegex) return REGEX;
        else return NORMAL;
    }

    String getMatchingAlgorithm() {
        return Configs.getCurrentSearchingAlgorithm();
    }

    String getWordMatchingAlgorithm() {
        return Configs.getCurrentWordSearchingAlgorithm();
    }

    String getRegexAlgorithm() {
        return Configs.getCurrentRegexSearchingAlgorithm();
    }
}
