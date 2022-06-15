package trashsoftware.deepSearcher2.css;

import java.net.URL;
import java.util.Map;
import java.util.Objects;

/**
 * A class that manages the themes in the resource directory.
 */
public class ThemeManager {

    public static final Map<String, URL> THEME_MAP = Map.of(
            "default", Objects.requireNonNull(ThemeManager.class.getResource("defaultTheme.css")),
            "dark", Objects.requireNonNull(ThemeManager.class.getResource("darkTheme.css"))
    );

    /**
     * Returns the stylesheet URL of a given theme name.
     *
     * @param themeName the theme name
     * @return the stylesheet URL of the theme named {@code themeName}
     */
    public static URL getThemeStylesheet(String themeName) {
        return THEME_MAP.get(themeName);
    }
}
