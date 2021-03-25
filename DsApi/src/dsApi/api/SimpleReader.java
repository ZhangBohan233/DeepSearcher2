package dsApi.api;

import java.io.File;
import java.util.Locale;

/**
 * A reader that reads the file as a whole string.
 *
 * @see FileFormatReader
 * @since 1.1
 */
public abstract class SimpleReader extends FileFormatReader {

    /**
     * The constructor.
     */
    public SimpleReader() {
        super();
    }

    /**
     * Returns {@code null} since simple reader does not have any keys.
     *
     * @param file the file to be read
     * @return {@code null} since simple reader does not have any keys
     */
    @Override
    public String[] readByPrimaryKey(File file) {
        return null;
    }

    /**
     * Returns {@code null} since simple reader does not have any keys.
     *
     * @param file the file to be read
     * @return {@code null} since simple reader does not have any keys
     */
    @Override
    public final String[][] readBySecondaryKey(File file) {
        return null;
    }

    /**
     * Returns {@code null} since simple reader does not have any keys.
     *
     * @return {@code null} since simple reader does not have any keys
     */
    @Override
    public final String primaryKeyDescription(Locale locale) {
        return null;
    }

    /**
     * Returns {@code null} since simple reader does not have any keys.
     *
     * @return {@code null} since simple reader does not have any keys
     */
    @Override
    public final String secondaryKeyDescription(Locale locale) {
        return null;
    }

    /**
     * @see FileFormatReader#hasPrimaryKey()
     */
    @Override
    public final boolean hasPrimaryKey() {
        return false;
    }

    /**
     * @see FileFormatReader#hasSecondaryKey()
     */
    @Override
    public final boolean hasSecondaryKey() {
        return false;
    }
}
