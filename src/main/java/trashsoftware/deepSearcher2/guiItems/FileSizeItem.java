package trashsoftware.deepSearcher2.guiItems;

import trashsoftware.deepSearcher2.controllers.Client;
import trashsoftware.deepSearcher2.util.Util;

import java.util.ResourceBundle;

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
        return Util.sizeToReadable(size, Client.getBundle().getString("bytes"));
    }

    @Override
    public int compareTo(FileSizeItem o) {
        return Long.compare(size, o.size);
    }
}
