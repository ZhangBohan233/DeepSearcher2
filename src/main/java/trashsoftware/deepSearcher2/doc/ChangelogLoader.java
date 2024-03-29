package trashsoftware.deepSearcher2.doc;

import trashsoftware.deepSearcher2.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * This class loads the changelog text from the resources.
 */
public class ChangelogLoader {

    /**
     * @return the changelog text stored in resources directory.
     */
    public static String loadChangelog() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(
                        ChangelogLoader.class.getResourceAsStream("changelog.txt")),
                StandardCharsets.UTF_8))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line).append('\n');
            }
            return builder.toString();
        } catch (IOException | NullPointerException e) {
            Log.severe(e);
        }
        return "";
    }
}
