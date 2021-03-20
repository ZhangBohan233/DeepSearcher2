package trashsoftware.deepSearcher2.searcher.archiveSearchers;

import trashsoftware.deepSearcher2.searcher.Searcher;
import trashsoftware.deepSearcher2.util.Cache;
import trashsoftware.deepSearcher2.util.Configs;
import trashsoftware.deepSearcher2.util.EventLogger;
import trashsoftware.deepSearcher2.util.Util;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class ZipSearcher extends ArchiveSearcher {

    private static final int BUFFER_SIZE = 8192;
    private final File file;
    private byte[] uncBuffer = null;

    public ZipSearcher(Searcher searcher, File file) {
        super(searcher);

        this.file = file;
    }

    @Override
    public void search() {
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
             ZipFile zipFile = new ZipFile(file)) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String entryName = zipEntry.getName();
//                System.out.println(entryName);
                FileInArchive fileInArchive =
                        new FileInArchive(
                                new File(file.getAbsolutePath() + File.separator + entryName),
                                file,
                                zipEntry.getSize());
                if (zipEntry.isDirectory()) {
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
                    if (searcher.getPrefSet().getExtensions() != null) {
                        String cachedName = cacheNameNonConflict(Util.getFileExtension(entryName));
                        if (uncompressSingle(cachedName, zipFile, zipEntry)) {
                            searcher.matchFileContent(new File(cachedName), fileInArchive);
                        }
                        Configs.deleteFileByName(cachedName);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized String cacheNameNonConflict(String extension) throws IOException {
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

    private boolean uncompressSingle(String uncName, ZipFile zipFile, ZipEntry zipEntry) {
        InputStream fileIs = null;
        OutputStream uncOs = null;
        try {
            fileIs = zipFile.getInputStream(zipEntry);
//            System.out.println(new File(uncName).createNewFile());
            uncOs = new FileOutputStream(uncName);
            if (uncBuffer == null) uncBuffer = new byte[BUFFER_SIZE];

            int read;
            while ((read = fileIs.read(uncBuffer)) > 0) {
                uncOs.write(uncBuffer, 0, read);
            }

            uncOs.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (fileIs != null) {
                try {
                    fileIs.close();
                } catch (IOException e) {
                    EventLogger.log(e);
                }
            }
            if (uncOs != null) {
                try {
                    uncOs.close();
                } catch (IOException e) {
                    EventLogger.log(e);
                }
            }
        }
    }
}
