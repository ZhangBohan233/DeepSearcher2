package trashsoftware.deepSearcher2.searcher;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SearcherTest {
    @Test
    void testPrefSetAddDirs() throws Exception {
        SearchingOptions.PrefSetBuilder psb = new SearchingOptions.PrefSetBuilder();
        psb.setSearchDirs(List.of(
                new File("E:\\GitHub"),
                //new File("E:\\"),
                new File("E:\\GitHub\\DeepSearcher2"),
                new File("E:\\Programs")
        ));
        psb.setTargets(new ArrayList<>(List.of("x")));
        psb.searchFileName(true);
        SearchingOptions ps = psb.build();
        for (File f : ps.getSearchDirs()) {
            System.out.println(f + " " + f.getAbsolutePath());
        }
    }

    @Test
    void testEscapes() {
        System.out.println("aaaxxx\ngggfff".indexOf("\n"));
    }
}
