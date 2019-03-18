package com.anhtt.miui.translation.helper.model;

import com.anhtt.miui.translation.helper.model.res.ArrayRes;
import com.anhtt.miui.translation.helper.model.res.Item;

import java.util.ArrayList;
import java.util.List;

public class WrongArrayRes {
    String name;
    String arrayType;
    List<Item> items = new ArrayList<>();
    private List<String> devices = new ArrayList<>();
    private boolean formatted = true;


    public void addDevice(String name) {
        if (devices.contains("all")) return;
        if (!devices.contains(name))
            devices.add(name);
    }

    public String getArrayType() {
        return arrayType;
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

    public String getName() {
        return name;
    }

    public List<Item> getItems() {
        return items;
    }

    public WrongArrayRes(String name, String arrayType, List<Item> items) {
        this.name = name;
        this.arrayType = arrayType;
        this.items = items;
    }

    public boolean equalsExact(ArrayRes origin) {
        if (!name.equals(origin.getName())) return false;
        if (items == null && origin.getItems() != null) return false;
        if (origin.getItems() == null && items != null) return false;
        if (items == null && origin.getItems() == null) return true;
        if (items.size() != origin.getItems().size()) return false;
        for (int i = 0; i < items.size(); i++)
            if (!items.get(i).getValue().equals(origin.getItems().get(i).getValue())) return false;
        return true;
    }
}
