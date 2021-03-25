package trashsoftware.deepSearcher2.extensionLoader;

import dsApi.api.FileFormatReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ExternalReaderJar extends ExtensionJar {
    private final List<FileFormatReader> formatReaders = new ArrayList<>();

    ExternalReaderJar(File file, List<Class<?>> classList) {
        super(file, classList);

        for (Class<?> clazz : classList) {
            if (ExtensionLoader.subclassOf(clazz, FileFormatReader.class)) {
                try {
                    FileFormatReader ffr = (FileFormatReader) clazz.getDeclaredConstructor().newInstance();
                    formatReaders.add(ffr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<FileFormatReader> getFormatReaders() {
        return formatReaders;
    }
}
