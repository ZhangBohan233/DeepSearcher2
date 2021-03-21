package trashsoftware.deepSearcher2.searcher.archiveSearchers;

import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZUtils;
import trashsoftware.deepSearcher2.searcher.Searcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class XzSearcher extends SoloCmpSearcher {
    /**
     * Constructor.
     *
     * @param archiveFile          the archive file on disk, must be readable
     * @param outermostArchiveFile the outermost archive file, which is used to be opened by user
     * @param internalPath         the intermediate path between the outermost archive and the current archive
     * @param searcher             the searcher instance
     */
    public XzSearcher(File archiveFile, File outermostArchiveFile, String internalPath, Searcher searcher) {
        super(archiveFile, outermostArchiveFile, internalPath, searcher);
    }

    @Override
    protected StreamAndInfo createStream() throws IOException {
        XZCompressorInputStream xis = new XZCompressorInputStream(new FileInputStream(archiveFile));
        String xzName;
        if (internalPath.length() == 0) xzName = archiveFile.getName();
        else {
            String standardPath = new File(internalPath).getPath();  // in internalPath, '/' and '\' are mixed
            xzName = standardPath.substring(standardPath.lastIndexOf(File.separator) + 1);
        }
        return new StreamAndInfo(xis, XZUtils.getUncompressedFilename(xzName), -1);
    }
}
