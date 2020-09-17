package trashsoftware.deepSearcher2.searcher.matchers.regularMatchers;

import org.apache.commons.collections4.Factory;
import trashsoftware.deepSearcher2.searcher.matchers.FixedMatcherFactory;
import trashsoftware.deepSearcher2.searcher.matchers.MatcherFactory;
import trashsoftware.deepSearcher2.searcher.matchers.StringMatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class SundayMatcher extends StringMatcher {

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

    public static void main(String[] args) {
        int n = 1000;
        int len = 10000;
        String[] strings = new String[n];
        String pat = "this is the target. that is not a target. This string is long";
        for (int x = 0; x < n; x++) {
            StringBuilder sb = new StringBuilder();
            int pos = (int) (Math.random() * len);
            int i = 0;
            for (; i < pos; i++) {
                sb.append((char) (Math.random() * 128));
            }
            sb.append(pat);
            for (; i < len; i++) {
                sb.append((char) (Math.random() * 128));
            }
            String str = sb.toString();
            strings[x] = str;
        }

//        MatcherFactory mf = new FixedMatcherFactory(NativeMatcher.class);
//        MatcherFactory mf = new FixedMatcherFactory(NaiveMatcher.class);
        MatcherFactory mf = new FixedMatcherFactory(SundayMatcher.class);
        long t1 = System.currentTimeMillis();
        for (int x = 0; x < n; x++) {
            StringMatcher matcher = mf.createMatcher(strings[x]);
            for (int y = 0; y < 10; y++)
                if (matcher.search(pat) < 0) System.out.println("err");
        }
        long t2 = System.currentTimeMillis();
        System.out.println(t2 - t1);
    }
}
