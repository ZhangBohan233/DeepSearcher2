package trashsoftware.deepSearcher2.searcher.archiveSearchers;

import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import trashsoftware.deepSearcher2.searcher.Searcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ZipSearcher extends ApacheEntrySearcher {
    public ZipSearcher(File archiveFile, File outermostArchiveFile, String internalPath, Searcher searcher) {
        super(archiveFile, outermostArchiveFile, internalPath, searcher);
    }

    @Override
    protected ArchiveInputStream createStream() throws IOException {
        return new ZipArchiveInputStream(new FileInputStream(archiveFile));
    }
}
