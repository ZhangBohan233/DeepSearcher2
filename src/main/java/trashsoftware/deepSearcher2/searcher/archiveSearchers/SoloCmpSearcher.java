package trashsoftware.deepSearcher2.searcher.archiveSearchers;

import org.apache.commons.compress.compressors.CompressorInputStream;
import trashsoftware.deepSearcher2.searcher.Searcher;
import trashsoftware.deepSearcher2.util.Configs;
import trashsoftware.deepSearcher2.util.Util;

import java.io.*;

public abstract class SoloCmpSearcher extends ArchiveSearcher {

    protected static final int BUFFER_SIZE = 8192;
    protected byte[] uncBuffer = null;

    /**
     * Constructor.
     *
     * @param archiveFile          the archive file on disk, must be readable
     * @param outermostArchiveFile the outermost archive file, which is used to be opened by user
     * @param internalPath         the intermediate path between the outermost archive and the current archive
     * @param searcher             the searcher instance
     */
    public SoloCmpSearcher(File archiveFile, File outermostArchiveFile, String internalPath, Searcher searcher) {
        super(archiveFile, outermostArchiveFile, internalPath, searcher);
    }

    @Override
    public void search() {
        try (StreamAndInfo sai = createStream()) {
            searchProcess(sai.getContentFileName(), sai.getInputStream(), sai.getOrigSize());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract StreamAndInfo createStream() throws IOException;

    protected void searchProcess(String contentFileName, InputStream compressedInput, long uncompressedSize)
            throws IOException {
        FileInArchive fileInArchive = createFileInArchive(contentFileName, uncompressedSize);
        // Check if this format is excluded
        if (searcher.getOptions().getExcludedFormats().contains(
                Util.getFileExtension(contentFileName))) return;
        // check file name is selected
        if (searcher.getOptions().isFileName()) {
            searcher.matchName(fileInArchive);
        }
        // check file content is selected
        String extension = Util.getFileExtension(contentFileName).toLowerCase();
        boolean childIsArchive = searcher.getOptions().getCmpFileFormats().contains(extension);
        if (searcher.getOptions().getExtensions() != null || childIsArchive) {
            String cachedName = cacheNameNonConflict(extension);
            uncompressContent(cachedName, compressedInput);
            if (searcher.getOptions().getExtensions() != null) {
                searcher.matchFileContentUncompressed(new File(cachedName), fileInArchive);
            }
            if (childIsArchive) {
                searchChildArchive(cachedName, contentFileName);
            }

            Configs.deleteFileByName(cachedName);
        }
    }

    /**
     * Uncompress this file.
     * <p>
     * The io-exception is thrown, because this kind of compressed file contains only one file,
     * there is no need to avoid interruption like {@link ZipSearcher}.
     *
     * @param cachedName  name of temp file
     * @param inputStream the input stream
     * @throws IOException if not readable
     */
    protected void uncompressContent(String cachedName, InputStream inputStream) throws IOException {
        try (OutputStream fos = new FileOutputStream(cachedName)) {
            if (uncBuffer == null) uncBuffer = new byte[BUFFER_SIZE];
            int read;
            while (searcher.isSearching() && (read = inputStream.read(uncBuffer)) >= 0) {
                fos.write(uncBuffer, 0, read);
            }
            fos.flush();
        }
    }

    /**
     * This class is a wrapper of some compressed input stream.
     */
    public static class StreamAndInfo implements Closeable {
        private final CompressorInputStream inputStream;
        private final String contentFileName;
        private final long origSize;

        public StreamAndInfo(CompressorInputStream inputStream, String contentFileName, long origSize) {
            this.inputStream = inputStream;
            this.contentFileName = contentFileName;
            this.origSize = origSize;
        }

        public CompressorInputStream getInputStream() {
            return inputStream;
        }

        public long getOrigSize() {
            return origSize;
        }

        public String getContentFileName() {
            return contentFileName;
        }

        @Override
        public void close() throws IOException {
            inputStream.close();
        }
    }
}
