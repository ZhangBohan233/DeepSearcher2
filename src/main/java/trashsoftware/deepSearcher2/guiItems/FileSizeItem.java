package trashsoftware.deepSearcher2.guiItems;

import trashsoftware.deepSearcher2.fxml.Client;
import trashsoftware.deepSearcher2.util.Util;

/**
 * A class that contains a file's size and can convert size in byte to a readable form.
 * <p>
 * This class is comparable by its size.
 */
public class FileSizeItem implements Comparable<FileSizeItem> {

    private final long size;

    FileSizeItem(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        if (size < 0) return "";
        return Util.sizeToReadable(size, Client.getBundle().getString("bytes"));
    }

    @Override
    public int compareTo(FileSizeItem o) {
        return Long.compare(size, o.size);
    }
}
