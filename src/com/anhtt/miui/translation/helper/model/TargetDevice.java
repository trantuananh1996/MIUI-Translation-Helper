package com.anhtt.miui.translation.helper.model;

import com.anhtt.miui.translation.helper.model.res.ApplicationStringRes;
import com.anhtt.miui.translation.helper.model.res.ArrayRes;
import com.anhtt.miui.translation.helper.model.res.PluralRes;
import com.anhtt.miui.translation.helper.model.res.StringRes;
import com.sun.istack.internal.Nullable;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class TargetDevice {
    private String name;
    private File translation;
    private List<Application> apps;
    //    private List<StringRes> allStrings = new ArrayList<>();
    private Map<String, ApplicationStringRes> mapAllStrings = new HashMap<>();
    private Map<String, ArrayRes> mapAllArrays = new HashMap<>();
    //    private List<ArrayRes> allArrays = new ArrayList<>();
    private Map<String, PluralRes> mapAllPlurals = new HashMap<>();

    public Map<String, PluralRes> getMapAllPlurals() {
        return mapAllPlurals;
    }

    public void setMapAllPlurals(Map<String, PluralRes> mapAllPlurals) {
        this.mapAllPlurals = mapAllPlurals;
    }

    public Map<String, ArrayRes> getMapAllArrays() {
        return mapAllArrays;
    }

    public void setMapAllArrays(Map<String, ArrayRes> mapAllArrays) {
        this.mapAllArrays = mapAllArrays;
    }

    public Map<String, ApplicationStringRes> getMapAllStrings() {
        return mapAllStrings;
    }

    public void setMapAllStrings(Map<String, ApplicationStringRes> mapAllStrings) {
        this.mapAllStrings = mapAllStrings;
    }


//    public List<StringRes> getAllStrings() {
//        return allStrings;
//    }

//    public void setAllStrings(List<StringRes> allStrings) {
//        this.allStrings = allStrings;
//    }

//    public List<ArrayRes> getAllArrays() {
//        return allArrays;
//    }
//
//    public void setAllArrays(List<ArrayRes> allArrays) {
//        this.allArrays = allArrays;
//    }

    public TargetDevice(String name, File translation) {
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
    public static TargetDevice create(String path, boolean isApplyFilter, SourceDevice sourceDevicesWithoutCompare) {
        String deviceName = getDeviceName(path);
        if (deviceName == null || deviceName.isEmpty()) return null;

        File file = new File(path);

        if (file.exists()) {
            TargetDevice originDevice = new TargetDevice(deviceName, file);
            File[] child = file.listFiles();
            if (child != null) {
                List<Application> apps = new ArrayList<>();
                for (File appFolder : child) {
                    if (appFolder.exists() && appFolder.isDirectory()) {
                        Application app = Application.create(appFolder.getAbsolutePath(), isApplyFilter);
                        if (app != null) {
                            apps.add(app);
                            if (sourceDevicesWithoutCompare != null)
                                for (StringRes stringRes : app.getMapOriginStrings().values()) {
                                    Application engApp = sourceDevicesWithoutCompare.getApps().get(app.getName());
                                    if (engApp != null) {
                                        StringRes engString = engApp.getMapOriginStrings().get(stringRes.getName());
                                        if (engString != null) {
                                            ApplicationStringRes applicationStringRes = new ApplicationStringRes(stringRes.getName(), stringRes.getValue());
                                            applicationStringRes.setUntranslatedString(engString.getValue());
//
                                            originDevice.getMapAllStrings().put(stringRes.getName(), applicationStringRes);
                                        }
                                    }

                                }
                            originDevice.getMapAllArrays().putAll(app.getMapOriginArrays());
                            originDevice.getMapAllPlurals().putAll(app.getMapOriginPlurals());
                        }
                    }
                }
//                originDevice.setAllStrings(originDevice.getAllStrings().stream().filter(distinctByKey(stringRes -> stringRes.getName() + stringRes.getValue())).collect(Collectors.toList()));
//                originDevice.setAllArrays(originDevice.getAllArrays().stream().filter(distinctByKey(arrayRes -> arrayRes.getName() + arrayRes.getArrayType() + arrayRes.getItems().size())).collect(Collectors.toList()));
//                originDevice.setAllPlurals(originDevice.getAllPlurals().stream().filter(distinctByKey(pluralRes -> pluralRes.getName() + pluralRes.getItems().size())).collect(Collectors.toList()));

                originDevice.setApps(apps);
            }


            return originDevice;
        }
        return null;
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
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
