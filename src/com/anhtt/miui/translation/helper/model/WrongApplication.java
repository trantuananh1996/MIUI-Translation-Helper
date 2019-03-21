package com.anhtt.miui.translation.helper.model;

import com.anhtt.miui.translation.helper.model.res.ArrayRes;
import com.anhtt.miui.translation.helper.model.res.StringRes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WrongApplication {
    private String name;
    List<WrongStringRes> wrongTranslatedOriginStrings = new ArrayList<>();//String gốc bị dịch sai
    List<WrongStringRes> wrongTranslatedStrings = new ArrayList<>();//String bị dịch sai

    List<WrongArrayRes> wrongTranslatedOriginArrays = new ArrayList<>();//String gốc bị dịch sai
    List<WrongArrayRes> wrongTranslatedArrays = new ArrayList<>();//String bị dịch sai

    public WrongApplication(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<WrongArrayRes> getWrongTranslatedOriginArrays() {
        return wrongTranslatedOriginArrays;
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

    public void addOrigin(int size, OriginDevice originDevice, StringRes origin) {
        Optional<WrongStringRes> hasString = wrongTranslatedOriginStrings.stream().filter(stringRes1 -> stringRes1.getName().equals(origin.getName()) && stringRes1.getValue().equals(origin.getValue())).findFirst();
        WrongStringRes wrongStringRes;
        if (!hasString.isPresent()) {
            wrongStringRes = new WrongStringRes(origin.getName(), origin.getValue(), origin.isFormatted());
            wrongStringRes.addDevice(originDevice.getName());
            wrongTranslatedOriginStrings.add(wrongStringRes);
        } else {
            wrongStringRes = hasString.get();
            wrongStringRes.addDevice(originDevice.getName());
        }
        if (wrongStringRes.getDevices().size() == size) {
            wrongStringRes.getDevices().clear();
            wrongStringRes.getDevices().add("all");
        }

    }

    public void addOrigin(int size, OriginDevice originDevice, ArrayRes origin) {
        Optional<WrongArrayRes> hasString = wrongTranslatedOriginArrays.stream().filter(stringRes1 -> stringRes1.equalsExact(origin)).findFirst();
        WrongArrayRes wrongStringRes;
        if (!hasString.isPresent()) {
            wrongStringRes = new WrongArrayRes(origin.getName(), origin.getArrayType(), origin.getItems());
            wrongStringRes.addDevice(originDevice.getName());
            wrongTranslatedOriginArrays.add(wrongStringRes);
        } else {
            wrongStringRes = hasString.get();
            wrongStringRes.addDevice(originDevice.getName());
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

