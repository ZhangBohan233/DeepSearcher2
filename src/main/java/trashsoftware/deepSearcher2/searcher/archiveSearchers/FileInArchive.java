package trashsoftware.deepSearcher2.searcher.archiveSearchers;

import java.io.File;

public class FileInArchive {
    private final long origSize;
    private final File fakeFile;
    private final File archiveFile;

    public FileInArchive(File fakeFile, File archiveFile, long origSize) {
        this.fakeFile = fakeFile;
        this.archiveFile = archiveFile;
        this.origSize = origSize;
    }

    public File getFakeFile() {
        return fakeFile;
    }

    public File getArchiveFile() {
        return archiveFile;
    }

    public boolean isDirectory() {
        return fakeFile.isDirectory();
    }

    public long origSize() {
        return origSize;
    }
}
