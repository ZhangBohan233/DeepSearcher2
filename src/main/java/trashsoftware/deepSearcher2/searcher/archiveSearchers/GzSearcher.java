package trashsoftware.deepSearcher2.searcher.archiveSearchers;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import trashsoftware.deepSearcher2.searcher.Searcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class GzSearcher extends SoloCmpSearcher {

    /**
     * Constructor.
     *
     * @param archiveFile          the archive file on disk, must be readable
     * @param outermostArchiveFile the outermost archive file, which is used to be opened by user
     * @param internalPath         the intermediate path between the outermost archive and the current archive
     * @param searcher             the searcher instance
     */
    public GzSearcher(File archiveFile, File outermostArchiveFile, String internalPath, Searcher searcher) {
        super(archiveFile, outermostArchiveFile, internalPath, searcher);
    }

    @Override
    protected StreamAndInfo createStream() throws IOException {
        GzipCompressorInputStream gis = new GzipCompressorInputStream(new FileInputStream(archiveFile));
        return new StreamAndInfo(gis, gis.getMetaData().getFilename(), -1);
    }
}
