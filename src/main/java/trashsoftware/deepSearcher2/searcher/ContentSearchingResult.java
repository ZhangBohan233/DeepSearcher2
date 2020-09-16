package trashsoftware.deepSearcher2.searcher;

import java.io.File;
import java.util.List;

public class ContentSearchingResult {

    public static final String CHARS_KEY = "chars";
    public static final String LINES_KEY = "lines";
    public static final String PARAGRAPHS_KEY = "paragraphs";
    public static final String PAGES_KEY = "pages";

    private String key1;
    private String key2;
    private List<Integer> values1;
    private List<Integer> values2;

    ContentSearchingResult() {
    }

    ContentSearchingResult(String key1, List<Integer> values1) {
        this.key1 = key1;
        this.values1 = values1;
    }

    ContentSearchingResult(String key1, List<Integer> values1, String key2, List<Integer> values2) {
        this.key1 = key1;
        this.values1 = values1;
        this.key2 = key2;
        this.values2 = values2;
    }

    public String getKey1() {
        return key1;
    }

    public String getKey2() {
        return key2;
    }

    public List<Integer> getValues1() {
        return values1;
    }

    public List<Integer> getValues2() {
        return values2;
    }
}
