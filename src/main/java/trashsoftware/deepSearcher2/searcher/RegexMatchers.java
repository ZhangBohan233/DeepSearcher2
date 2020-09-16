package trashsoftware.deepSearcher2.searcher;

class NativeRegexMatcher extends StringMatcher {

    private final String string;

    NativeRegexMatcher(String string) {
        super(string);
        this.string = string;
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
