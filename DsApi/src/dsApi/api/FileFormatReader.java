package dsApi.api;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

/**
 * An API for developers to customize the supported file formats of file content searching.
 * <p>
 * There are two subclasses in this API. It is strongly recommended for developers to extend those two
 * subclasses, {@link SimpleReader}, {@link SplitterReader}.
 *
 * @see SimpleReader
 * @see SplitterReader
 * @since 1.1
 */
@SuppressWarnings("unused")
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
     * only one description. That is, all extensions share a same description.
     *
     * @param locale the locale
     * @return the description of file formats supported by this class
     */
    public abstract String description(Locale locale);

    /**
     * Returns the string content of the whole file.
     *
     * @param file the file to be read
     * @return the string content of the whole file
     * @throws IOException if any IOError occurs during reading
     */
    public abstract String readFile(File file) throws IOException;

    /**
     * Returns the formatter string of the word 'nth character' in the locale {@code locale}.
     * <p>
     * For example, in English, this method should return {@code "character %d"}, meaning the %d th character.
     * 例如，若语言为中文，则该方法应返回{@code "第%d字"}。
     *
     * @param locale the locale
     * @return the translation of the word 'character' in the language of {@code locale}
     */
    public abstract String characterFormat(Locale locale);

    /**
     * Returns the formatter string of the splitter to show on the gui.
     * <p>
     * For example, the splitter can be pages, paragraphs, lines, etc.
     * The returned value of this method should be similar as in {@link FileFormatReader#characterFormat(Locale)}
     *
     * @param locale the locale
     * @return the formatter string of the splitter to show on the gui, or
     * {@code null} if {@link FileFormatReader#hasSplitter()} returns {@code false}
     */
    public abstract String splitterFormat(Locale locale);

    /**
     * Returns an array consists of strings separated according to the splitter.
     * <p>
     * For example, if the splitter is line, then the returning value of this method is an array of all lines
     * read from the file.
     *
     * @param file the file to be read
     * @return an array consists of strings separated according to the splitter
     * @throws IOException if any IOError occurs during reading
     */
    public abstract String[] readBySplitter(File file) throws IOException;

    /**
     * Returns whether this reader has a splitter.
     * <p>
     * For the information of splitter, see {@link FileFormatReader#splitterFormat(Locale)}
     *
     * @return whether this reader has a splitter
     */
    public abstract boolean hasSplitter();

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
