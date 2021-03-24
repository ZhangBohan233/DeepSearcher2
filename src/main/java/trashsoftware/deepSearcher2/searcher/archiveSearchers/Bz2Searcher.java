package trashsoftware.deepSearcher2.searcher.archiveSearchers;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2Utils;
import trashsoftware.deepSearcher2.searcher.Searcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Bz2Searcher extends SoloCmpSearcher {

    /**
     * Constructor.
     *
     * @param archiveFile          the archive file on disk, must be readable
     * @param outermostArchiveFile the outermost archive file, which is used to be opened by user
     * @param internalPath         the intermediate path between the outermost archive and the current archive
     * @param searcher             the searcher instance
     */
    public Bz2Searcher(File archiveFile, File outermostArchiveFile, String internalPath, Searcher searcher) {
        super(archiveFile, outermostArchiveFile, internalPath, searcher);
    }

    @Override
    protected StreamAndInfo createStream() throws IOException {
        BZip2CompressorInputStream bis = new BZip2CompressorInputStream(new FileInputStream(archiveFile));
        String bzName;
        if (internalPath.length() == 0) bzName = archiveFile.getName();
        else {
            String standardPath = new File(internalPath).getPath();  // in internalPath, '/' and '\' are mixed
            bzName = standardPath.substring(standardPath.lastIndexOf(File.separator) + 1);
        }
        return new StreamAndInfo(bis, BZip2Utils.getUncompressedFilename(bzName), -1);
    }
}
