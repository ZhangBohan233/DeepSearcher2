package trashsoftware.deepSearcher2.searcher.contentSearchers;

import dsApi.api.FileFormatReader;
import trashsoftware.deepSearcher2.searcher.ContentResult;
import trashsoftware.deepSearcher2.searcher.ContentSearcher;
import trashsoftware.deepSearcher2.searcher.matchers.MatcherFactory;
import trashsoftware.deepSearcher2.searcher.matchers.StringMatcher;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * A content searcher that uses a external {@link FileFormatReader} to search file's content.
 */
public class FormatReaderSearcher extends ContentSearcher {

    /**
     * The format reader instance.
     */
    private final FileFormatReader formatReader;

    private final Locale locale;

    private final Set<String> foundTargets = new HashSet<>();
    private final List<Integer> splitFounds = new ArrayList<>();
    private final List<Integer> charFounds = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param file           the file to be searched
     * @param formatReader   the format reader instance
     * @param matcherFactory matcher factory which produces a string searcher according to algorithm and match mode
     * @param caseSensitive  is case sensitive
     */
    public FormatReaderSearcher(File file,
                                FileFormatReader formatReader,
                                MatcherFactory matcherFactory,
                                boolean caseSensitive,
                                Locale locale) {
        super(file, matcherFactory, caseSensitive);

        this.formatReader = formatReader;
        this.locale = locale;
    }

    @Override
    public ContentResult searchAll(List<String> targets) {
        try {
            if (formatReader.hasSplitter()) {
                return searchOneKeyAll(targets);
            } else {
                return searchCharsAll(targets);
            }
        } catch (IOException e) {
            System.err.println(e.toString());
            return null;
        }
    }

    @Override
    public ContentResult searchAny(List<String> targets) {
        try {
            if (formatReader.hasSplitter()) {
                return searchOneKeyAny(targets);
            } else {
                return searchCharsAny(targets);
            }
        } catch (IOException e) {
            System.err.println(e.toString());
            return null;
        }
    }

    private void searchInSplit(List<String> targets) throws IOException {
        foundTargets.clear();
        splitFounds.clear();
        charFounds.clear();
        String[] split = formatReader.readBySplitter(file);
        for (int i = 0; i < split.length; ++i) {
            String part = split[i];
            StringMatcher matcher = matcherFactory.createMatcher(part);
            for (String target : targets) {
                int pos = matcher.search(target);
                if (pos >= 0) {
                    foundTargets.add(target);
                    splitFounds.add(i + 1);
                    charFounds.add(pos + 1);
                }
            }
        }
    }

    private ContentResult searchOneKeyAll(List<String> targets) throws IOException {
        searchInSplit(targets);
        if (foundTargets.size() == targets.size()) {
            return new ContentResult.External(
                    formatReader.splitterFormat(locale), splitFounds,
                    formatReader.characterFormat(locale), charFounds);
        }
        return null;
    }

    private ContentResult searchOneKeyAny(List<String> targets) throws IOException {
        searchInSplit(targets);
        if (foundTargets.size() > 0) {
            return new ContentResult.External(
                    formatReader.splitterFormat(locale), splitFounds,
                    formatReader.characterFormat(locale), charFounds);
        }
        return null;
    }

    private void searchInText(List<String> targets) throws IOException {
        foundTargets.clear();
        charFounds.clear();
        String text = formatReader.readFile(file);
        StringMatcher matcher = matcherFactory.createMatcher(text);
        for (String target : targets) {
            int pos = matcher.search(target);
            if (pos >= 0) {
                foundTargets.add(target);
                charFounds.add(pos + 1);
            }
        }
    }

    private ContentResult searchCharsAll(List<String> targets) throws IOException {
        searchInText(targets);
        if (foundTargets.size() == targets.size()) {
            return new ContentResult.External(
                    formatReader.characterFormat(locale),
                    charFounds
            );
        }
        return null;
    }

    private ContentResult searchCharsAny(List<String> targets) throws IOException {
        searchInText(targets);
        if (foundTargets.size() > 0) {
            return new ContentResult.External(
                    formatReader.characterFormat(locale),
                    charFounds
            );
        }
        return null;
    }

    @Override
    protected String readWholeFile() {
        try {
            return formatReader.readFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
