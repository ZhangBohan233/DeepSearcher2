package dsApi.api;

/**
 * A reader that reads a file separated by a splitter.
 * <p>
 * For example, a Microsoft Word file (.docx) can be splitted by pages, paragraphs.
 *
 * @see FileFormatReader
 * @since 1.1
 */
public abstract class SplitterReader extends FileFormatReader {

    /**
     * The constructor.
     */
    public SplitterReader() {
        super();
    }

    /**
     * @see FileFormatReader#hasSplitter()
     */
    @Override
    public final boolean hasSplitter() {
        return true;
    }
}
