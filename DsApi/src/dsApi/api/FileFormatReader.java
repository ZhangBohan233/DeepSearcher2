package dsApi.api;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

/**
 * An API for developers to customize the supported file formats of file content searching.
 * <p>
 * There are three subclasses in this API. It is strongly recommended for developers to extend those three
 * subclasses, {@link SimpleReader}, {@link OneKeyReader}, {@link TwoKeysReader}.
 *
 * @see SimpleReader
 * @see OneKeyReader
 * @see TwoKeysReader
 * @since 1.1
 */
public abstract class FileFormatReader {

    /**
     * The constructor.
     */
    public FileFormatReader() {
    }

    /**
     * Returns the file format extensions supported by this class.
     * <p>
     * The returning values should be in lower case and without dot.
     * e.g. {@code new String[]{"txt", "text"}}
     *
     * @return the file format extensions supported by this class, in lower case, without dot
     */
    public abstract String[] extensions();

    /**
     * Returns the description of file formats supported by this class.
     * <p>
     * No matter {@link FileFormatReader#extensions()} returns how many extensions, this method should return
     * only one description.
     *
     * @param locale the locale
     * @return the description of file formats supported by this class
     */
    public abstract String description(Locale locale);

    /**
     * Returns the description of the primary key.
     * <p>
     * For example, "primary key" can be pages, paragraphs, lines, etc.
     *
     * @param locale the locale
     * @return the description of the primary key, or
     * {@code null} if {@link FileFormatReader#hasPrimaryKey()} returns {@code false}
     */
    public abstract String primaryKeyDescription(Locale locale);

    /**
     * Returns the description of the secondary key.
     * <p>
     * For example, if "primary key" is "line", then "secondary key" can be "character"
     *
     * @param locale the locale
     * @return the description of the secondary key,
     * or {@code null} if {@link FileFormatReader#hasPrimaryKey()} returns {@code true} but
     * {@link FileFormatReader#hasSecondaryKey()} returns {@code false}
     */
    public abstract String secondaryKeyDescription(Locale locale);

    /**
     * Returns the string content of the whole file.
     *
     * @param file the file to be read
     * @return the string content of the whole file
     * @throws IOException if any IOError occurs during reading
     */
    public abstract String readFile(File file) throws IOException;

    /**
     * Returns an array consists of strings separated according to "primary key".
     * <p>
     * For example, if "primary key" is line, then the returning value of this method is an array of all lines
     * read from the file.
     *
     * @param file the file to be read
     * @return an array consists of strings separated according to "primary key"
     * @throws IOException if any IOError occurs during reading
     */
    public abstract String[] readByPrimaryKey(File file) throws IOException;

    /**
     * Returns a 2d array consists of arrays of strings separated according to "primary key", each sub-array
     * is formed by strings separated according to "secondary key".
     * <p>
     * For example, if "primary key" is paragraph and "secondary key" is line,
     * then the returning value of this method is an 2d array. Each element array represents a paragraph, and
     * each string in the element array represents a line in that paragraph.
     *
     * @param file the file to be read
     * @return a 2d array consists of arrays of strings separated according to "primary key", each sub-array
     * is formed by strings separated according to "secondary key"
     * @throws IOException if any IOError occurs during reading
     */
    public abstract String[][] readBySecondaryKey(File file) throws IOException;

    /**
     * Returns whether this reader has a primary key.
     *
     * @return whether this reader has a primary key
     */
    public abstract boolean hasPrimaryKey();

    /**
     * Returns whether this reader has a secondary key.
     *
     * @return whether this reader has a secondary key
     */
    public abstract boolean hasSecondaryKey();

    @Override
    public int hashCode() {
        return Arrays.hashCode(extensions());
    }

    @Override
    public boolean equals(Object obj) {
        return this.getClass() == obj.getClass() &&
                Arrays.equals(this.extensions(), ((FileFormatReader) obj).extensions());
    }
}
