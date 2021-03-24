package trashsoftware.deepSearcher2.searcher.archiveSearchers;

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;
import trashsoftware.deepSearcher2.searcher.Searcher;
import trashsoftware.deepSearcher2.util.Configs;
import trashsoftware.deepSearcher2.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class RarSearcher extends EntryArchiveSearcher {

    public RarSearcher(File archiveFile, File outermostArchiveFile, String internalPath, Searcher searcher) {
        super(archiveFile, outermostArchiveFile, internalPath, searcher);
    }

    @Override
    public void search() {
        try (Archive rar = new Archive(archiveFile)) {
            FileHeader fileHeader;
            while (searcher.isSearching() && (fileHeader = rar.nextFileHeader()) != null) {
                String entryName = fileHeader.getFileName();
                FileInArchive fileInArchive =
                        new FileInArchive(
                                new File(archiveFile.getAbsolutePath() + File.separator + entryName),
                                outermostArchiveFile,
                                fileHeader.getUnpSize());
                if (fileHeader.isDirectory()) {
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
                        if (uncompressSingle(cachedName, rar, fileHeader)) {
                            if (searcher.getOptions().getExtensions() != null) {
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

    private boolean uncompressSingle(String cachedName, Archive archive, FileHeader fileHeader) {
        try (OutputStream uncOs = new FileOutputStream(cachedName)) {
            archive.extractFile(fileHeader, uncOs);
            uncOs.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
