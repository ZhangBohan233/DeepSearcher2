package trashsoftware.deepSearcher2.searcher.matchers.regularMatchers;

import trashsoftware.deepSearcher2.searcher.matchers.StringMatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A string matcher that is implemented according to the "Sunday" algorithm
 */
public class SundayMatcher extends StringMatcher {

    /**
     * Reason for not using array: Unicode character
     */
    private Map<Character, Integer> indices;

    public SundayMatcher(String string) {
        super(string);
    }

    private void calculateIndices(String pat) {
        indices = new HashMap<>();
        int pLen = pat.length();
        for (int i = 0; i < pLen; i++) {
            indices.put(pat.charAt(i), i);
        }
    }

    @Override
    public int search(String pat) {
        calculateIndices(pat);
        int sLen = string.length();
        int pLen = pat.length();
        int s = 0;
        int end = sLen - pLen;
        while (s <= end) {
            int i;
            for (i = 0; i < pLen; i++) {
                if (string.charAt(s + i) != pat.charAt(i)) break;
            }
            if (i == pLen) return s;
            if (s == end) break;

            char next = string.charAt(s + pLen);
            Integer lastPos = Objects.requireNonNullElse(indices.get(next), -1);
            s += pLen - lastPos;
        }
        return -1;
    }
}
