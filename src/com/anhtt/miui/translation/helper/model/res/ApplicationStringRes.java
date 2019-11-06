package com.anhtt.miui.translation.helper.model.res;

public class ApplicationStringRes extends StringRes {
    private String untranslatedString;

    public void setUntranslatedString(String untranslatedString) {
        this.untranslatedString = untranslatedString;
    }

    public String getUntranslatedString() {
        return untranslatedString;
    }

    public ApplicationStringRes(String name, String value) {
        super(name, value);
    }
}
