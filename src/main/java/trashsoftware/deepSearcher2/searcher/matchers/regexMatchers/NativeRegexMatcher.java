package trashsoftware.deepSearcher2.searcher.matchers.regexMatchers;

import trashsoftware.deepSearcher2.searcher.matchers.StringMatcher;

public class NativeRegexMatcher extends StringMatcher {

    public NativeRegexMatcher(String string) {
        super(string);
    }

    @Override
    public int search(String target) {
        return string.matches(target) ? 1 : -1;
    }

//    public static void main(String[] args) {
//        System.out.println("a1234.txt".matches("[a-zA-Z][0-9]*.[a-z]"));
//        System.out.println("a1234.txt".matches("[a-zA-Z][0-9]*\\.[a-z]*"));
//    }
}
