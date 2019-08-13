package com.anhtt.miui.translation.helper.model;

import com.anhtt.miui.translation.helper.MainGUI;
import com.sun.istack.internal.Nullable;

import java.io.File;
import java.util.*;

public class SourceDevice {
    private String name;
    private File translation;
    private List<Application> apps;

    public SourceDevice(String name, File translation) {
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
                if (!isIgnoreDevice(name))
                    return name;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static boolean isIgnoreDevice(String name) {//Ignore very old device
        List<String> strings = Arrays.asList("aqua", "beryllium", "cancro", "cereus", "kate", "kenzo", "libra", "helium"
                , "hydrogen", "land", "markw", "mido", "nikel", "omega", "prada", "rolex");
        return strings.contains(name);
    }

    @Nullable
    public static SourceDevice create(MainGUI.StringWorker swingWorker, String path, TargetDevice transDevices, List<SourceDevice> targetSeparateDevices) {
        String deviceName = getDeviceName(path);
        if (deviceName == null || deviceName.isEmpty()) return null;

        File file = new File(path);

        if (file.exists()) {
            SourceDevice sourceDevice = new SourceDevice(deviceName, file);
            swingWorker.sendLog("Đang xử lý " + deviceName);
            File[] child = file.listFiles();
            if (child != null) {
                List<Application> apps = new ArrayList<>();
                for (File appFolder : child) {
                    if (appFolder.exists() && appFolder.isDirectory()) {
                        Application app = Application.create(appFolder.getAbsolutePath(), true);
                        if (app != null) {
                            swingWorker.sendLog("Đang xử lý " + deviceName + ": " + app.getName());

                            Application separateApp = findSpecificedApp(targetSeparateDevices, sourceDevice, app);
                            Application translatedApp = findTranslatedApp(transDevices, app);
                            app.compare(sourceDevice, separateApp, translatedApp, transDevices);
                            apps.add(app);
                        }
                    }
                }
                sourceDevice.setApps(apps);
            }


            return sourceDevice;
        }
        return null;
    }

    @Nullable
    public static SourceDevice create(String path) {
        String deviceName = getDeviceName(path);
        if (deviceName == null || deviceName.isEmpty()) return null;

        File file = new File(path);

        if (file.exists()) {
            SourceDevice sourceDevice = new SourceDevice(deviceName, file);
            File[] child = file.listFiles();
            if (child != null) {
                List<Application> apps = new ArrayList<>();
                for (File appFolder : child) {
                    if (appFolder.exists() && appFolder.isDirectory()) {
                        Application app = Application.create(appFolder.getAbsolutePath(), false);
                        if (app != null) {
                            apps.add(app);
                        }
                    }
                }
                sourceDevice.setApps(apps);
            }


            return sourceDevice;
        }
        return null;
    }

    private static Application findSpecificedApp(List<SourceDevice> specificSourceDevices, SourceDevice sourceDevice, Application app) {

        //Duyệt từng sourceDevice trong ngôn ngữ gốc
        Optional<SourceDevice> specificDevice = specificSourceDevices.stream().filter(sourceDevice1 -> sourceDevice.getName().equals(sourceDevice1.getName())).findAny();
        if (specificDevice.isPresent()) {//Có tùy chỉnh
            Optional<Application> specificApp = specificDevice.get().getApps().stream().filter(app1 -> app.getName().equals(app1.getName())).findAny();
            if (specificApp.isPresent()) return specificApp.get();
        }
        return null;
    }

    private static Application findTranslatedApp(TargetDevice originDevice, Application app) {
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
