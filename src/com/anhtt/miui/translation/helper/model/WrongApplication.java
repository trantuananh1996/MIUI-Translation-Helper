package com.anhtt.miui.translation.helper.model;

import com.anhtt.miui.translation.helper.model.res.ArrayRes;
import com.anhtt.miui.translation.helper.model.res.PluralRes;
import com.anhtt.miui.translation.helper.model.res.StringRes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WrongApplication {
    private String name;
    List<WrongStringRes> wrongTranslatedOriginStrings = new ArrayList<>();//String gốc bị dịch sai
    List<WrongStringRes> wrongTranslatedFromAllOriginStrings = new ArrayList<>();//String gốc bị dịch sai
    List<WrongStringRes> wrongTranslatedStrings = new ArrayList<>();//String bị dịch sai

    List<WrongArrayRes> wrongTranslatedOriginArrays = new ArrayList<>();//String gốc bị dịch sai
    List<WrongArrayRes> wrongTranslatedFromAllOriginArrays = new ArrayList<>();//String gốc bị dịch sai
    List<WrongArrayRes> wrongTranslatedArrays = new ArrayList<>();//String bị dịch sai

    List<WrongPluralRes> wrongTranslatedOriginPlurals = new ArrayList<>();//String gốc bị dịch sai
    List<WrongPluralRes> wrongTranslatedFromAllOriginPlurals = new ArrayList<>();//String gốc bị dịch sai
    List<WrongPluralRes> wrongTranslatedPlurals = new ArrayList<>();//String bị dịch sai

    public WrongApplication(String name) {
        this.name = name;
    }

    public List<WrongPluralRes> getWrongTranslatedOriginPlurals() {
        return wrongTranslatedOriginPlurals;
    }

    public List<WrongPluralRes> getWrongTranslatedPlurals() {
        return wrongTranslatedPlurals;
    }

    public String getName() {
        return name;
    }

    public List<WrongArrayRes> getWrongTranslatedOriginArrays() {
        return wrongTranslatedOriginArrays;
    }

    public List<WrongArrayRes> getWrongTranslatedFromAllOriginArrays() {
        return wrongTranslatedFromAllOriginArrays;
    }

    public List<WrongPluralRes> getWrongTranslatedFromAllOriginPlurals() {
        return wrongTranslatedFromAllOriginPlurals;
    }

    public List<WrongArrayRes> getWrongTranslatedArrays() {
        return wrongTranslatedArrays;
    }

    public List<WrongStringRes> getWrongTranslatedOriginStrings() {
        return wrongTranslatedOriginStrings;
    }

    public List<WrongStringRes> getWrongTranslatedStrings() {
        return wrongTranslatedStrings;
    }

    public List<WrongStringRes> getWrongTranslatedFromAllOriginStrings() {
        return wrongTranslatedFromAllOriginStrings;
    }

    public void addOrigin(int size, SourceDevice sourceDevice, StringRes origin) {
        Optional<WrongStringRes> hasString = wrongTranslatedOriginStrings.stream().filter(stringRes1 -> stringRes1.getName().equals(origin.getName()) && stringRes1.getValue().equals(origin.getValue())).findFirst();
        WrongStringRes wrongStringRes;
        if (!hasString.isPresent()) {
            wrongStringRes = new WrongStringRes(origin.getName(), origin.getValue(), origin.isFormatted());
            wrongStringRes.addDevice(sourceDevice.getName());
            wrongTranslatedOriginStrings.add(wrongStringRes);
        } else {
            wrongStringRes = hasString.get();
            wrongStringRes.addDevice(sourceDevice.getName());
        }
        if (wrongStringRes.getDevices().size() == size) {
            wrongStringRes.getDevices().clear();
            wrongStringRes.getDevices().add("all");
        }

    }

    public void addOriginFromAll(int size, SourceDevice sourceDevice, StringRes origin) {
        Optional<WrongStringRes> hasString = wrongTranslatedFromAllOriginStrings.stream().filter(stringRes1 -> stringRes1.getName().equals(origin.getName()) && stringRes1.getValue().equals(origin.getValue())).findFirst();
        WrongStringRes wrongStringRes;
        if (!hasString.isPresent()) {
            wrongStringRes = new WrongStringRes(origin.getName(), origin.getValue(), origin.isFormatted());
            wrongStringRes.addDevice(sourceDevice.getName());
            wrongTranslatedFromAllOriginStrings.add(wrongStringRes);
        } else {
            wrongStringRes = hasString.get();
            wrongStringRes.addDevice(sourceDevice.getName());
        }
        if (wrongStringRes.getDevices().size() == size) {
            wrongStringRes.getDevices().clear();
            wrongStringRes.getDevices().add("all");
        }

    }

    public void addOriginFromAll(int size, SourceDevice sourceDevice, PluralRes origin) {
        Optional<WrongPluralRes> hasString = wrongTranslatedFromAllOriginPlurals.stream().filter(stringRes1 -> stringRes1.equalsExact(origin)).findFirst();
        WrongPluralRes wrongStringRes;
        if (!hasString.isPresent()) {
            wrongStringRes = new WrongPluralRes(origin.getName(), origin.getArrayType(),origin.getItems());
            wrongStringRes.addDevice(sourceDevice.getName());
            wrongTranslatedFromAllOriginPlurals.add(wrongStringRes);
        } else {
            wrongStringRes = hasString.get();
            wrongStringRes.addDevice(sourceDevice.getName());
        }
        if (wrongStringRes.getDevices().size() == size) {
            wrongStringRes.getDevices().clear();
            wrongStringRes.getDevices().add("all");
        }

    }

    public void addOriginFromAll(int size, SourceDevice sourceDevice, ArrayRes origin) {
        Optional<WrongArrayRes> hasString = wrongTranslatedFromAllOriginArrays.stream().filter(stringRes1 -> stringRes1.equalsExact(origin)).findFirst();
        WrongArrayRes wrongStringRes;
        if (!hasString.isPresent()) {
            wrongStringRes = new WrongArrayRes(origin.getName(), origin.getArrayType(), origin.getItems());
            wrongStringRes.addDevice(sourceDevice.getName());
            wrongTranslatedFromAllOriginArrays.add(wrongStringRes);
        } else {
            wrongStringRes = hasString.get();
            wrongStringRes.addDevice(sourceDevice.getName());
        }
        if (wrongStringRes.getDevices().size() == size) {
            wrongStringRes.getDevices().clear();
            wrongStringRes.getDevices().add("all");
        }

    }

    public void addOrigin(int size, SourceDevice sourceDevice, ArrayRes origin) {
        Optional<WrongArrayRes> hasString = wrongTranslatedOriginArrays.stream().filter(stringRes1 -> stringRes1.equalsExact(origin)).findFirst();
        WrongArrayRes wrongStringRes;
        if (!hasString.isPresent()) {
            wrongStringRes = new WrongArrayRes(origin.getName(), origin.getArrayType(), origin.getItems());
            wrongStringRes.addDevice(sourceDevice.getName());
            wrongTranslatedOriginArrays.add(wrongStringRes);
        } else {
            wrongStringRes = hasString.get();
            wrongStringRes.addDevice(sourceDevice.getName());
        }
        if (wrongStringRes.getDevices().size() == size) {
            wrongStringRes.getDevices().clear();
            wrongStringRes.getDevices().add("all");
        }

    }

    public void addOrigin(int size, SourceDevice sourceDevice, PluralRes origin) {
        Optional<WrongPluralRes> hasString = wrongTranslatedOriginPlurals.stream().filter(stringRes1 -> stringRes1.equalsExact(origin)).findFirst();
        WrongPluralRes wrongStringRes;
        if (!hasString.isPresent()) {
            wrongStringRes = new WrongPluralRes(origin.getName(), origin.getArrayType(), origin.getItems());
            wrongStringRes.addDevice(sourceDevice.getName());
            wrongTranslatedOriginPlurals.add(wrongStringRes);
        } else {
            wrongStringRes = hasString.get();
            wrongStringRes.addDevice(sourceDevice.getName());
        }
        if (wrongStringRes.getDevices().size() == size) {
            wrongStringRes.getDevices().clear();
            wrongStringRes.getDevices().add("all");
        }

    }

    @Override
    public String toString() {
        return name;
    }
}

