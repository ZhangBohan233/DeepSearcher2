package tplReader;

import java.util.Locale;

@SuppressWarnings("unused")
public class SplReader extends TplSplReader {

    public SplReader() {
        super();
    }

    @Override
    public String[] extensions() {
        return new String[]{"sp"};
    }

    @Override
    public String description(Locale locale) {
        if (locale.getLanguage().equals(new Locale("ZH", "cn").getLanguage())) {
            return "龟速语言源代码";
        } else {
            return "Slowest Program Source Code";
        }
    }
}
