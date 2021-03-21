package trashsoftware.deepSearcher2.searcher.archiveSearchers;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import trashsoftware.deepSearcher2.searcher.Searcher;
import trashsoftware.deepSearcher2.util.Configs;
import trashsoftware.deepSearcher2.util.Util;

import java.io.*;

public class TarSearcher extends EntryArchiveSearcher {
    public TarSearcher(File archiveFile, File outermostArchiveFile, String internalPath, Searcher searcher) {
        super(archiveFile, outermostArchiveFile, internalPath, searcher);
    }

    @Override
    public void search() {
        try (TarArchiveInputStream tar = new TarArchiveInputStream(new FileInputStream(archiveFile))) {
            TarArchiveEntry entry;
            while (searcher.isSearching() && (entry = tar.getNextTarEntry()) != null) {
                String entryName = entry.getName();
                FileInArchive fileInArchive = createFileInArchive(entryName, entry.getRealSize());
                if (entry.isDirectory()) {
                    // Check dir is selected
                    if (searcher.getPrefSet().isDirName()) {
                        searcher.matchName(fileInArchive);
                    }
                } else {
                    // Check if this format is excluded
                    if (searcher.getPrefSet().getExcludedFormats().contains(
                            Util.getFileExtension(entryName))) continue;
                    // check file name is selected
                    if (searcher.getPrefSet().isFileName()) {
                        searcher.matchName(fileInArchive);
                    }
                    // check file content is selected
                    String extension = Util.getFileExtension(entryName).toLowerCase();
                    boolean childIsArchive = searcher.getPrefSet().getCmpFileFormats().contains(extension);
                    if (searcher.getPrefSet().getExtensions() != null || childIsArchive) {
                        String cachedName = cacheNameNonConflict(extension);
                        if (extractSingle(cachedName, tar)) {
                            if (searcher.getPrefSet().getExtensions() != null) {
                                searcher.matchFileContent(new File(cachedName), fileInArchive);
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

    private boolean extractSingle(String cachedName, TarArchiveInputStream tar) {
        try (OutputStream uncOs = new FileOutputStream(cachedName)) {
            if (uncBuffer == null) uncBuffer = new byte[BUFFER_SIZE];
            int read;
            while (searcher.isSearching() && (read = tar.read(uncBuffer)) >= 0) {
                uncOs.write(uncBuffer, 0, read);
            }
            uncOs.flush();
            return true;
        } catch (IOException e) {
            // the exception is contained in this method, to avoid interruption of archive entries iteration
            e.printStackTrace();
            return false;
        }
    }
}
