package trashsoftware.deepSearcher2.searcher.archiveSearchers;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import trashsoftware.deepSearcher2.searcher.Searcher;
import trashsoftware.deepSearcher2.util.Configs;
import trashsoftware.deepSearcher2.util.Util;

import java.io.*;

public class SevenZSearcher extends EntryArchiveSearcher {

    public SevenZSearcher(File archiveFile, File outermostArchiveFile, String internalPath, Searcher searcher) {
        super(archiveFile, outermostArchiveFile, internalPath, searcher);
    }

    @Override
    public void search() {
        try (SevenZFile sevenZFile = new SevenZFile(archiveFile)) {
            SevenZArchiveEntry entry;
            while (searcher.isSearching() && (entry = sevenZFile.getNextEntry()) != null) {
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
                        if (uncompressSingle(cachedName, sevenZFile)) {
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

    private boolean uncompressSingle(String cachedName, SevenZFile sevenZFile) {
        try (OutputStream uncOs = new FileOutputStream(cachedName)) {
            if (uncBuffer == null) uncBuffer = new byte[BUFFER_SIZE];
            int read;
            while (searcher.isSearching() && (read = sevenZFile.read(uncBuffer)) >= 0) {
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
