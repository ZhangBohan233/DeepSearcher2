package trashsoftware.deepSearcher2.searcher.matchers.regularMatchers;

import trashsoftware.deepSearcher2.searcher.matchers.StringMatcher;

/**
 * A string matcher that is implemented according to the Knuth-Morris-Pratt (KMP) algorithm
 */
public class KMPMatcher extends StringMatcher {

    private int[] next;

    public KMPMatcher(String s) {
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
}
