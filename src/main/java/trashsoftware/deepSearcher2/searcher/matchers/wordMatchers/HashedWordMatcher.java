package trashsoftware.deepSearcher2.searcher.matchers.wordMatchers;

import trashsoftware.deepSearcher2.searcher.matchers.StringMatcher;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A word matcher, implemented with hashing algorithm.
 */
public class HashedWordMatcher extends StringMatcher {
    private final Map<Integer, Set<String>> lengthMap = new HashMap<>();

    public HashedWordMatcher(String string) {
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
        Set<String> listOfThisLen = lengthMap.computeIfAbsent(wordLen, k -> new HashSet<>());
        listOfThisLen.add(word);
    }

    @Override
    public int search(String pattern) {
        Set<String> listOfThisLen = lengthMap.get(pattern.length());
        if (listOfThisLen == null) return -1;
        return listOfThisLen.contains(pattern) ? 1 : -1;
    }
}
