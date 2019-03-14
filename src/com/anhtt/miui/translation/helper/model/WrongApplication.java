package com.anhtt.miui.translation.helper.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WrongApplication {
    private String name;
    List<WrongStringRes> wrongTranslatedOrigins = new ArrayList<>();//String gốc bị dịch sai
    List<WrongStringRes> wrongTranslateds = new ArrayList<>();//String bị dịch sai

    public WrongApplication(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<WrongStringRes> getWrongTranslatedOrigins() {
        return wrongTranslatedOrigins;
    }

    public List<WrongStringRes> getWrongTranslateds() {
        return wrongTranslateds;
    }

    public void addOrigin(int size, OriginDevice originDevice, StringRes origin) {
        Optional<WrongStringRes> hasString = wrongTranslatedOrigins.stream().filter(stringRes1 -> stringRes1.getName().equals(origin.getName()) && stringRes1.getValue().equals(origin.getValue())).findFirst();
        WrongStringRes wrongStringRes;
        if (!hasString.isPresent()) {
            wrongStringRes = new WrongStringRes(origin.getName(), origin.getValue());
            wrongStringRes.addDevice(originDevice.getName());
            wrongTranslatedOrigins.add(wrongStringRes);
        } else {
            wrongStringRes = hasString.get();
            wrongStringRes.addDevice(originDevice.getName());
        }
        if (wrongStringRes.getDevices().size() == size) {
            wrongStringRes.getDevices().clear();
            wrongStringRes.getDevices().add("all");
        }

    }
}

