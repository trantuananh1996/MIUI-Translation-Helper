package com.anhtt.miui.translation.helper.model;

import com.anhtt.miui.translation.helper.MainGUI;
import com.sun.istack.internal.Nullable;

import java.io.File;
import java.util.*;

public class SourceDevice {
    private String name;
    private File translation;
    private Map<String, Application> apps;

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

//            if (!name.contains("Diff") && !name.contains("extras") && name.contains("_stable") && !name.contains(".") && !file.isHidden())
//                if (isValidDevice(name.replaceAll("_stable", "")))
//                    return name.replaceAll("_stable", "");

            if (!name.contains("Diff") && !name.contains("extras") && !name.contains("stable") && !name.contains(".") && !file.isHidden())
                if (isValidDevice(name))
                    return name;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static boolean isValidDevice(String name) {//Ignore very old device
//        return true;
        List<String> strings = Arrays.asList(
//                "begonia",
                "apollo",
                "cepheus",
                "cas",
                "cmi",
                "crux",
                "davinci",
                "gauguin",
                "ginkgo",
                "grus",
                "laurus",
                "lavender",
                "lime",
                "lmi",
                "phoenix",
                "picasso",
                "picasso_48m",
                "pyxis",
                "raphael",
                "tucana",
                "umi",
                "vangogh",
                "violet",
                "venus");
        return strings.stream()
                .anyMatch(name::contains);
    }

    @Nullable
    public static SourceDevice create(MainGUI.StringWorker swingWorker, String path, TargetDevice transDevices, List<SourceDevice> targetSeparateDevices, boolean needCompare) {
        String deviceName = getDeviceName(path);
        if (deviceName == null || deviceName.isEmpty()) return null;

        File file = new File(path);

        if (file.exists()) {
            SourceDevice sourceDevice = new SourceDevice(deviceName, file);
            swingWorker.sendLog("Processing device " + deviceName);
            File[] child = file.listFiles();
            if (child != null) {
                Map<String, Application> apps = new HashMap<>();
                for (File appFolder : child) {
                    if (appFolder.exists() && appFolder.isDirectory()) {
                        Application app = Application.create(appFolder.getAbsolutePath(), true);
                        if (app != null) {
                            if (needCompare) {
                                swingWorker.sendLog("Processing " + deviceName + ": " + app.getName());

                                Application separateApp = findSpecifiedApp(targetSeparateDevices, sourceDevice, app);
                                Application translatedApp = findTranslatedApp(transDevices, app);
                                app.compare(sourceDevice, separateApp, translatedApp, transDevices);
                            }
                            apps.put(app.getName(), app);
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
                Map<String, Application> apps = new HashMap<>();
                for (File appFolder : child) {
                    if (appFolder.exists() && appFolder.isDirectory()) {
                        Application app = Application.create(appFolder.getAbsolutePath(), false);
                        if (app != null) {
                            apps.put(app.getName(), app);
                        }
                    }
                }
                sourceDevice.setApps(apps);
            }


            return sourceDevice;
        }
        return null;
    }

    private static Application findSpecifiedApp(List<SourceDevice> specificSourceDevices, SourceDevice sourceDevice, Application app) {

        //Duyệt từng sourceDevice trong ngôn ngữ gốc
        Optional<SourceDevice> specificDevice = specificSourceDevices.stream().filter(sourceDevice1 -> sourceDevice.getName().equals(sourceDevice1.getName())).findAny();
        if (specificDevice.isPresent()) {//Có tùy chỉnh
            Application specificApp = specificDevice.get().getApps().get(app.getName());
            if (specificApp != null) return specificApp;
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

    public void setApps(Map<String, Application> apps) {
        this.apps = apps;
    }

    public Map<String, Application> getApps() {
        return apps;
    }
}
