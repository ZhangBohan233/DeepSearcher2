package trashsoftware.deepSearcher2.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventLogger {

    private static final String LOG_DIR = "logs";
    private static final String LOG_BASE_NAME = LOG_DIR + File.separator + "error-";
    private static final String DATE_FMT = "yyyy-MM-dd HH-mm-ss";

    /**
     * Logs complete error message and stack trace to a new log file.
     *
     * @param throwable error
     */
    public static void log(Throwable throwable) {
        createLogDirIfNone();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FMT);
            String realName = LOG_BASE_NAME + sdf.format(new Date()) + ".log";
            FileWriter fileWriter = new FileWriter(realName);
            PrintWriter pw = new PrintWriter(fileWriter);
            throwable.printStackTrace(pw);

            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Logs text error message to a new log file.
     *
     * @param message text message
     */
    public static void log(String message) {
        createLogDirIfNone();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FMT);
            String realName = LOG_BASE_NAME + sdf.format(new Date()) + ".log";
            FileWriter fileWriter = new FileWriter(realName);
            fileWriter.write(message);

            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createLogDirIfNone() {
        File dir = new File(LOG_DIR);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                System.out.println("Failed to create log directory.");
            }
        }
    }
}
