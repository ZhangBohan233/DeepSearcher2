package dsApi.api;

/**
 * A reader that reads a file separated by two levels of keys.
 *
 * @see FileFormatReader
 * @since 1.1
 */
public abstract class TwoKeysReader extends FileFormatReader {

    /**
     * The constructor.
     */
    public TwoKeysReader() {
        super();
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
        return true;
    }
}
