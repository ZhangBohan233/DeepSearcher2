package trashsoftware.deepSearcher2.searcher;

import trashsoftware.deepSearcher2.searcher.matchers.MatcherFactory;
import trashsoftware.deepSearcher2.searcher.matchers.StringMatcher;

import java.io.File;
import java.util.List;

/**
 * The abstract class for searcher that searches the content of a specific file format.
 */
public abstract class ContentSearcher {

    /**
     * The file to be searched.
     */
    protected File file;

    /**
     * matcher factory which produces a string searcher according to algorithm and match mode.
     */
    protected MatcherFactory matcherFactory;

    /**
     * Whether the search is case sensitive, i.e. whether UPPER CASE LETTERS differs from lower case letters.
     */
    protected boolean caseSensitive;

    /**
     * Constructor.
     *
     * @param file           the file to be searched
     * @param matcherFactory matcher factory which produces a string searcher according to algorithm and match mode
     * @param caseSensitive  is case sensitive
     */
    public ContentSearcher(File file, MatcherFactory matcherFactory, boolean caseSensitive) {
        this.file = file;
        this.matcherFactory = matcherFactory;
        this.caseSensitive = caseSensitive;
    }

    /**
     * @param targets patterns to be searched
     * @return search result if all targets are found in this file, otherwise return {@code null}.
     */
    public abstract ContentResult searchAll(List<String> targets);

    /**
     * @param targets patterns to be searched
     * @return search result if at least one target is found in this file, otherwise return {@code null}.
     */
    public abstract ContentResult searchAny(List<String> targets);

    /**
     * @return the concatenated file content, or {@code null} if the file is not readable
     */
    protected abstract String readWholeFile();

    /**
     * Search all, reads the whole file as a string.
     *
     * @param targets patterns to be searched
     * @return the content result with no keys
     */
    public ContentResult searchAllWhole(List<String> targets) {
        String content = readWholeFile();
        if (content == null) return null;
        if (!caseSensitive) content = content.toLowerCase();
        StringMatcher matcher = matcherFactory.createMatcher(content);
        for (String tar : targets) {
            if (matcher.search(tar) == -1) return null;
        }
        return new ContentResult();
    }

    /**
     * Search any, reads the whole file as a string.
     *
     * @param targets patterns to be searched
     * @return the content result with no keys
     */
    public ContentResult searchAnyWhole(List<String> targets) {
        String content = readWholeFile();
        if (content == null) return null;
        if (!caseSensitive) content = content.toLowerCase();
        StringMatcher matcher = matcherFactory.createMatcher(content);
        for (String tar : targets) {
            if (matcher.search(tar) >= 0) return new ContentResult();
        }
        return null;
    }
}

