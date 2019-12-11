package com.anhtt.miui.translation.helper.model;

import java.util.HashMap;

public enum Language {
    French("fr", "FR", "https://github.com/pmspr4100/MA-XML-11-FRENCH"),
    Russian("ru", "RU", "https://github.com/ingbrzy/MA-XML-11-RUSSIAN"),
    Croatian("hr", "HR", "https://github.com/MASVA/MIUI-11-XML-CROATIAN-TRANSLATION"),
    Vietnamese("vi", "VN", "https://github.com/Belmont-Gabriel/MIUI-11-XML-Vietnamese"),
    Italian("it", "IT", "https://github.com/MishFdM/MIUI_V10_Italy"),
    Dutch("nl", "NL", "https://github.com/redmaner/MIUI11-XML-DUTCH"),
    Spanish("es", "ES", "https://github.com/danielchc/MA-XML-11-SPANISH"),
    Turkish("tr", "TR", "https://github.com/suat074/MA-XML-11-TURKISH"),
    Brazilian("pt", "BR", "https://github.com/RicardoGuariento/MIUI-XML-11-BRAZILIAN"),
    Portuguese("pt", "PT", "https://github.com/KcNirvana/MIUI-XML-11-PORTUGUESE"),
    Romanian("ro", "RO", "https://github.com/ashtefan/Romanian_MIUI_11"),
    German("de", "DE", "https://github.com/berlinux2016/MIUI11"),
    Catalan("ca", "CA", "https://github.com/deivids84/MA-XML-11-CATALAN"),
    Korean("ko", "KR", "https://github.com/cjhyuky/MA-XML-MIUI11-KOREAN"),
    Slovenian("sl", "SI", "https://github.com/bostjan2016/MIUI-11-SLOVENIAN"),
    Serbian("sr", "RS", "https://github.com/dudjaa/MIUI-v10-Serbian-translation"),
    Finnish("fi", "FI", "https://github.com/dogiex/MIUI-XML-11-FINNISH"),
    Japanese("ja", "JP", "https://github.com/ScratchBuild/MIUI11-XML-Japanese");

    private String locale;
    private String code;
    private String gitUrl;

    private static HashMap<String, Language> map = new HashMap<>();

    static {
        for (Language rollEnum : Language.values()) {
            map.put(rollEnum.code, rollEnum);
        }
    }

    Language(final String locale, String code, String gitUrl) {
        this.locale = locale;
        this.code = code;
        this.gitUrl = gitUrl;
    }

    public String getGitUrl() {
        return gitUrl;
    }

    public static Language forCode(String code) {
        return map.get(code);
    }

    public String getCode() {
        return code;
    }

    public String getLocale() {
        return locale;
    }
}
