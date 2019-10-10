package trashsoftware.deepSearcher2.searcher;

class NativeRegexMatcher implements StringMatcher {

    private String string;

    NativeRegexMatcher(String string) {
        this.string = string;
    }

    @Override
    public boolean contains(String target) {
        return string.matches(target);
    }

//    public static void main(String[] args) {
//        System.out.println("a1234.txt".matches("[a-zA-Z][0-9]*.[a-z]"));
//        System.out.println("a1234.txt".matches("[a-zA-Z][0-9]*\\.[a-z]*"));
//    }
}
