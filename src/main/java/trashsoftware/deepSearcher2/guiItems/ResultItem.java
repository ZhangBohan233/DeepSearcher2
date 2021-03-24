package trashsoftware.deepSearcher2.guiItems;

import javafx.fxml.FXML;
import trashsoftware.deepSearcher2.controllers.Client;
import trashsoftware.deepSearcher2.searcher.ContentResult;
import trashsoftware.deepSearcher2.searcher.archiveSearchers.FileInArchive;
import trashsoftware.deepSearcher2.util.Util;

import java.io.File;
import java.util.Map;

public abstract class ResultItem {

    protected final FileSizeItem fileSizeItem;
    protected final boolean[] matchModes;
    protected final Map<String, String> customFormats;
    private ContentResult contentRes;

    private ResultItem(boolean matchName,
                       boolean matchContent,
                       FileSizeItem fileSizeItem,
                       ContentResult contentRes,
                       Map<String, String> customFormats) {
        this.matchModes = new boolean[]{matchName, matchContent};
        this.fileSizeItem = fileSizeItem;

        this.contentRes = contentRes;
        this.customFormats = customFormats;
    }

    public static RegularItem createNameMatch(File file,
                                              Map<String, String> customFormats) {
        return new RegularItem(
                file, true, false, null, customFormats);
    }

    public static RegularItem createContentMatch(File file,
                                                 ContentResult contentRes,
                                                 Map<String, String> customFormats) {
        return new RegularItem(
                file, false, true, contentRes, customFormats);
    }

    public static CompressedItem createNameMatchInArchive(
            FileInArchive fileInArchive,
            Map<String, String> customFormats) {
        return new CompressedItem(
                fileInArchive, true, false, null, customFormats);
    }

    public static CompressedItem createContentMatchInArchive(
            FileInArchive fileInArchive,
            ContentResult contentRes,
            Map<String, String> customFormats) {
        return new CompressedItem(
                fileInArchive, false, true, contentRes, customFormats);
    }

    public void setContentRes(ContentResult contentRes) {
        this.contentRes = contentRes;
        this.matchModes[1] = true;
    }

    @FXML
    public abstract String getSimpleName();

    @FXML
    public abstract String getFullPath();

    public abstract boolean isDir();

    /**
     * Opens the file represented by this in desktop.
     */
    public abstract void open();

    /**
     * Opens the parent directory represented by this in desktop;
     */
    public abstract void openParentDir();

    @FXML
    public String getMode() {
        if (matchModes[0]) {
            if (matchModes[1]) {
                return Client.getBundle().getString("matchedName") + ", " +
                        Client.getBundle().getString("matchedContent");
            } else {
                return Client.getBundle().getString("matchedName");
            }
        } else if (matchModes[1]) {
            return Client.getBundle().getString("matchedContent");
        } else {
            throw new RuntimeException("Result that does not match any could not be here, must be a bug");
        }
    }

    @FXML
    public FileSizeItem getSize() {
        return fileSizeItem;  // nullable
    }

    @FXML
    public abstract String getType();

//    public abstract File getFile();

    public String showInfo() {
        if (contentRes != null) {
            return contentRes.getAsString(Client.getBundle());
        } else {
            return null;
        }
    }

    public static class RegularItem extends ResultItem {
        private final File file;

        private RegularItem(File file,
                            boolean matchName,
                            boolean matchContent,
                            ContentResult contentRes,
                            Map<String, String> customFormats) {
            super(matchName,
                    matchContent,
                    file.isDirectory() ? null : new FileSizeItem(file.length()),
                    contentRes,
                    customFormats);

            this.file = file;
        }

        @Override
        public String getSimpleName() {
            return file.getName();
        }

        @Override
        public String getFullPath() {
            return file.getAbsolutePath();
        }

        @Override
        public boolean isDir() {
            return file.isDirectory();
        }

        @Override
        public void open() {
            Util.desktopOpenFile(file);
        }

        @Override
        public void openParentDir() {
            Util.desktopOpenFile(file.getParentFile());
        }

        @Override
        public String getType() {
            if (file.isDirectory()) return Client.getBundle().getString("folder");
            String name = file.getName();
            String ext = Util.getFileExtension(name);
            if (ext.equals("")) return Client.getBundle().getString("file");
            else if (Client.getFileTypeBundle().containsKey(ext))
                return Client.getFileTypeBundle().getString(ext);
            else if (customFormats.containsKey(ext)) return customFormats.get(ext);
            else return ext.toUpperCase() + " " + Client.getBundle().getString("file");
        }
    }

    public static class CompressedItem extends ResultItem {
        private final FileInArchive fileInArchive;

        private CompressedItem(FileInArchive fileInArchive,
                               boolean matchName, boolean matchContent, ContentResult contentRes, Map<String, String> customFormats) {
            super(matchName,
                    matchContent,
                    fileInArchive.isDirectory() ? null : new FileSizeItem(fileInArchive.origSize()),
                    contentRes,
                    customFormats);

            this.fileInArchive = fileInArchive;
        }

        @Override
        public String getSimpleName() {
            return fileInArchive.getFakeFile().getName();
        }

        @Override
        public String getFullPath() {
            return fileInArchive.getFakeFile().getAbsolutePath();
        }

        @Override
        public String getType() {
            return Client.getBundle().getString("cmpFileContent");
        }

        @Override
        public boolean isDir() {
            return fileInArchive.isDirectory();
        }

        @Override
        public void open() {
            Util.desktopOpenFile(fileInArchive.getOutermostArchiveFile());
        }

        @Override
        public void openParentDir() {
            Util.desktopOpenFile(fileInArchive.getOutermostArchiveFile().getParentFile());
        }
    }
}
