package trashsoftware.deepSearcher2.searcher.archiveSearchers;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import trashsoftware.deepSearcher2.searcher.Searcher;
import trashsoftware.deepSearcher2.util.Configs;
import trashsoftware.deepSearcher2.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public abstract class ApacheEntrySearcher extends EntryArchiveSearcher {

    public ApacheEntrySearcher(File archiveFile, File outermostArchiveFile, String internalPath, Searcher searcher) {
        super(archiveFile, outermostArchiveFile, internalPath, searcher);
    }

    @Override
    public final void search() {
        try (ArchiveInputStream ais = createStream()) {
            ArchiveEntry entry;
            while (searcher.isSearching() && (entry = ais.getNextEntry()) != null) {
                String entryName = entry.getName();
                FileInArchive fileInArchive = createFileInArchive(entryName, entry.getSize());
                if (entry.isDirectory()) {
                    // Check dir is selected
                    if (searcher.getOptions().isDirName()) {
                        searcher.matchName(fileInArchive);
                    }
                } else {
                    // Check if this format is excluded
                    if (searcher.getOptions().getExcludedFormats().contains(
                            Util.getFileExtension(entryName))) continue;
                    // check file name is selected
                    if (searcher.getOptions().isFileName()) {
                        searcher.matchName(fileInArchive);
                    }
                    // check file content is selected
                    String extension = Util.getFileExtension(entryName);
                    boolean childIsArchive = searcher.getOptions().getCmpFileFormats().contains(extension);
                    if (searcher.getOptions().getExtensions() != null || childIsArchive) {
                        String cachedName = cacheNameNonConflict(extension);
                        if (uncompressSingle(cachedName, ais)) {
                            if (searcher.getOptions().getExtensions() != null) {
                                searcher.matchFileContentUncompressed(new File(cachedName), fileInArchive);
                            }
                            if (childIsArchive) {
                                searchChildArchive(cachedName, entryName);
                            }
                        }
                        Configs.deleteFileByName(cachedName);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the archive input stream corresponding to the class which implements a type of archive.
     *
     * @return the newly created stream
     * @throws IOException if IOError occurs
     */
    protected abstract ArchiveInputStream createStream() throws IOException;

    private boolean uncompressSingle(String cachedName, ArchiveInputStream zip) {
        try (OutputStream uncOs = new FileOutputStream(cachedName)) {
            if (uncBuffer == null) uncBuffer = new byte[BUFFER_SIZE];
            int read;
            while (searcher.isSearching() && (read = zip.read(uncBuffer)) >= 0) {
                uncOs.write(uncBuffer, 0, read);
            }
            uncOs.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
