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
//        Pattern pattern = Pattern.compile("about five0");
//        System.out.println();
//    }
}
