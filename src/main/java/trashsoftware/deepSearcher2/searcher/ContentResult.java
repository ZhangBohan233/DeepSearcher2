package trashsoftware.deepSearcher2.searcher;

import java.util.List;
import java.util.ResourceBundle;

/**
 * A result of file content search.
 * <p>
 * This may include some keys, for example, pages, lines.
 * Note that all indexes are counted from 1.
 */
public abstract class ContentResult {
    private List<Integer> values1;
    private List<Integer> values2;

    private List<StringValue> strValues;

    ContentResult() {
    }

    ContentResult(List<Integer> values1) {
        this.values1 = values1;
    }

    ContentResult(List<Integer> values1, List<Integer> values2) {
        this.values1 = values1;
        this.values2 = values2;
    }

    ContentResult(List<Integer> values1, List<Integer> values2, List<StringValue> strValues) {
        this.values1 = values1;
        this.values2 = values2;
        this.strValues = strValues;
    }

    private static String getFmtStringByKey(ICategory key, ResourceBundle bundle) {
        return key.display(bundle);
    }

    protected abstract String getKey1FmtString(ResourceBundle resourceBundle);

    protected abstract String getKey2FmtString(ResourceBundle resourceBundle);

    public List<Integer> getValues1() {
        return values1;
    }

    public List<Integer> getValues2() {
        return values2;
    }

    /**
     * Returns the showing string of this content result, including all specified keys, e.g. pages, lines.
     * <p>
     * If no keys are specified, returns {@code null}
     *
     * @param bundle the language bundle
     * @return the showing content result
     */
    public String getAsString(ResourceBundle bundle) {
        StringBuilder builder = new StringBuilder();
        String key1Fmt = getKey1FmtString(bundle);
        if (key1Fmt != null) {
            List<Integer> v1 = getValues1();
            String key2Fmt = getKey2FmtString(bundle);
            if (key2Fmt != null) {
                List<Integer> v2 = getValues2();
                if (v1.size() != v2.size()) throw new RuntimeException("Unexpected unequal size.");
                if (strValues != null) {
                    if (v1.size() != strValues.size()) throw new RuntimeException("Unexpected unequal size.");
                    for (int i = 0; i < v1.size(); i++) {
                        builder.append(String.format(key1Fmt, v1.get(i)))
                                .append(", ")
                                .append(String.format(key2Fmt, v2.get(i)))
                                .append(", ")
                                .append(strValues.get(i).getShowString(bundle))
                                .append('\n');
                    }
                } else {
                    for (int i = 0; i < v1.size(); i++) {
                        builder.append(String.format(key1Fmt, v1.get(i)))
                                .append(", ")
                                .append(String.format(key2Fmt, v2.get(i)))
                                .append('\n');
                    }
                }
            } else {
                for (Integer integer : v1) {
                    builder.append(String.format(key1Fmt, integer))
                            .append('\n');
                }
            }
        } else return null;  // no keys are specified
        return builder.toString();
    }

    public static class Native extends ContentResult {
        private Category key1;  // null if not specified
        private Category key2;  // null if not specified

        public Native() {
        }

        public Native(Category key1, List<Integer> values1) {
            super(values1);
            this.key1 = key1;
        }

        public Native(Category key1, List<Integer> values1, Category key2, List<Integer> values2) {
            super(values1, values2);
            this.key1 = key1;
            this.key2 = key2;
        }

        public Native(Category key1, List<Integer> values1, Category key2, List<Integer> values2,
                             List<StringValue> strValues) {
            super(values1, values2, strValues);

            this.key1 = key1;
            this.key2 = key2;
        }

        @Override
        protected String getKey1FmtString(ResourceBundle resourceBundle) {
            return ContentResult.getFmtStringByKey(key1, resourceBundle);
        }

        @Override
        protected String getKey2FmtString(ResourceBundle resourceBundle) {
            return ContentResult.getFmtStringByKey(key2, resourceBundle);
        }
    }

    public static class External extends ContentResult {
        private String key1fmt;
        private String key2fmt;

        public External() {
        }

        public External(String key1fmt, List<Integer> values1) {
            super(values1);

            this.key1fmt = key1fmt;
        }

        public External(String key1fmt, List<Integer> values1, String key2fmt, List<Integer> values2) {
            super(values1, values2);

            this.key1fmt = key1fmt;
            this.key2fmt = key2fmt;
        }

        @Override
        protected String getKey1FmtString(ResourceBundle resourceBundle) {
            return key1fmt;
        }

        @Override
        protected String getKey2FmtString(ResourceBundle resourceBundle) {
            return key2fmt;
        }
    }

    /**
     * A enum of all secondary keys.
     * <p>
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

    /**
     * An interface of two types of categories.
     */
    private interface ICategory {
        String display(ResourceBundle resourceBundle);
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
}
