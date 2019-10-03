package trashsoftware.deepSearcher2.searcher;

class NaiveMatcher implements StringMatcher {

    private String string;

    NaiveMatcher(String s) {
        this.string = s;
    }

    @Override
    public boolean contains(String target) {
        if (target.length() > string.length()) return false;
        for (int i = 0; i < string.length() - target.length() + 1; i++) {
            int j;
            for (j = 0; j < target.length(); j++) {
                if (string.charAt(i + j) != target.charAt(j)) break;
            }
            if (j == target.length()) return true;
        }
        return false;
    }
}

class NativeMatcher implements StringMatcher {
    private String string;

    NativeMatcher(String s) {
        this.string = s;
    }

    @Override
    public boolean contains(String target) {
        return string.contains(target);
    }
}
