package trashsoftware.deepSearcher2.searcher.contentSearchers;

import trashsoftware.deepSearcher2.searcher.ContentSearchingResult;
import trashsoftware.deepSearcher2.searcher.matchers.MatcherFactory;
import trashsoftware.deepSearcher2.searcher.matchers.StringMatcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class PlainTextSearcher extends TwoKeysSearcher {

    /**
     * Reason for collecting total text:
     * The regular implementation searches line-by-line, which omits cross-line matches.
     * So if the regular search does not match, search in total text again.
     */
//    private StringBuilder wholeFileBuilder = new StringBuilder();

    public PlainTextSearcher(File file, MatcherFactory matcherFactory, boolean caseSensitive) {
        super(file, matcherFactory, caseSensitive, ContentSearchingResult.LINES_KEY, ContentSearchingResult.CHARS_KEY);
    }

    @Override
    protected void searchFile(List<String> targets) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            int lineCount = 1;
            while ((line = bufferedReader.readLine()) != null) {
                searchInString(line, targets, lineCount);
                lineCount++;
            }
            bufferedReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private boolean totalContains()
//            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException{
//        String totalText = wholeFileBuilder.toString();
//    }
}
