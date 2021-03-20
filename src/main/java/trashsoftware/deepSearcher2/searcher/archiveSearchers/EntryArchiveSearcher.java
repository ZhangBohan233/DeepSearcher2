package trashsoftware.deepSearcher2.searcher.archiveSearchers;

import trashsoftware.deepSearcher2.searcher.Searcher;
import trashsoftware.deepSearcher2.util.Cache;

import java.io.File;
import java.io.IOException;

public abstract class EntryArchiveSearcher extends ArchiveSearcher {

    protected static final int BUFFER_SIZE = 8192;
    protected byte[] uncBuffer = null;

    public EntryArchiveSearcher(File archiveFile,
                                File outermostArchiveFile,
                                String internalPath,
                                Searcher searcher) {
        super(archiveFile, outermostArchiveFile, internalPath, searcher);
    }

    /**
     * A commonly used utility method.
     * <p>
     * This method also creates an empty file of the returned name.
     *
     * @param extension the format extension of file, without dot
     * @return name of a new cache file that does not conflict with any existing file
     * @throws IOException if not able to create the temp file
     */
    protected synchronized String cacheNameNonConflict(String extension) throws IOException {
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
