package tplReader;

import dsApi.api.OneKeyReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TplReader extends OneKeyReader {

    public TplReader() {
        super();
    }

    @Override
    public String[] extensions() {
        return new String[]{"tpl"};
    }

    @Override
    public String description(Locale locale) {
        if (locale.getLanguage().equals(new Locale("ZH", "cn").getLanguage())) {
            return "垃圾语言源代码";
        } else {
            return "Trash Program Source Code";
        }
    }

    @Override
    public String primaryKeyDescription(Locale locale) {
        if (locale.getLanguage().equals(new Locale("ZH", "cn").getLanguage())) {
            return "行";
        } else {
            return "line";
        }
    }

    @Override
    public String readFile(File file) throws IOException {
        String[] lines = readByPrimaryKey(file);
        return String.join("\n", lines);
    }

    @Override
    public String[] readByPrimaryKey(File file) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            List<String> list = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
            return list.toArray(new String[0]);
        }
    }
}
