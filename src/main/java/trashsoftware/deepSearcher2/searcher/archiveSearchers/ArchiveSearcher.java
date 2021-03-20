package trashsoftware.deepSearcher2.searcher.archiveSearchers;

import trashsoftware.deepSearcher2.searcher.Searcher;

public abstract class ArchiveSearcher {

    protected final Searcher searcher;

    public ArchiveSearcher(Searcher searcher) {
        this.searcher = searcher;
    }

    public abstract void search();
}
