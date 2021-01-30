package trashsoftware.deepSearcher2.searcher;

import trashsoftware.deepSearcher2.searcher.matchers.MatcherFactory;

import java.io.File;
import java.util.List;

/**
 * The abstract class for searcher that searches the content of a specific file format.
 */
public abstract class ContentSearcher {
    protected File file;
    protected MatcherFactory matcherFactory;
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
    public abstract ContentSearchingResult searchAll(List<String> targets);

    /**
     * @param targets patterns to be searched
     * @return search result if at least one target is found in this file, otherwise return {@code null}.
     */
    public abstract ContentSearchingResult searchAny(List<String> targets);
}

