package trashsoftware.deepSearcher2.searcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


class NaiveMatcher extends StringMatcher {

    NaiveMatcher(String s) {
        super(s);
    }

    @Override
    public int search(String target) {
        return naiveMatch(string, target);
    }

    private static int naiveMatch(String string, String pattern) {
        if (pattern.length() > string.length()) return -1;
        for (int i = 0; i < string.length() - pattern.length() + 1; i++) {
            int j;
            for (j = 0; j < pattern.length(); j++) {
                if (string.charAt(i + j) != pattern.charAt(j)) break;
            }
            if (j == pattern.length()) return i;
        }
        return -1;
    }
}

class NativeMatcher extends StringMatcher {

    NativeMatcher(String s) {
        super(s);
    }

    @Override
    public int search(String target) {
        return string.indexOf(target);
    }
}

class KMPMatcher extends StringMatcher {

    private int[] next;

    KMPMatcher(String s) {
        super(s);
    }

    private void calculateNext(String target) {
        int tLen = target.length();
        next = new int[tLen];
        next[0] = -1;
        int k = -1;
        int j = 0;
        while (j < tLen - 1) {
            if (k == -1 || target.charAt(j) == target.charAt(k)) {
                j++;
                k++;
                if (target.charAt(j) == target.charAt(k)) {
                    next[j] = next[k];
                } else {
                    next[j] = k;
                }
            } else {
                k = next[k];
            }
        }
    }

    @Override
    public int search(String target) {
        int sLen = string.length();
        int tLen = target.length();
        calculateNext(target);
        int i = 0, j = 0;
        while (i < sLen && j < tLen) {
            if (j == -1 || string.charAt(i) == target.charAt(j)) {
                i++;
                j++;
            } else {
                j = next[j];
            }
        }
        if (j == tLen) return i - j;
        else return -1;
    }

    public static void main(String[] args) {
        String txt = "BBC ABCDAB CDABABCDABCDABDE";
        String pat = "ABCDABD";
        KMPMatcher matcher = new KMPMatcher(txt);
        System.out.println(matcher.search(pat));
    }
}


class BoyerMooreMatcher extends StringMatcher {

    BoyerMooreMatcher(String string) {
        super(string);
    }

    @Override
    public int search(String pattern) {
        return -1;
    }
}


class SundayMatcher extends StringMatcher {

    private Map<Character, Integer> indices;

    SundayMatcher(String string) {
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

    public static void main(String[] args) {
        String txt = "猫把狗吃了";
        String pat = "吃了";
        SundayMatcher matcher = new SundayMatcher(txt);
        System.out.println(matcher.search(pat));
    }
}