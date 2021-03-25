package trashsoftware.deepSearcher2.extensionLoader;

import java.io.File;
import java.util.List;

/**
 * A representation of a .jar file under userData/extensions/ directory.
 */
public class ExtensionJar {
    private final File file;
    private final List<Class<?>> classList;

    ExtensionJar(File file, List<Class<?>> classList) {
        this.file = file;
        this.classList = classList;
    }

    public File getFile() {
        return file;
    }

    public List<Class<?>> getClassList() {
        return classList;
    }
}
