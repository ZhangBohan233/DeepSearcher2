package trashsoftware.deepSearcher2.util;

public class Util {

    /**
     * Returns the readable {@code String} of <code>size</code>, representing the size of a file.
     * <p>
     * This method shows a number that at most 1,024 and a corresponding suffix
     *
     * @param size the size to be converted
     * @return the readable {@code String}
     */
    public static String sizeToReadable(long size) {
        if (size < Math.pow(2, 10)) return numToReadable((int) size) + " Bytes";
        else if (size < Math.pow(2, 20)) return numToReadable((double) size / 1024 + 1) + " KB";
        else if (size < Math.pow(2, 30)) return numToReadable((double) size / 1048576 + 1) + " MB";
        else return numToReadable((double) size / 1073741824 + 1) + "GB";
    }

    public static String separateInteger(long number) {
        return String.format("%,d", number);
    }

    private static String numToReadable(double num) {
        if (num >= 1048576) throw new IndexOutOfBoundsException("Number Too large");
        int decimalNum = (int) num;
        String decimal = String.valueOf(decimalNum);
        double fractionalNum = (double) Math.round((num - decimalNum) * 1000) / 1000;
        String fractional = String.valueOf(fractionalNum);
        String decimalWithComma;
        if (decimal.length() <= 3) decimalWithComma = decimal;
        else {
            int split = decimal.length() - 3;
            decimalWithComma = decimal.substring(0, split) + "," + decimal.substring(split);
        }
        if (fractionalNum == 0) return decimalWithComma;
        else return decimalWithComma + "." + fractional.substring(2);
    }
}
