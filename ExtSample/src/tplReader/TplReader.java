package tplReader;

import java.util.Locale;

@SuppressWarnings("unused")
public class TplReader extends TplSplReader {

    public TplReader() {
        super();
    }

    @Override
    public String[] extensions() {
        return new String[]{"tp", "tpl"};
    }

    @Override
    public String description(Locale locale) {
        if (locale.getLanguage().equals(new Locale("ZH", "cn").getLanguage())) {
            return "垃圾语言源代码";
        } else {
            return "Trash Program Source Code";
        }
    }
}
