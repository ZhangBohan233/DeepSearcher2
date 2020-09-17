package trashsoftware.deepSearcher2.searcher;

import java.util.List;
import java.util.ResourceBundle;

public class ContentSearchingResult {

    public static final int CHARS_KEY = 1;
    public static final int LINES_KEY = 2;
    public static final int PARAGRAPHS_KEY = 3;
    public static final int PAGES_KEY = 4;
    public static final int BLOCKS_KEY = 5;

    public static final int TITLE_VALUE = 101;
    public static final int TEXT_VALUE = 102;
    public static final int TABLE_VALUE = 103;

    private int key1;  // 0 if not specified
    private int key2;  // 0 if not specified
    private List<Integer> values1;
    private List<Integer> values2;

    /**
     * This list stores int identifiers starts with 100, e.g., TITLE_VALUE = 101
     */
    private List<Integer> strValues;

    public ContentSearchingResult() {
    }

    public ContentSearchingResult(int key1, List<Integer> values1) {
        this.key1 = key1;
        this.values1 = values1;
    }

    public ContentSearchingResult(int key1, List<Integer> values1, int key2, List<Integer> values2) {
        this.key1 = key1;
        this.values1 = values1;
        this.key2 = key2;
        this.values2 = values2;
    }

    public ContentSearchingResult(int key1, List<Integer> values1, int key2, List<Integer> values2,
                                  List<Integer> strValues) {
        this.key1 = key1;
        this.values1 = values1;
        this.key2 = key2;
        this.values2 = values2;
        this.strValues = strValues;
    }

    public int getKey1() {
        return key1;
    }

    public int getKey2() {
        return key2;
    }

    public List<Integer> getValues1() {
        return values1;
    }

    public List<Integer> getValues2() {
        return values2;
    }

    public String getAsString(ResourceBundle bundle) {
        StringBuilder builder = new StringBuilder();
        if (key1 != 0) {
            List<Integer> v1 = getValues1();
            String showK1 = getShowStringByKey(getKey1(), bundle);
            String ordNum = bundle.getString("ordNum");
            if (key2 != 0) {
                List<Integer> v2 = getValues2();
                if (v1.size() != v2.size()) throw new RuntimeException("Unexpected unequal size.");
                String showK2 = getShowStringByKey(getKey2(), bundle);
                if (strValues != null) {
                    if (v1.size() != strValues.size()) throw new RuntimeException("Unexpected unequal size.");
                    for (int i = 0; i < v1.size(); i++) {
                        builder.append(ordNum)
                                .append(v1.get(i))
                                .append(showK1)
                                .append(", ")
                                .append(ordNum)
                                .append(v2.get(i))
                                .append(showK2)
                                .append(", ")
                                .append(getShowStringByKey(strValues.get(i), bundle))
                                .append('\n');
                    }
                } else {
                    for (int i = 0; i < v1.size(); i++) {
                        builder.append(ordNum)
                                .append(v1.get(i))
                                .append(showK1)
                                .append(", ")
                                .append(ordNum)
                                .append(v2.get(i))
                                .append(showK2)
                                .append('\n');
                    }
                }
            } else {
                for (Integer integer : v1) {
                    builder.append(ordNum)
                            .append(integer)
                            .append(showK1)
                            .append('\n');
                }
            }
        }
        return builder.toString();
    }

    private static String getShowStringByKey(int key, ResourceBundle bundle) {
        switch (key) {
            case LINES_KEY:
                return bundle.getString("lineNum");
            case PARAGRAPHS_KEY:
                return bundle.getString("paragraphNum");
            case PAGES_KEY:
                return bundle.getString("pageNum");
            case CHARS_KEY:
                return bundle.getString("characterNum");
            case BLOCKS_KEY:
                return bundle.getString("blockNum");
            case TITLE_VALUE:
                return bundle.getString("titleStr");
            case TEXT_VALUE:
                return bundle.getString("textStr");
            case TABLE_VALUE:
                return bundle.getString("tableStr");
            default:
                throw new RuntimeException("No such key");
        }
    }
}
