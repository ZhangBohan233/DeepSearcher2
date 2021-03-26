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
     * Returns {@code null} since simple reader does not have a splitter.
     *
     * @param file the file to be read
     * @return {@code null} since simple reader does not have a splitter
     */
    @Override
    public String[] readBySplitter(File file) {
        return null;
    }

    /**
     * Returns {@code null} since simple reader does not have a splitter.
     *
     * @return {@code null} since simple reader does not have a splitter
     */
    @Override
    public final String splitterFormat(Locale locale) {
        return null;
    }

    /**
     * @see FileFormatReader#hasSplitter()
     */
    @Override
    public final boolean hasSplitter() {
        return false;
    }
}
