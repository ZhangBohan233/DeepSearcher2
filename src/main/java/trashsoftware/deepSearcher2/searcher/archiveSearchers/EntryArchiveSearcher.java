package trashsoftware.deepSearcher2.searcher.archiveSearchers;

import trashsoftware.deepSearcher2.searcher.Searcher;

import java.io.File;

public abstract class EntryArchiveSearcher extends ArchiveSearcher {

    protected static final int BUFFER_SIZE = 8192;
    protected byte[] uncBuffer = null;

    public EntryArchiveSearcher(File archiveFile,
                                File outermostArchiveFile,
                                String internalPath,
                                Searcher searcher) {
        super(archiveFile, outermostArchiveFile, internalPath, searcher);
    }
}
