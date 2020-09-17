package trashsoftware.deepSearcher2.searcher.matchers;

import trashsoftware.deepSearcher2.searcher.InvalidClassException;

import java.lang.reflect.InvocationTargetException;

public abstract class StringMatcher {

    protected final String string;

    public StringMatcher(String string) {
        this.string = string;
    }

    /**
     * Returns the first occurrence position of {@code pattern} in {@code this.string}, {@code -1} if not found
     *
     * @param pattern the target string fragment
     * @return the first occurrence position of {@code pattern} in {@code this.string}, {@code -1} if not found
     */
    public abstract int search(String pattern);

    /**
     * Creates a matcher instance of {@code matcherClass}, with {@code string} as constructor argument.
     *
     * @param matcherClass the matcher's class to be created
     * @param string       the argument, which is the long string
     * @return the new matcher instance
     */
    public static StringMatcher createMatcher(Class<? extends StringMatcher> matcherClass, String string) {
        try {
            return matcherClass.getDeclaredConstructor(String.class).newInstance(string);
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new InvalidClassException("Unexpected matcher. ", e);
        }
    }
}


