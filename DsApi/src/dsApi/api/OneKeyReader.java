package dsApi.api;

import java.io.File;
import java.util.Locale;

/**
 * A reader that reads a file separated by a primary key.
 *
 * @see FileFormatReader
 * @since 1.1
 */
public abstract class OneKeyReader extends FileFormatReader {

    /**
     * The constructor.
     */
    public OneKeyReader() {
        super();
    }

    /**
     * Returns {@code null} since this reader has no secondary keys.
     *
     * @param file the file to be read
     * @return {@code null} this reader has no secondary keys
     */
    @Override
    public final String[][] readBySecondaryKey(File file) {
        return null;
    }

    /**
     * Returns {@code null} since this reader has no secondary keys.
     *
     * @return {@code null} this reader has no secondary keys
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
        return true;
    }

    /**
     * @see FileFormatReader#hasSecondaryKey()
     */
    @Override
    public final boolean hasSecondaryKey() {
        return false;
    }
}
