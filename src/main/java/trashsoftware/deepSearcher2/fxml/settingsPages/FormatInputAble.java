package trashsoftware.deepSearcher2.fxml.settingsPages;

/**
 * An interface that supports the {@code FormatInputBox} to input a format.
 */
public interface FormatInputAble {

    /**
     * Adds a format to this, from a {@code FormatInputBox}.
     *
     * @param ext         the extension of the format
     * @param description description of format
     */
    void addFormat(String ext, String description);
}
