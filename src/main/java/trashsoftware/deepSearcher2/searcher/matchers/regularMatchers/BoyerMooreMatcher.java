package trashsoftware.deepSearcher2.searcher.matchers.regularMatchers;

import trashsoftware.deepSearcher2.searcher.matchers.StringMatcher;

public class BoyerMooreMatcher extends StringMatcher {

    public BoyerMooreMatcher(String string) {
        super(string);
    }

    @Override
    public int search(String pattern) {
        return -1;
    }
}
