package trashsoftware.deepSearcher2.searcher.matchers.wordMatchers;

import trashsoftware.deepSearcher2.searcher.matchers.StringMatcher;

public class NaiveWordMatcher extends StringMatcher {
    private final String[] words;

    public NaiveWordMatcher(String string) {
        super(string);
        this.words = WordSplitter.split(string);
    }

    @Override
    public int search(String pattern) {
        int i = 0;
        for (String s : words) {
            if (s.equals(pattern)) {
                return i;
            }
            i += s.length() + 1;
        }
        return -1;
    }
}
