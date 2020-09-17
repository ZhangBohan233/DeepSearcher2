package trashsoftware.deepSearcher2.searcher;

import trashsoftware.deepSearcher2.searcher.matchers.MatcherFactory;

import java.io.*;
import java.util.*;

public abstract class ContentSearcher {

    protected File file;
    protected MatcherFactory matcherFactory;
    protected boolean caseSensitive;

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

