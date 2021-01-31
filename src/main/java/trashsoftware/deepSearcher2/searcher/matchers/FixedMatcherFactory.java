package trashsoftware.deepSearcher2.searcher.matchers;

import trashsoftware.deepSearcher2.searcher.InvalidClassException;

import java.lang.reflect.InvocationTargetException;

/**
 * A matcher factory that uses a fixed algorithm to search.
 */
public class FixedMatcherFactory extends MatcherFactory {

    private final Class<? extends StringMatcher> matcherClass;

    public FixedMatcherFactory(Class<? extends StringMatcher> matcherClass) {
        this.matcherClass = matcherClass;
    }

    public StringMatcher createMatcher(String string) {
        try {
            return matcherClass.getDeclaredConstructor(String.class).newInstance(string);
        } catch (InvocationTargetException
                | NoSuchMethodException
                | InstantiationException
                | IllegalAccessException e) {
            throw new InvalidClassException("Unexpected matcher. ", e);
        }
    }
}
