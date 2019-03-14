package com.anhtt.miui.translation.helper.model;

public class StringRes {
    private String name;
    private String value;
    private boolean formatted = true;

    public void setFormatted(boolean formatted) {
        this.formatted = formatted;
    }

    public boolean isFormatted() {
        return formatted;
    }

    public StringRes(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

}
