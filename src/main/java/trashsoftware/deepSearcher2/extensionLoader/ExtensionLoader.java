package trashsoftware.deepSearcher2.extensionLoader;

import dsApi.api.FileFormatReader;
import trashsoftware.deepSearcher2.util.Configs;
import trashsoftware.deepSearcher2.util.EventLogger;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ExtensionLoader {

    public static final String EXT_JAR_DIR = Configs.USER_DATA_DIR + File.separator + "extensions";
    private static ExtensionLoader jarLoader;
    private final List<ExtensionJar> extensionJarList;

    private ExtensionLoader() {
        extensionJarList = listExternalJars();
    }

    /**
     * Starts the loader.
     * <p>
     * This static method reads jars from
     */
    public static void startLoader() {
        jarLoader = new ExtensionLoader();
    }

    public static void stopLoader() {
        if (jarLoader != null) jarLoader.close();
    }

    public static ExtensionLoader getJarLoader() {
        return jarLoader;
    }

    /**
     * Returns {@code true} iff this jar file can be loaded without error.
     *
     * @param file the jar file
     * @return {@code true} iff this jar file can be loaded without error
     */
    public static boolean testJar(File file) {
        try {
            URL jarUrl = file.toURI().toURL();
            JarFile jarFile = new JarFile(file);
            List<Class<?>> classList = findClassesInJar(jarFile, jarUrl);
            jarFile.close();
            return !classList.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            EventLogger.log(e);
        }
        return false;
    }

    private static List<ExtensionJar> listExternalJars() {
        List<ExtensionJar> list = new ArrayList<>();
        File dir = new File(EXT_JAR_DIR);

        if (dir.exists()) {
            File[] sub = dir.listFiles(new JarFilter());
            if (sub != null) {
                for (File file : sub) {
                    try {
                        URL jarUrl = file.toURI().toURL();
                        JarFile jarFile = new JarFile(file);
                        List<Class<?>> classesInJar = findClassesInJar(jarFile, jarUrl);
                        jarFile.close();
                        if (!classesInJar.isEmpty())
                            list.add(new ExtensionJar(file.getName(), classesInJar));
                    } catch (Exception e) {
                        e.printStackTrace();
                        EventLogger.log(e);
                    }
                }
            }
        } else {
            try {
                if (!dir.createNewFile()) {
                    EventLogger.log("Failed to create " + EXT_JAR_DIR);
                }
            } catch (IOException e) {
                e.printStackTrace();
                EventLogger.log(e);
            }
        }
        return list;
    }

    /**
     * Creates and returns a list containing instances of all subclass of {@link FileFormatReader} in a given
     * {@link ExtensionJar} instance which represents a jar file.
     *
     * @param extensionJar the jar file representation
     * @return a list containing instances of all subclass of {@link FileFormatReader} in a given {@link ExtensionJar}
     */
    public static List<FileFormatReader> createFormatReaderInstances(ExtensionJar extensionJar) {
        List<FileFormatReader> list = new ArrayList<>();
        for (Class<?> clazz : extensionJar.getClassList()) {
            if (!isAbstract(clazz) && subclassOf(clazz, FileFormatReader.class)) {
                try {
                    FileFormatReader ffr = (FileFormatReader) clazz.getDeclaredConstructor().newInstance();
                    list.add(ffr);
                } catch (Exception e) {
                    System.err.println(clazz);
                    e.printStackTrace();
                    EventLogger.log(e);
                }
            }
        }
        return list;
    }

    private static List<Class<?>> findClassesInJar(JarFile jarFile,
                                                   URL jarUrl) {
        List<Class<?>> classList = new ArrayList<>();
        Enumeration<JarEntry> enumeration = jarFile.entries();
        while (enumeration.hasMoreElements()) {
            JarEntry entry = enumeration.nextElement();
            String name = entry.getName();
            if (!entry.isDirectory() && name.endsWith(".class")) {
                if (name.endsWith("module-info.class")) continue;

                // removes ".class"
                String className = name.substring(0, name.length() - 6).replace('/', '.');
                Class<?> loadedClass = loadClass(className, jarUrl);
                if (loadedClass != null) {
                    classList.add(loadedClass);
                } else {
                    System.out.println("Failed to load class '" + className + "'");
                }
            }
        }
        return classList;
    }

    private static Class<?> loadClass(String className, URL jarUrl) {
        ClassLoaderWrapper wrapper = new ClassLoaderWrapper(jarUrl, className);
        return wrapper.load();
    }

    /**
     * Returns whether {@code childClass} is or is a subclass of {@code superClass}.
     *
     * @param childClass the probable child class
     * @param superClass the superclass {@code childClass} is or is a subclass of {@code superClass}
     * @return {@code true} if
     */
    public static boolean subclassOf(Class<?> childClass, Class<?> superClass) {
        if (childClass == null) return false;
        if (childClass == superClass) return true;
        return subclassOf(childClass.getSuperclass(), superClass);
    }

    /**
     * Returns whether a class is abstract.
     *
     * @param clazz the class
     * @return {@code true} if {@code clazz} is abstract
     */
    public static boolean isAbstract(Class<?> clazz) {
        return Modifier.isAbstract(clazz.getModifiers());
    }

    /**
     * @return the list of all loaded {@link ExtensionJar} instances.
     */
    public List<ExtensionJar> getExtensionJarList() {
        return extensionJarList;
    }

    public void close() {
        jarLoader = null;
    }

    /**
     * Creates and returns a list containing instances of all subclass of {@link FileFormatReader} that are enabled.
     *
     * @return a list containing instances of all subclass of {@link FileFormatReader} that are enabled
     */
    public List<FileFormatReader> listEnabledExternalReaders() {
        Set<String> enabled = Configs.getConfigs().getEnabledJars();
        List<FileFormatReader> result = new ArrayList<>();
        for (ExtensionJar ej : extensionJarList) {
            if (enabled.contains(ej.getJarName())) {
                result.addAll(createFormatReaderInstances(ej));
            }
        }
        return result;
    }

    private static class JarFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith("jar");
        }
    }

    /**
     * A wrapper of a url class loader.
     * <p>
     * Because the java try-catch statement cannot catch exceptions thrown by {@link URLClassLoader} in the
     * main thread, this wrapper creates a new thread to contain the exception.
     */
    private static class ClassLoaderWrapper {
        private final URLClassLoader classLoader;
        private final String className;
        private Class<?> loadedClass;

        ClassLoaderWrapper(URL jarUrl, String className) {
            this.className = className;
            this.classLoader = new URLClassLoader(new URL[]{jarUrl});
        }

        Class<?> load() {
            Thread loaderThread = new Thread(() -> {
                try {
                    loadedClass = classLoader.loadClass(className);
                } catch (Exception e) {
                    // This catch actually does not catch loader exceptions because the exception is
                    // thrown to the classloader of this thread.
                    // The real mechanism is, the loader thread itself isolates the exception from the main thread,
                    // to keep the whole program safe.
                }
            });
            loaderThread.start();
            try {
                loaderThread.join();  // block the main thread until the loader thread finishes or fails
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                classLoader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return loadedClass;
        }
    }
}
