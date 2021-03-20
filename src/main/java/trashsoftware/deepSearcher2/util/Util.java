package trashsoftware.deepSearcher2.util;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Util {

    /**
     * Returns the readable {@code String} of <code>size</code>, representing the size of a file.
     * <p>
     * This method shows a number that at most 1,024 and a corresponding suffix
     *
     * @param size the size to be converted
     * @return the readable {@code String}
     */
    public static String sizeToReadable(long size, String bytesString) {
        if (size < Math.pow(2, 10)) return numToReadable((int) size) + " " + bytesString;
        else if (size < Math.pow(2, 20)) return numToReadable((double) size / 1024 + 1) + " KB";
        else if (size < Math.pow(2, 30)) return numToReadable((double) size / 1048576 + 1) + " MB";
        else return numToReadable((double) size / 1073741824 + 1) + "GB";
    }

    public static String separateInteger(long number) {
        return String.format("%,d", number);
    }

    /**
     * Returns the extension (suffix name) of a file's name.
     *
     * @param fileName the file's name
     * @return the extension (suffix name) of a file's name, in lower case.
     */
    public static String getFileExtension(String fileName) {
        int extIndex = fileName.lastIndexOf(".");
        return extIndex == -1 ? "" : fileName.substring(extIndex + 1).toLowerCase();
    }

    @SafeVarargs
    public static <T> Set<T> mergeSets(Set<T>... sets) {
        Set<T> res = new HashSet<>();
        for (Set<T> set : sets) res.addAll(set);
        return res;
    }

    private static String numToReadable(double num) {
        return num == (int) num ? String.format("%,d", (int) num) : String.format("%,.2f", num);
    }

    /**
     * Returns a non-duplicate version of a desired file name.
     * <p>
     * For example, if "name.txt" is already exists, returns "name(1).txt"
     *
     * @param origName the desired file name, may conflict with an existing file
     * @return the file name that does not conflict with existing files
     * @throws IOException if not able to create the temp file
     */
    public static String fileNameNoConflict(String origName) throws IOException {
        int count = 0;
        int dotIndex = origName.lastIndexOf('.');
        String front, back;
        if (dotIndex == -1) {
            front = origName;
            back = "";
        } else {
            front = origName.substring(0, dotIndex);
            back = origName.substring(dotIndex);
        }
        String aftName = origName;
        File file;
        while ((file = new File(aftName)).exists()) {
            aftName = String.format("%s(%d)%s", front, ++count, back);
        }
        File parent = file.getParentFile();
        if (!parent.exists()) {
            if (!parent.mkdirs()) {
                throw new IOException("Cannot create parent directory " + parent);
            }
        }
        if (!file.createNewFile()) {
            System.err.println("Cannot create file " + file);
        }
        return aftName;
    }

    public static void desktopOpenFile(File file) {
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            e.printStackTrace();
            EventLogger.log(e);
        }
    }
}
