package trashsoftware.deepSearcher2.searcher.archiveSearchers;

import trashsoftware.deepSearcher2.searcher.Searcher;

import java.io.File;

public class FileInArchive {
    private final long origSize;
    private final File fakeFile;
    private final File outermostArchiveFile;

    /**
     * @param origSize the size of the original file, uncompressed.
     * @see ArchiveSearcher#ArchiveSearcher(File, File, String, Searcher)
     */
    public FileInArchive(File fakeFile, File outermostArchiveFile, long origSize) {
        this.fakeFile = fakeFile;
        this.outermostArchiveFile = outermostArchiveFile;
        this.origSize = origSize;
    }

    public File getFakeFile() {
        return fakeFile;
    }

    /**
     * Returns the outermost archive file, which is 'permanently' existing on disk.
     *
     * @return the outermost archive file, which is 'permanently' existing on disk
     */
    public File getOutermostArchiveFile() {
        return outermostArchiveFile;
    }

    public boolean isDirectory() {
        return fakeFile.isDirectory();
    }

    public long origSize() {
        return origSize;
    }
}
