package trashsoftware.deepSearcher2.searcher.archiveSearchers;

import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import trashsoftware.deepSearcher2.searcher.Searcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class TarSearcher extends ApacheEntrySearcher {
    public TarSearcher(File archiveFile, File outermostArchiveFile, String internalPath, Searcher searcher) {
        super(archiveFile, outermostArchiveFile, internalPath, searcher);
    }

    @Override
    protected ArchiveInputStream createStream() throws IOException {
        return new TarArchiveInputStream(new FileInputStream(archiveFile));
    }
}
