package trashsoftware.deepSearcher2.searcher.archiveSearchers;

import trashsoftware.deepSearcher2.searcher.Searcher;

import java.io.File;
import java.util.Set;

public abstract class ArchiveSearcher {

    public static final Set<String> COMPRESSED_FORMATS = Set.of(
            "zip", "rar", "7z"
    );

    protected final File archiveFile;
    protected final File outermostArchiveFile;
    protected final String internalPath;
    protected final Searcher searcher;

    /**
     * Constructor.
     *
     * @param archiveFile          the archive file on disk, must be readable
     * @param outermostArchiveFile the outermost archive file, which is used to be opened by user
     * @param internalPath         the intermediate path between the outermost archive and the current archive
     * @param searcher             the searcher instance
     */
    public ArchiveSearcher(File archiveFile, File outermostArchiveFile, String internalPath, Searcher searcher) {
        this.archiveFile = archiveFile;
        this.outermostArchiveFile = outermostArchiveFile;
        this.internalPath = internalPath;
        this.searcher = searcher;
    }

    public abstract void search();
}
