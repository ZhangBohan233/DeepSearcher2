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

    /**
     * @return the simple name of jar file
     */
    public String getJarName() {
        return jarName;
    }

    /**
     * @return the jar file name with path
     */
    public String getPathName() {
        return ExtensionLoader.EXT_JAR_DIR + File.separator + jarName;
    }

    /**
     * @return a list containing all classes loaded from this jar file
     */
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
