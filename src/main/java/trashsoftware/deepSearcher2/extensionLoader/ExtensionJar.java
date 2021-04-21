package trashsoftware.deepSearcher2.extensionLoader;

import java.io.File;
import java.util.List;

/**
 * A representation of a .jar file under userData/extensions/ directory.
 */
public class ExtensionJar {
    private final String jarName;
    private final List<Class<?>> classList;

    ExtensionJar(String jarName, List<Class<?>> classList) {
        this.jarName = jarName;
        this.classList = classList;
    }

    public String getJarName() {
        return jarName;
    }

    public String getPathName() {
        return ExtensionLoader.EXT_JAR_DIR + File.separator + jarName;
    }

    public List<Class<?>> getClassList() {
        return classList;
    }

    @Override
    public String toString() {
        return "ExtensionJar{" +
                "jarName='" + jarName + '\'' +
                ", classList=" + classList +
                '}';
    }
}
