package trashsoftware.deepSearcher2.searcher;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.hwpf.extractor.WordExtractor;

import java.io.*;
import java.util.*;

public abstract class ContentSearcher {

    protected File file;
    protected Class<? extends StringMatcher> matcherClass;
    protected boolean caseSensitive;

    ContentSearcher(File file, Class<? extends StringMatcher> matcherClass, boolean caseSensitive) {
        this.file = file;
        this.matcherClass = matcherClass;
        this.caseSensitive = caseSensitive;
    }

    /**
     * @param targets patterns to be searched
     * @return search result if all targets are found in this file, otherwise return {@code null}.
     */
    abstract ContentSearchingResult searchAll(List<String> targets);

    /**
     * @param targets patterns to be searched
     * @return search result if at least one target is found in this file, otherwise return {@code null}.
     */
    abstract ContentSearchingResult searchAny(List<String> targets);
}

abstract class TwoKeysSearcher extends ContentSearcher {

    private final String key1;
    private final String key2;

    TwoKeysSearcher(File file, Class<? extends StringMatcher> matcherClass, boolean caseSensitive,
                    String key1, String key2) {
        super(file, matcherClass, caseSensitive);

        this.key1 = key1;
        this.key2 = key2;
    }

    @Override
    public ContentSearchingResult searchAll(List<String> targets) {

        Set<String> foundTargets = new HashSet<>();
        List<Integer> foundKey1s = new ArrayList<>();
        List<Integer> foundKey2s = new ArrayList<>();
        searchFile(targets, foundTargets, foundKey1s, foundKey2s);

        if (foundTargets.size() == targets.size()) {  // all matched
            return new ContentSearchingResult(key1, foundKey1s,
                    key2, foundKey2s);
        }

        return null;
    }

    @Override
    public ContentSearchingResult searchAny(List<String> targets) {
        Set<String> foundTargets = new HashSet<>();
        List<Integer> foundKey1s = new ArrayList<>();
        List<Integer> foundKey2s = new ArrayList<>();
        searchFile(targets, foundTargets, foundKey1s, foundKey2s);

        if (foundTargets.size() > 0) {  // at least one matched
            return new ContentSearchingResult(key1, foundKey1s,
                    key2, foundKey2s);
        }

        return null;
    }

    protected void searchInString(String string, List<String> targets, Set<String> foundTargets,
                                  List<Integer> found1, List<Integer> found2, int thisInValue1) {
        if (!caseSensitive) string = string.toLowerCase();
        StringMatcher matcher = StringMatcher.createMatcher(matcherClass, string);
        for (String tar : targets) {
            int pos = matcher.search(tar);
            if (pos >= 0) {
                foundTargets.add(tar);
                found1.add(thisInValue1);
                found2.add(pos);
            }
        }
    }

    protected abstract void searchFile(List<String> targets, Set<String> foundTargets, List<Integer> foundKey1s,
                                       List<Integer> foundKey2s);
}

class PlainTextSearcher extends TwoKeysSearcher {

    /**
     * Reason for collecting total text:
     * The regular implementation searches line-by-line, which omits cross-line matches.
     * So if the regular search does not match, search in total text again.
     */
//    private StringBuilder wholeFileBuilder = new StringBuilder();

    PlainTextSearcher(File file, Class<? extends StringMatcher> matcherClass, boolean caseSensitive) {
        super(file, matcherClass, caseSensitive, ContentSearchingResult.LINES_KEY, ContentSearchingResult.CHARS_KEY);
    }

    @Override
    protected void searchFile(List<String> targets, Set<String> foundTargets, List<Integer> foundLines,
                              List<Integer> foundChars) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            int lineCount = 1;
            while ((line = bufferedReader.readLine()) != null) {
                searchInString(line, targets, foundTargets, foundLines, foundChars, lineCount);
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

class PdfReader extends TwoKeysSearcher {

    PdfReader(File file, Class<? extends StringMatcher> matcherClass, boolean caseSensitive) {
        super(file, matcherClass, caseSensitive, ContentSearchingResult.PAGES_KEY, ContentSearchingResult.CHARS_KEY);
    }

    @Override
    protected void searchFile(List<String> targets, Set<String> foundTargets,
                              List<Integer> foundPages, List<Integer> foundChars) {
        try {
            PDDocument document = PDDocument.load(file);
            if (!document.isEncrypted()) {

                PDFTextStripper stripper = new PDFTextStripper();

                int endPage = document.getNumberOfPages();

                for (int i = 1; i < endPage; i++) {
                    stripper.setStartPage(i);
                    stripper.setEndPage(i + 1);
                    String page = stripper.getText(document);

                    searchInString(page, targets, foundTargets, foundPages, foundChars, i);
                }

                document.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class DocReader extends TwoKeysSearcher {

    DocReader(File file, Class<? extends StringMatcher> matcherClass, boolean caseSensitive) {
        super(file, matcherClass, caseSensitive, ContentSearchingResult.PARAGRAPHS_KEY, ContentSearchingResult.CHARS_KEY);
    }

    @Override
    protected void searchFile(List<String> targets, Set<String> foundTargets, List<Integer> foundLines,
                              List<Integer> foundChars) {
        try {
            FileInputStream fis = new FileInputStream(file);
            WordExtractor we = new WordExtractor(fis);

            String[] paragraphs = we.getParagraphText();

            for (int i = 0; i < paragraphs.length; i++) {
                String paragraph = paragraphs[i];
                searchInString(paragraph, targets, foundTargets, foundLines, foundChars, i);
            }
            fis.close();
            we.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class DocxReader extends TwoKeysSearcher {

    DocxReader(File file, Class<? extends StringMatcher> matcherClass, boolean caseSensitive) {
        super(file, matcherClass, caseSensitive, ContentSearchingResult.PARAGRAPHS_KEY, ContentSearchingResult.CHARS_KEY);
    }

    @Override
    protected void searchFile(List<String> targets, Set<String> foundTargets,
                              List<Integer> foundLines, List<Integer> foundChars) {
        try {
            FileInputStream fis = new FileInputStream(file);
            XWPFDocument docx = new XWPFDocument(fis);
            List<XWPFParagraph> paragraphs = docx.getParagraphs();
            for (int i = 0; i < paragraphs.size(); i++) {
                String par = paragraphs.get(i).getText();
                searchInString(par, targets, foundTargets, foundLines, foundChars, i);
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
