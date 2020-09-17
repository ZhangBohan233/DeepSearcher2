package trashsoftware.deepSearcher2.searcher.matchers.regularMatchers;

import trashsoftware.deepSearcher2.searcher.matchers.StringMatcher;

public class NativeMatcher extends StringMatcher {

    public NativeMatcher(String s) {
        super(s);
    }

    @Override
    public int search(String target) {
        return string.indexOf(target);
    }
}
