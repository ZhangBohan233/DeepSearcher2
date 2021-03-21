package trashsoftware.deepSearcher2.searcher.archiveSearchers;

import trashsoftware.deepSearcher2.searcher.Searcher;
import trashsoftware.deepSearcher2.util.Cache;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public abstract class ArchiveSearcher {

    public static final Set<String> COMPRESSED_FORMATS = Set.of(
            "zip", "rar", "7z", "tar", "gz", "xz", "bz2"
    );

    protected final File archiveFile;
    protected final File outermostArchiveFile;
    protected final String internalPath;
    protected final Searcher searcher;

    /**
     * Constructor.
     *
     * @param archiveFile          the archive file on disk, must be readable
     * @param outermostArchiveFile the outermost archive file, which is used to be opened by user
     * @param internalPath         the intermediate path between the outermost archive and the current archive
     * @param searcher             the searcher instance
     */
    public ArchiveSearcher(File archiveFile, File outermostArchiveFile, String internalPath, Searcher searcher) {
        this.archiveFile = archiveFile;
        this.outermostArchiveFile = outermostArchiveFile;
        this.internalPath = internalPath;
        this.searcher = searcher;
    }

    public abstract void search();

    /**
     * A commonly used utility method.
     * <p>
     * This method also creates an empty file of the returned name.
     *
     * @param extension the format extension of file, without dot
     * @return name of a new cache file that does not conflict with any existing file
     * @throws IOException if not able to create the temp file
     */
    protected static synchronized String cacheNameNonConflict(String extension) throws IOException {
        String front = Cache.CACHE_DIR + File.separator + "temp";
        String cacheName = front + "." + extension;
        File file;
        int count = 0;
        while ((file = new File(cacheName)).exists()) {
            cacheName = String.format("%s(%d).%s", front, ++count, extension);
        }
        if (!file.createNewFile()) {
            System.err.println("Cannot create file " + file);
        }
        return cacheName;
    }

    protected FileInArchive createFileInArchive(String entryName, long entrySize) {
        return new FileInArchive(
                new File(outermostArchiveFile.getAbsolutePath() +
                        File.separator + internalPath +
                        File.separator + entryName),
                outermostArchiveFile,
                entrySize);
    }

    protected void searchChildArchive(String cachedName, String entryName) {
        searcher.searchArchiveFile(
                new File(cachedName),
                outermostArchiveFile,
                internalPath + File.separator + entryName);
    }
}
