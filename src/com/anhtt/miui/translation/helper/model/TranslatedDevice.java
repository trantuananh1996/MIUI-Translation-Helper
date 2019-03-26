package com.anhtt.miui.translation.helper.model;

import com.anhtt.miui.translation.helper.model.res.ArrayRes;
import com.anhtt.miui.translation.helper.model.res.PluralRes;
import com.anhtt.miui.translation.helper.model.res.StringRes;
import com.sun.istack.internal.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TranslatedDevice {
    private String name;
    private File translation;
    private List<Application> apps;
    private List<StringRes> allStrings = new ArrayList<>();
    private List<ArrayRes> allArrays = new ArrayList<>();
    private List<PluralRes> allPlurals = new ArrayList<>();

    public List<PluralRes> getAllPlurals() {
        return allPlurals;
    }

    public void setAllPlurals(List<PluralRes> allPlurals) {
        this.allPlurals = allPlurals;
    }

    public List<StringRes> getAllStrings() {
        return allStrings;
    }

    public void setAllStrings(List<StringRes> allStrings) {
        this.allStrings = allStrings;
    }

    public List<ArrayRes> getAllArrays() {
        return allArrays;
    }

    public void setAllArrays(List<ArrayRes> allArrays) {
        this.allArrays = allArrays;
    }

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
            if (!name.contains("Diff") && !name.contains("extras") && !name.contains("stable") && !name.contains(".") && !file.isHidden())
                return name;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Nullable
    public static TranslatedDevice create(String path, boolean isApplyFilter) {
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
                        Application app = Application.create(appFolder.getAbsolutePath(), isApplyFilter);
                        if (app != null) {
                            apps.add(app);
                            originDevice.getAllStrings().addAll(app.getOriginString());
                            originDevice.getAllArrays().addAll(app.getOriginArrays());
                        }
                    }
                }
                originDevice.setAllStrings(originDevice.getAllStrings().stream().filter(distinctByKey(stringRes -> stringRes.getName() + stringRes.getValue())).collect(Collectors.toList()));
                originDevice.setAllArrays(originDevice.getAllArrays().stream().filter(distinctByKey(ArrayRes::getName)).collect(Collectors.toList()));
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
