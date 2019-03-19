package com.anhtt.miui.translation.helper.model;

import com.anhtt.miui.translation.helper.MainGUI;
import com.sun.istack.internal.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OriginDevice {
    private String name;
    private File translation;
    private List<Application> apps;

    public OriginDevice(String name, File translation) {
        this.name = name;
        this.translation = translation;
    }

    @Nullable
    private static String getDeviceName(String path) {
        String name = "";
        try {
            File file = new File(path);
            name = file.getName();
            if (!name.contains("Diff") && !name.contains("extras") && !name.contains("stable") && !name.contains(".") && !file.isHidden())
                return name;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Nullable
    public static OriginDevice create(MainGUI.StringWorker swingWorker, String path, TranslatedDevice transDevices, List<OriginDevice> specificOriginDevices) {
        String deviceName = getDeviceName(path);
        if (deviceName == null || deviceName.isEmpty()) return null;

        File file = new File(path);

        if (file.exists()) {
            OriginDevice originDevice = new OriginDevice(deviceName, file);
            MainGUI.tvLogStatic.setText("Đang xử lý " + deviceName);
            File[] child = file.listFiles();
            if (child != null) {
                List<Application> apps = new ArrayList<>();
                for (File appFolder : child) {
                    if (appFolder.exists() && appFolder.isDirectory()) {
                        Application app = Application.create(appFolder.getAbsolutePath());
                        if (app != null) {
                           swingWorker.sendLog("Đang xử lý " + deviceName + ": " + app.getName());

                            Application specificedApp = findSpecificedApp(specificOriginDevices, originDevice, app);
                            Application translatedApp = findTranslatedApp(transDevices, app);
                            app.compare(originDevice, specificedApp, translatedApp,transDevices);
                            apps.add(app);
                        }
                    }
                }
                originDevice.setApps(apps);
            }


            return originDevice;
        }
        return null;
    }

    @Nullable
    public static OriginDevice create(String path) {
        String deviceName = getDeviceName(path);
        if (deviceName == null || deviceName.isEmpty()) return null;

        File file = new File(path);

        if (file.exists()) {
            OriginDevice originDevice = new OriginDevice(deviceName, file);
            File[] child = file.listFiles();
            if (child != null) {
                List<Application> apps = new ArrayList<>();
                for (File appFolder : child) {
                    if (appFolder.exists() && appFolder.isDirectory()) {
                        Application app = Application.create(appFolder.getAbsolutePath());
                        if (app != null) {
                            apps.add(app);
                        }
                    }
                }
                originDevice.setApps(apps);
            }


            return originDevice;
        }
        return null;
    }

    private static Application findSpecificedApp(List<OriginDevice> specificOriginDevices, OriginDevice originDevice, Application app) {

        //Duyệt từng originDevice trong ngôn ngữ gốc
        Optional<OriginDevice> specificDevice = specificOriginDevices.stream().filter(originDevice1 -> originDevice.getName().equals(originDevice1.getName())).findAny();
        if (specificDevice.isPresent()) {//Có tùy chỉnh
            Optional<Application> specificApp = specificDevice.get().getApps().stream().filter(app1 -> app.getName().equals(app1.getName())).findAny();
            if (specificApp.isPresent()) return specificApp.get();
        }
        return null;
    }

    private static Application findTranslatedApp(TranslatedDevice originDevice, Application app) {
        //Duyệt từng originDevice trong ngôn ngữ gốc
        Optional<Application> specificApp = originDevice.getApps().stream().filter(app1 -> app.getName().equals(app1.getName())).findAny();
        return specificApp.orElse(null);
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
