package trashsoftware.deepSearcher2.searcher;

import java.util.List;
import java.util.ResourceBundle;

public class ContentResult {
    private Category key1;  // null if not specified
    private Category key2;  // null if not specified
    private List<Integer> values1;
    private List<Integer> values2;

    private List<StringValue> strValues;

    public ContentResult() {
    }

    public ContentResult(Category key1, List<Integer> values1) {
        this.key1 = key1;
        this.values1 = values1;
    }

    public ContentResult(Category key1, List<Integer> values1, Category key2, List<Integer> values2) {
        this.key1 = key1;
        this.values1 = values1;
        this.key2 = key2;
        this.values2 = values2;
    }

    public ContentResult(Category key1, List<Integer> values1, Category key2, List<Integer> values2,
                         List<StringValue> strValues) {
        this.key1 = key1;
        this.values1 = values1;
        this.key2 = key2;
        this.values2 = values2;
        this.strValues = strValues;
    }

    public Category getKey1() {
        return key1;
    }

    public Category getKey2() {
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
        if (key1 != null) {
            List<Integer> v1 = getValues1();
            String fmt1 = getFmtStringByKey(getKey1(), bundle);
            if (key2 != null) {
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

    private static String getFmtStringByKey(ICategory key, ResourceBundle bundle) {
        return key.display(bundle);
    }

    public static class StringValue {
        private final ValueCategory category;
        private final String realStringValue;

        public StringValue(ValueCategory category) {
            this.category = category;
            this.realStringValue = null;
        }

        public StringValue(String realStringValue) {
            this.category = null;
            this.realStringValue = realStringValue;
        }

        private String getShowString(ResourceBundle bundle) {
            if (category != null) {
                return getFmtStringByKey(category, bundle);
            } else {
                return realStringValue;
            }
        }
    }

    /**
     * An interface of two types of categories.
     */
    private interface ICategory {
        String display(ResourceBundle resourceBundle);
    }

    /**
     * A enum of all secondary keys.
     *
     * This is used for displaying details such as "page 2, line 15" in the result table.
     */
    public enum Category implements ICategory {
        CHAR("characterFmt"),
        LINE("lineFmt"),
        PARAGRAPH("paragraphFmt"),
        PAGE("pageFmt"),
        BLOCK("blockFmt"),
        ROW("rowFmt"),
        COLUMN("colFmt");

        private final String displayKey;

        Category(String displayKey) {
            this.displayKey = displayKey;
        }

        @Override
        public String display(ResourceBundle bundle) {
            return bundle.getString(displayKey);
        }
    }

    /**
     * A enum of all position keys.
     */
    public enum ValueCategory implements ICategory {
        TITLE("titleStr"),
        TEXT("textStr"),
        TABLE("tableStr");

        private final String displayKey;

        ValueCategory(String displayKey) {
            this.displayKey = displayKey;
        }

        @Override
        public String display(ResourceBundle bundle) {
            return bundle.getString(displayKey);
        }
    }
}
