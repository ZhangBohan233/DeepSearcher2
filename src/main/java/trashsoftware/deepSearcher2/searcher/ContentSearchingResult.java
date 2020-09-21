package trashsoftware.deepSearcher2.searcher;

import java.util.List;
import java.util.ResourceBundle;

public class ContentSearchingResult {

    public static final int CHARS_KEY = 1;
    public static final int LINES_KEY = 2;
    public static final int PARAGRAPHS_KEY = 3;
    public static final int PAGES_KEY = 4;
    public static final int BLOCKS_KEY = 5;
    public static final int ROWS_KEY = 6;
    public static final int COLUMNS_KEY = 7;

    public static final int TITLE_VALUE = 101;
    public static final int TEXT_VALUE = 102;
    public static final int TABLE_VALUE = 103;

    private int key1;  // 0 if not specified
    private int key2;  // 0 if not specified
    private List<Integer> values1;
    private List<Integer> values2;

    private List<StringValue> strValues;

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
                                  List<StringValue> strValues) {
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
            String fmt1 = getFmtStringByKey(getKey1(), bundle);
            if (key2 != 0) {
                List<Integer> v2 = getValues2();
                if (v1.size() != v2.size()) throw new RuntimeException("Unexpected unequal size.");
                String fmt2 = getFmtStringByKey(getKey2(), bundle);
                if (strValues != null) {
                    if (v1.size() != strValues.size()) throw new RuntimeException("Unexpected unequal size.");
                    for (int i = 0; i < v1.size(); i++) {
                        builder.append(String.format(fmt1, v1.get(i)))
                                .append(", ")
                                .append(String.format(fmt2, v2.get(i)))
                                .append(", ")
                                .append(strValues.get(i).getShowString(bundle))
                                .append('\n');
                    }
                } else {
                    for (int i = 0; i < v1.size(); i++) {
                        builder.append(String.format(fmt1, v1.get(i)))
                                .append(", ")
                                .append(String.format(fmt2, v2.get(i)))
                                .append('\n');
                    }
                }
            } else {
                for (Integer integer : v1) {
                    builder.append(String.format(fmt1, integer))
                            .append('\n');
                }
            }
        }
        return builder.toString();
    }

    private static String getFmtStringByKey(int key, ResourceBundle bundle) {
        switch (key) {
            case LINES_KEY:
                return bundle.getString("lineFmt");
            case PARAGRAPHS_KEY:
                return bundle.getString("paragraphFmt");
            case PAGES_KEY:
                return bundle.getString("pageFmt");
            case CHARS_KEY:
                return bundle.getString("characterFmt");
            case BLOCKS_KEY:
                return bundle.getString("blockFmt");
            case ROWS_KEY:
                return bundle.getString("rowFmt");
            case COLUMNS_KEY:
                return bundle.getString("colFmt");
            case TITLE_VALUE:
                return bundle.getString("titleStr");
            case TEXT_VALUE:
                return bundle.getString("textStr");
            case TABLE_VALUE:
                return bundle.getString("tableStr");
            default:
                throw new RuntimeException("No such key: " + key);
        }
    }

    public static class StringValue {
        private int category = 0;
        private String realStringValue;

        public StringValue(int category) {
            this.category = category;
        }

        public StringValue(String realStringValue) {
            this.realStringValue = realStringValue;
        }

        private String getShowString(ResourceBundle bundle) {
            if (category != 0 || realStringValue == null) {
                return getFmtStringByKey(category, bundle);
            } else {
                return realStringValue;
            }
        }
    }
}
