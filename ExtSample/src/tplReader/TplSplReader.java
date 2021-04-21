package tplReader;

import dsApi.api.SplitterReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class TplSplReader extends SplitterReader {

    @Override
    public String splitterFormat(Locale locale) {
        if (locale.getLanguage().equals(new Locale("ZH", "cn").getLanguage())) {
            return "第%d行";
        } else {
            return "line %d";
        }
    }

    @Override
    public String characterFormat(Locale locale) {
        if (locale.getLanguage().equals(new Locale("ZH", "cn").getLanguage())) {
            return "第%d字";
        } else {
            return "character %d";
        }
    }

    @Override
    public String readFile(File file) throws IOException {
        String[] lines = readBySplitter(file);
        return String.join("\n", lines);
    }

    @Override
    public String[] readBySplitter(File file) throws IOException {
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
