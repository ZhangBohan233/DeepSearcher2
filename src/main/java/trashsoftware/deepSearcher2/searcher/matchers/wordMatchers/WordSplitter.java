package trashsoftware.deepSearcher2.searcher.matchers.wordMatchers;

import java.util.ArrayList;
import java.util.List;

class WordSplitter {

    static String[] split(String s) {
        List<String> list = new ArrayList<>();
        int sLen = s.length();
        StringBuilder wordBuilder = new StringBuilder();
        for (int i = 0; i < sLen; i++) {
            char c = s.charAt(i);
            if (isWordChar(c)) {
                wordBuilder.append(c);
            } else if (wordBuilder.length() > 0) {
                list.add(wordBuilder.toString());
                wordBuilder.setLength(0);
            }
        }
        if (wordBuilder.length() > 0) list.add(wordBuilder.toString());
        return list.toArray(new String[0]);
    }

    static boolean isWordChar(char c) {
        return Character.isAlphabetic(c) || Character.isDigit(c) || c == '_';
    }
}
