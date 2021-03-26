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
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ExtensionLoader {

    public static final String EXT_JAR_DIR = Configs.USER_DATA_DIR + File.separator + "extensions";

    public static List<ExtensionJar> listExternalJars() {
        List<ExtensionJar> list = new ArrayList<>();
        File dir = new File(EXT_JAR_DIR);
        if (dir.exists()) {
            File[] sub = dir.listFiles(new JarFilter());
            if (sub != null) {
                for (File file : sub) {
                    try {
                        URL jarUrl = file.toURI().toURL();
                        JarFile jarFile = new JarFile(file);
                        List<Class<?>> classesInJar = findClassInJar(jarFile, jarUrl);
                        list.add(new ExtensionJar(file.getName(), classesInJar));
                    } catch (IOException e) {
                        e.printStackTrace();
                        EventLogger.log(e);
                    }
                }
            }
        } else {
            try {
                if (!dir.createNewFile()) {
                    System.err.println("Failed to create " + EXT_JAR_DIR);
                    EventLogger.log("Failed to create " + EXT_JAR_DIR);
                }
            } catch (IOException e) {
                e.printStackTrace();
                EventLogger.log(e);
            }
        }
        return list;
    }

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

    public static List<FileFormatReader> listEnabledExternalReaders() {
        List<ExtensionJar> allClasses = listExternalJars();
        Set<String> enabled = Configs.getConfigs().getEnabledJars();
        List<FileFormatReader> result = new ArrayList<>();
        for (ExtensionJar ej : allClasses) {
            if (enabled.contains(ej.getJarName())) {
                for (Class<?> clazz : ej.getClassList()) {
                    if (subclassOf(clazz, FileFormatReader.class)) {
                        try {
                            result.add((FileFormatReader) clazz.getDeclaredConstructor().newInstance());
                        } catch (Exception e) {
                            //
                        }
                    }
                }
            }
        }
        return result;
    }

//    public static List<Class<?>> listExternalClasses() {
//        List<File> jarFiles = listExternalJars();
//        List<Class<?>> result = new ArrayList<>();
//        for (File file : jarFiles) {
//            try {
//                URL jarUrl = file.toURI().toURL();
//                JarFile jarFile = new JarFile(file);
//                findClassInJar(jarFile, jarUrl, result);
//            } catch (IOException e) {
//                e.printStackTrace();
//                EventLogger.log(e);
//            }
//        }
//        return result;
//    }

    private static List<Class<?>> findClassInJar(JarFile jarFile, URL jarUrl) {
        List<Class<?>> list = new ArrayList<>();
        Enumeration<JarEntry> enumeration = jarFile.entries();
        while (enumeration.hasMoreElements()) {
            JarEntry entry = enumeration.nextElement();
            String name = entry.getName();
            if (!entry.isDirectory() && name.endsWith(".class")) {
                if (name.endsWith("module-info.class")) continue;
//                if (name.endsWith(FileFormatReader.class.getSimpleName() + ".class")) continue;
//                if (name.endsWith(SimpleReader.class.getSimpleName() + ".class")) continue;
//                if (name.endsWith(OneKeyReader.class.getSimpleName() + ".class")) continue;
//                if (name.endsWith(TwoKeysReader.class.getSimpleName() + ".class")) continue;

                // removes ".class"
                String className = name.substring(0, name.length() - 6).replace('/', '.');
                Class<?> clazz = loadClass(className, jarUrl);
                if (clazz != null) {
                    list.add(clazz);
                }
            }
        }
        return list;
    }

    private static Class<?> loadClass(String className, URL jarUrl) {
        try {
            URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{jarUrl});
            return urlClassLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean subclassOf(Class<?> childClass, Class<?> superClass) {
        if (childClass == null) return false;
        if (childClass == superClass) return true;
        return subclassOf(childClass.getSuperclass(), superClass);
    }

    public static boolean isAbstract(Class<?> clazz) {
        return Modifier.isAbstract(clazz.getModifiers());
    }

    private static class JarFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith("jar");
        }
    }
}
