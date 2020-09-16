package trashsoftware.deepSearcher2.searcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class NaiveWordMatcher extends StringMatcher {
    private final String[] words;

    NaiveWordMatcher(String string) {
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

class HashMapWordSplitter extends StringMatcher {
    private final Map<Integer, List<String>> lengthMap = new HashMap<>();

    HashMapWordSplitter(String string) {
        super(string);
        splitToMap(string);
    }

    private void splitToMap(String s) {
        int sLen = s.length();
        StringBuilder wordBuilder = new StringBuilder();
        for (int i = 0; i < sLen; i++) {
            char c = s.charAt(i);
            if (WordSplitter.isWordChar(c)) {
                wordBuilder.append(c);
            } else if (wordBuilder.length() > 0) {
                String word = wordBuilder.toString();
                wordBuilder.setLength(0);
                putToMap(word);
            }
        }
        if (wordBuilder.length() > 0) putToMap(wordBuilder.toString());
    }

    private void putToMap(String word) {
        int wordLen = word.length();
        List<String> listOfThisLen = lengthMap.computeIfAbsent(wordLen, k -> new ArrayList<>());
        listOfThisLen.add(word);
    }

    @Override
    public int search(String pattern) {
        List<String> listOfThisLen = lengthMap.get(pattern.length());
        if (listOfThisLen == null) return -1;
        for (String s : listOfThisLen) {
            if (s.equals(pattern))
                return 1;
        }
        return -1;
    }
}

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
