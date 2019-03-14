package com.anhtt.miui.translation.helper.model;

import com.sun.istack.internal.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TranslatedDevice {
    private String name;
    private File translation;
    private List<Application> apps;

    public TranslatedDevice(String name, File translation) {
        this.name = name;
        this.translation = translation;
    }

    @Nullable
    private static String getDeviceName(String path) {
        String name = "";
        try {
            File file = new File(path);
            name = file.getName();
            if (!name.contains("Diff")&&!name.contains("extras") && !name.contains("stable") && !name.contains(".") && !file.isHidden())
                return name;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Nullable
    public static TranslatedDevice create(String path) {
        String deviceName = getDeviceName(path);
        if (deviceName == null || deviceName.isEmpty()) return null;

        File file = new File(path);

        if (file.exists()) {
            TranslatedDevice originDevice = new TranslatedDevice(deviceName, file);
            File[] child = file.listFiles();
            if (child != null) {
                List<Application> apps = new ArrayList<>();
                for (File appFolder : child) {
                    if (appFolder.exists() && appFolder.isDirectory()) {
                        Application app = Application.create(appFolder.getAbsolutePath());
                        if (app != null) apps.add(app);
                    }
                }
                originDevice.setApps(apps);
            }


            return originDevice;
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public File getTranslation() {
        return translation;
    }

    public void setApps(List<Application> apps) {
        this.apps = apps;
    }

    public List<Application> getApps() {
        return apps;
    }
}
