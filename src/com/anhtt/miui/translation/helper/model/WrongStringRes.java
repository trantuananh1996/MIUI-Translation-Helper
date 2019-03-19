package com.anhtt.miui.translation.helper.model;

import java.util.ArrayList;
import java.util.List;

public class WrongStringRes {
    private String name;
    private String value;
    private List<String> devices = new ArrayList<>();
    private boolean formatted = true;


    public void addDevice(String name) {
        if (devices.contains("all")) return;
        if (!devices.contains(name))
            devices.add(name);
    }

    public List<String> getDevices() {
        return devices;
    }

    public void setFormatted(boolean formatted) {
        this.formatted = formatted;
    }

    public boolean isFormatted() {
        return formatted;
    }

    public WrongStringRes(String name, String value, boolean formatted) {
        this.formatted = formatted;
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
