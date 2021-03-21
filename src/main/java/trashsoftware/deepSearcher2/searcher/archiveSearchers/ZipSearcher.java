package trashsoftware.deepSearcher2.searcher.archiveSearchers;

import trashsoftware.deepSearcher2.searcher.Searcher;
import trashsoftware.deepSearcher2.util.Configs;
import trashsoftware.deepSearcher2.util.Util;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class ZipSearcher extends EntryArchiveSearcher {
    public ZipSearcher(File archiveFile, File outermostArchiveFile, String internalPath, Searcher searcher) {
        super(archiveFile, outermostArchiveFile, internalPath, searcher);
    }

    @Override
    public void search() {
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(archiveFile));
             ZipFile zipFile = new ZipFile(archiveFile)) {
            ZipEntry entry;
            while (searcher.isSearching() && (entry = zipInputStream.getNextEntry()) != null) {
                String entryName = entry.getName();
                FileInArchive fileInArchive = createFileInArchive(entryName, entry.getSize());
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
                        if (uncompressSingle(cachedName, zipFile, entry)) {
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

    private boolean uncompressSingle(String uncName, ZipFile zipFile, ZipEntry zipEntry) {
        try (InputStream fileIs = zipFile.getInputStream(zipEntry);
             OutputStream uncOs = new FileOutputStream(uncName)) {
            if (uncBuffer == null) uncBuffer = new byte[BUFFER_SIZE];
            int read;
            while (searcher.isSearching() && (read = fileIs.read(uncBuffer)) >= 0) {
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
