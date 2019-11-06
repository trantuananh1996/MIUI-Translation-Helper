package com.anhtt.miui.translation.helper.model;

import com.anhtt.miui.translation.helper.MainGUI;
import com.anhtt.miui.translation.helper.SearchOptions;
import com.anhtt.miui.translation.helper.model.res.ApplicationStringRes;
import com.anhtt.miui.translation.helper.model.res.ArrayRes;
import com.anhtt.miui.translation.helper.model.res.PluralRes;
import com.anhtt.miui.translation.helper.model.res.StringRes;
import com.sun.istack.internal.Nullable;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Application {
    private String name;
    private File translation;
    Map<String, StringRes> mapOriginStrings = new HashMap<>();
    Map<String, StringRes> mapTranslatedStrings = new HashMap<>();//String đã dịch
    Map<String, StringRes> mapUnTranslatedStrings = new HashMap<>();//String chưa dịch
    Map<String, StringRes> mapWrongTranslatedOriginStrings = new HashMap<>();//String gốc bị dịch sai
    Map<String, StringRes> mapWrongTranslatedStrings = new HashMap<>();//String bị dịch sai
    Map<String, StringRes> mapMightNotTranslatedStrings = new HashMap<>();//String giống nhau giữa gốc và dịch, có thể bỏ
    Map<String, StringRes> mapCanNotTranslateStrings = new HashMap<>();//String không thể dịch
    Map<String, StringRes> mapOriginEqualTranslatedStrings = new HashMap<>();//Dịch giống gốc
    Map<String, StringRes> mapTranslatedFromOtherString = new HashMap<>();//String đã dịch

    public Map<String, StringRes> getMapOriginStrings() {
        return mapOriginStrings;
    }

    public Map<String, StringRes> getMapTranslatedStrings() {
        return mapTranslatedStrings;
    }

    public Map<String, StringRes> getMapUnTranslatedStrings() {
        return mapUnTranslatedStrings;
    }

    public Map<String, StringRes> getMapWrongTranslatedOriginStrings() {
        return mapWrongTranslatedOriginStrings;
    }

    public Map<String, StringRes> getMapWrongTranslatedStrings() {
        return mapWrongTranslatedStrings;
    }

    public Map<String, StringRes> getMapMightNotTranslatedStrings() {
        return mapMightNotTranslatedStrings;
    }

    public Map<String, StringRes> getMapCanNotTranslateStrings() {
        return mapCanNotTranslateStrings;
    }

    public Map<String, StringRes> getMapOriginEqualTranslatedStrings() {
        return mapOriginEqualTranslatedStrings;
    }

    public Map<String, StringRes> getMapTranslatedFromOtherString() {
        return mapTranslatedFromOtherString;
    }
//
//    private List<StringRes> originStrings = new ArrayList<>();//String gốc của app
//    private List<StringRes> translatedStrings = new ArrayList<>();//String đã dịch
//    private List<StringRes> unTranslatedStrings = new ArrayList<>();//String chưa dịch
//    private List<StringRes> wrongTranslatedOriginStrings = new ArrayList<>();//String gốc bị dịch sai
//    private List<StringRes> wrongTranslatedStrings = new ArrayList<>();//String bị dịch sai
//    private List<StringRes> mightNotTranslatedStrings = new ArrayList<>();//String giống nhau giữa gốc và dịch, có thể bỏ
//    private List<StringRes> canNotTranslateStrings = new ArrayList<>();//String không thể dịch
//    private List<StringRes> originEqualTranslatedStrings = new ArrayList<>();//Dịch giống gốc
//    private List<StringRes> translatedFromOtherString = new ArrayList<>();//String đã dịch
//
//    public List<StringRes> getOriginStrings() {
//        return originStrings;
//    }
//
//    public List<StringRes> getTranslatedStrings() {
//        return translatedStrings;
//    }
//
//    public List<StringRes> getUnTranslatedStrings() {
//        return unTranslatedStrings;
//    }
//
//    public List<StringRes> getWrongTranslatedOriginStrings() {
//        return wrongTranslatedOriginStrings;
//    }
//
//    public List<StringRes> getWrongTranslatedStrings() {
//        return wrongTranslatedStrings;
//    }
//
//    public List<StringRes> getMightNotTranslatedStrings() {
//        return mightNotTranslatedStrings;
//    }
//
//    public List<StringRes> getCanNotTranslateStrings() {
//        return canNotTranslateStrings;
//    }
//
//    public List<StringRes> getOriginEqualTranslatedStrings() {
//        return originEqualTranslatedStrings;
//    }
//
//    public List<StringRes> getTranslatedFromOtherString() {
//        return translatedFromOtherString;
//    }

    private Map<String, ArrayRes> mapOriginArrays = new HashMap<>();
    private Map<String, ArrayRes> mapTranslatedArrays = new HashMap<>();//Array đã dịch
    private Map<String, ArrayRes> mapUnTranslatedArrays = new HashMap<>();//Array chưa dịch
    private Map<String, ArrayRes> mapWrongTranslatedOriginArrays = new HashMap<>();//Array gốc bị dịch sai
    private Map<String, ArrayRes> mapWrongTranslatedArrays = new HashMap<>();//Array bị dịch sai
    private Map<String, ArrayRes> mapMightNotTranslatedArrays = new HashMap<>();//Array giống nhau giữa gốc và dịch, có thể bỏ
    private Map<String, ArrayRes> mapCanNotTranslateArrays = new HashMap<>();//Array không thể dịch
    private Map<String, ArrayRes> mapOriginEqualTranslatedArrays = new HashMap<>();//Dịch giống gốc
    private Map<String, ArrayRes> mapTranslatedFromOtherArray = new HashMap<>();//Dịch giống gốc

    public Map<String, ArrayRes> getMapOriginArrays() {
        return mapOriginArrays;
    }

    public Map<String, ArrayRes> getMapTranslatedArrays() {
        return mapTranslatedArrays;
    }

    public Map<String, ArrayRes> getMapUnTranslatedArrays() {
        return mapUnTranslatedArrays;
    }

    public Map<String, ArrayRes> getMapWrongTranslatedOriginArrays() {
        return mapWrongTranslatedOriginArrays;
    }

    public Map<String, ArrayRes> getMapWrongTranslatedArrays() {
        return mapWrongTranslatedArrays;
    }

    public Map<String, ArrayRes> getMapMightNotTranslatedArrays() {
        return mapMightNotTranslatedArrays;
    }

    public Map<String, ArrayRes> getMapCanNotTranslateArrays() {
        return mapCanNotTranslateArrays;
    }

    public Map<String, ArrayRes> getMapOriginEqualTranslatedArrays() {
        return mapOriginEqualTranslatedArrays;
    }

    public Map<String, ArrayRes> getMapTranslatedFromOtherArray() {
        return mapTranslatedFromOtherArray;
    }

    private Map<String, PluralRes> mapOriginPlurals = new HashMap<>();
    private Map<String, PluralRes> mapTranslatedPlurals = new HashMap<>();//Plural đã dịch
    private Map<String, PluralRes> mapUnTranslatedPlurals = new HashMap<>();//Plural chưa dịch
    private Map<String, PluralRes> mapWrongTranslatedOriginPlurals = new HashMap<>();//Plural gốc bị dịch sai
    private Map<String, PluralRes> mapWrongTranslatedPlurals = new HashMap<>();//Plural bị dịch sai
    private Map<String, PluralRes> mapMightNotTranslatedPlurals = new HashMap<>();//Plural giống nhau giữa gốc và dịch, có thể bỏ
    private Map<String, PluralRes> mapCanNotTranslatePlurals = new HashMap<>();//Plural không thể dịch
    private Map<String, PluralRes> mapOriginEqualTranslatedPlurals = new HashMap<>();//Dịch giống gốc
    private Map<String, PluralRes> mapTranslatedFromOtherPlural = new HashMap<>();//Dịch giống gốc
    private List<StringRes> duplicateString;

    public Map<String, PluralRes> getMapOriginPlurals() {
        return mapOriginPlurals;
    }

    public Map<String, PluralRes> getMapTranslatedPlurals() {
        return mapTranslatedPlurals;
    }

    public Map<String, PluralRes> getMapUnTranslatedPlurals() {
        return mapUnTranslatedPlurals;
    }

    public Map<String, PluralRes> getMapWrongTranslatedOriginPlurals() {
        return mapWrongTranslatedOriginPlurals;
    }

    public Map<String, PluralRes> getMapWrongTranslatedPlurals() {
        return mapWrongTranslatedPlurals;
    }

    public Map<String, PluralRes> getMapMightNotTranslatedPlurals() {
        return mapMightNotTranslatedPlurals;
    }

    public Map<String, PluralRes> getMapCanNotTranslatePlurals() {
        return mapCanNotTranslatePlurals;
    }

    public Map<String, PluralRes> getMapOriginEqualTranslatedPlurals() {
        return mapOriginEqualTranslatedPlurals;
    }

    public Map<String, PluralRes> getMapTranslatedFromOtherPlural() {
        return mapTranslatedFromOtherPlural;
    }

    private Application(String name, File translation) {
        this.name = name;
        this.translation = translation;
    }

    @Nullable
    private static String getDeviceName(String path) {
        String name = "";
        try {
            File file = new File(path);
            name = file.getName();
            if (!name.contains("extras") && !file.isHidden())
                return name;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Nullable
    public static Application create(String path, boolean isApplyFilter) {
        String deviceName = getDeviceName(path);
        if (deviceName == null || deviceName.isEmpty()) return null;

        File file = new File(path);

        if (file.exists()) {
            Application app = new Application(deviceName, file);
            try {
                if (SearchOptions.searchString) app.createStringList(isApplyFilter);
                if (SearchOptions.searchArray) app.createArrayList(isApplyFilter);
                if (SearchOptions.searchPlural) app.createPluralList(isApplyFilter);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return app;
        }
        return null;
    }

    private void createPluralList(boolean isApplyFilter) throws ParserConfigurationException, IOException, SAXException {
//      System.out.println("createPluralList() called with: isApplyFilter = [" + isApplyFilter + "]");
        mapOriginPlurals = new HashMap<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        File valueFolder = new File(translation.getAbsolutePath() + "\\res\\values");
        if (!valueFolder.exists()) valueFolder = new File(translation.getAbsolutePath() + "\\res\\values-vi");
        if (!valueFolder.exists()) valueFolder = new File(translation.getAbsolutePath() + "\\res\\values-vi-rVN");
        if (!valueFolder.exists()) return;
        File file = new File(valueFolder.getAbsolutePath() + "\\plurals.xml");
        if (!file.exists()) return;
        Document doc = docBuilder.parse(file);
        NodeList list = doc.getElementsByTagName("plurals");
        for (int i = 0; i < list.getLength(); i++) {
//            System.out.println("Process" + i);
            PluralRes stringRes = PluralRes.create((Element) list.item(i), isApplyFilter);
            if (stringRes != null) {
//                System.out.println("Plural "+name+" - " + stringRes.getName());
                mapOriginPlurals.put(stringRes.getName(), stringRes);
//                originPlurals.add(stringRes);
            }
        }
    }

    private void createStringList(boolean isApplyFilter) throws Exception {
//        originStrings = new ArrayList<>();
        mapOriginStrings = new HashMap<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        File valueFolder = new File(translation.getAbsolutePath() + "\\res\\values");
        if (!valueFolder.exists()) valueFolder = new File(translation.getAbsolutePath() + "\\res\\values-vi");
        if (!valueFolder.exists()) valueFolder = new File(translation.getAbsolutePath() + "\\res\\values-vi-rVN");
        if (!valueFolder.exists()) return;
        File file = new File(valueFolder.getAbsolutePath() + "\\strings.xml");
        if (!file.exists()) return;
        Document doc = docBuilder.parse(file);
        NodeList list = doc.getElementsByTagName("string");
        for (int i = 0; i < list.getLength(); i++) {
            StringRes stringRes = StringRes.create(list.item(i), isApplyFilter);
            if (stringRes != null) {
                mapOriginStrings.put(stringRes.getName(), stringRes);
//                originStrings.add(stringRes);
            }
        }
    }

    private void createArrayList(boolean isTranslatedDevice) throws Exception {
        mapOriginArrays = new HashMap<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        File valueFolder = new File(translation.getAbsolutePath() + "\\res\\values");
        if (!valueFolder.exists()) valueFolder = new File(translation.getAbsolutePath() + "\\res\\values-vi");
        if (!valueFolder.exists()) valueFolder = new File(translation.getAbsolutePath() + "\\res\\values-vi-rVN");
        if (!valueFolder.exists()) return;
        File file = new File(valueFolder.getAbsolutePath() + "\\arrays.xml");
        if (!file.exists()) return;
        Document doc = docBuilder.parse(file);
        NodeList list = doc.getElementsByTagName("string-array");
        for (int i = 0; i < list.getLength(); i++) {
            ArrayRes stringRes = ArrayRes.create((Element) list.item(i), isTranslatedDevice);
            if (stringRes != null) {
                mapOriginArrays.put(stringRes.getName(), stringRes);
//                originArrays.add(stringRes);
            }
        }
    }

    public void compare(SourceDevice sourceDevice, Application specificedApp, Application translatedApp, TargetDevice transDevices) {
//        if (translatedApp == null) {
//            if (SearchOptions.searchString) {
//                mapOriginStrings.putAll(mapUnTranslatedStrings);
////                unTranslatedStrings.addAll(originStrings);
//            }
//            if (SearchOptions.searchArray) {
//                mapUnTranslatedArrays.putAll(mapOriginArrays);
////                unTranslatedArrays.addAll(originArrays);
//            }
//            if (SearchOptions.searchPlural) {
//                mapUnTranslatedPlurals.putAll(mapOriginPlurals);
//            }
//            return;
//        }
        if (SearchOptions.searchString)
            filterString(specificedApp, translatedApp, transDevices);

        if (SearchOptions.searchArray)
            filterArray(specificedApp, translatedApp, transDevices);

        if (SearchOptions.searchPlural)
            filterPlural(specificedApp, translatedApp, transDevices);

    }

    private void filterPlural(Application specificedApp, Application translatedApp, TargetDevice transDevices) {
        mapOriginPlurals.forEach((key, originPlural) -> {
            boolean canNotTranslate = MainGUI.unTranslateables.get(getName() + "plurals.xml" + originPlural.getName()) != null;

//            boolean isIgnored = MainGUI.autoIgnoredList.stream().anyMatch(unTranslateable -> {
//                return unTranslateable.getApplication().equals(getName())
//                        && unTranslateable.getName().equals(originPlural.getName());
//            });
            PluralRes translatedPlural = findTranslatedPlural(originPlural, translatedApp, transDevices, false);
            PluralRes specificPlural = findTranslatedPlural(originPlural, specificedApp, transDevices, true);

            if (canNotTranslate) {
                //Nếu là string không thể dịch, mà đã có dịch rồi thì add vào
                if (translatedPlural != null) mapCanNotTranslatePlurals.put(key, translatedPlural);
            } else {
                if (translatedPlural == null) {//Chưa có dịch cho string này
                    if (specificPlural == null)
                        mapUnTranslatedPlurals.put(key, originPlural);
                } else {//Đã có dịch
                    boolean isWrongTranslation = originPlural.isWrongFormat(translatedPlural);
                    if (isWrongTranslation) {//Nếu là dịch sai
                        if (specificPlural != null) {//Dịch sai mà đã có dịch riêng biệt
                            boolean isWrongSpecificTranslation = originPlural.isWrongFormat(specificPlural);
                            if (isWrongSpecificTranslation) {//Dịch riêng biệt cũng sai nốt
                                mapWrongTranslatedOriginPlurals.put(key, originPlural);
                                mapWrongTranslatedPlurals.put(key, translatedPlural);
                            }
                        } else {//Dịch sai mà cũng ko có cả dịch riêng luôn
                            mapWrongTranslatedOriginPlurals.put(key, originPlural);
                            mapWrongTranslatedPlurals.put(key, translatedPlural);
                        }
                    } else if (originPlural.getItems().equals(translatedPlural.getItems())
                            && originPlural.getName().equals(translatedPlural.getName())) {
                        //Có dịch nhưng mà dịch cũng như ko vì nó giống nhau
                        mapOriginEqualTranslatedPlurals.put(key, originPlural);
                    } else {
                        //Dịch rồi, mà lại còn đúng nữa thì làm gì ở đây nữa =))
                    }
                }
            }
        });
    }

    private void filterArray(Application specificedApp, @Nullable Application translatedApp, TargetDevice transDevices) {
        mapOriginArrays.forEach((key, originArray) -> {
            boolean canNotTranslate = MainGUI.unTranslateables.get(getName() + "arrays.xml" + originArray.getName()) != null;
//            boolean isIgnored = MainGUI.autoIgnoredList.stream().anyMatch(unTranslateable -> {
//                return unTranslateable.getApplication().equals(getName())
//                        && unTranslateable.getName().equals(originArray.getName());
//            });
            ArrayRes translatedArray = findTranslatedArray(originArray, translatedApp, transDevices, false);
            ArrayRes specificArray = findTranslatedArray(originArray, specificedApp, transDevices, true);

            if (canNotTranslate) {
                //Nếu là string không thể dịch, mà đã có dịch rồi thì add vào
                if (translatedArray != null) mapCanNotTranslateArrays.put(key, translatedArray);
            } else {
                if (translatedArray == null) {//Chưa có dịch cho string này
                    if (specificArray == null)
                        mapUnTranslatedArrays.put(key, originArray);
                } else {//Đã có dịch
                    boolean isWrongTranslation = originArray.isWrongFormat(translatedArray);
                    if (isWrongTranslation) {//Nếu là dịch sai
                        if (specificArray != null) {//Dịch sai mà đã có dịch riêng biệt
                            boolean isWrongSpecificTranslation = originArray.isWrongFormat(specificArray);
                            if (isWrongSpecificTranslation) {//Dịch riêng biệt cũng sai nốt
                                mapWrongTranslatedOriginArrays.put(key, originArray);
                                mapWrongTranslatedArrays.put(key, translatedArray);
                            }
                        } else {//Dịch sai mà cũng ko có cả dịch riêng luôn
                            mapWrongTranslatedOriginArrays.put(key, originArray);
                            mapWrongTranslatedArrays.put(key, translatedArray);
                        }
                    } else if (originArray.getItems().equals(translatedArray.getItems())
                            && originArray.getName().equals(translatedArray.getName())) {
                        //Có dịch nhưng mà dịch cũng như ko vì nó giống nhau
                        mapOriginEqualTranslatedArrays.put(key, originArray);
                    } else {
                        //Dịch rồi, mà lại còn đúng nữa thì làm gì ở đây nữa =))
                    }
                }
            }
        });


    }

    private void filterString(Application specificedApp, Application translatedApp, TargetDevice transDevices) {
        mapOriginStrings.forEach((key, originString) -> {
            boolean canNotTranslate = MainGUI.unTranslateables.get(getName() + "strings.xml" + originString.getName()) != null;

//            boolean isIgnored = MainGUI.autoIgnoredList.stream().anyMatch(unTranslateable -> {
//                return unTranslateable.getApplication().equals(getName())
//                        && unTranslateable.getName().equals(originString.getName());
//            });
            StringRes translatedString = findTranslatedString(originString, translatedApp, transDevices, false);
            StringRes specificString = findTranslatedString(originString, specificedApp, transDevices, true);

            if (canNotTranslate) {
                //Nếu là string không thể dịch, mà đã có dịch rồi thì add vào
                if (translatedString != null) mapCanNotTranslateStrings.put(key, translatedString);
            } else {
                if (translatedString == null) {//Chưa có dịch cho string này
                    if (specificString == null)
                        mapUnTranslatedStrings.put(key, originString);
                } else {//Đã có dịch
                    boolean isWrongTranslation = originString.isWrongFormat(translatedString);
                    if (isWrongTranslation) {//Nếu là dịch sai
                        if (specificString != null) {//Dịch sai mà đã có dịch riêng biệt
                            boolean isWrongSpecificTranslation = originString.isWrongFormat(specificString);
                            if (isWrongSpecificTranslation) {//Dịch riêng biệt cũng sai nốt
                                mapWrongTranslatedOriginStrings.put(key, originString);
                                mapWrongTranslatedStrings.put(key, translatedString);
                            }
                        } else {//Dịch sai mà cũng ko có cả dịch riêng luôn
                            mapWrongTranslatedOriginStrings.put(key, originString);
                            mapWrongTranslatedStrings.put(key, translatedString);
                        }
                    } else if (originString.getValue().equals(translatedString.getValue())
                            && originString.getName().equals(translatedString.getName())) {
                        //Có dịch nhưng mà dịch cũng như ko vì nó giống nhau
                        mapOriginEqualTranslatedStrings.put(key, originString);
                    } else {
                        //Dịch rồi, mà lại còn đúng nữa thì làm gì ở đây nữa =))
                    }
                }
            }
        });
    }

    private PluralRes findTranslatedPlural(PluralRes originPlural, Application translatedApp, TargetDevice transDevices, boolean isSpecific) {
//        if (translatedApp == null) return null;
//        Map<String, PluralRes> mappluralToStream = !isSpecific && SearchOptions.deepFilterForUnTranslated ? transDevices.getAllPlurals() : translatedApp.getOriginPlurals();
//        Optional<PluralRes> hasTranslated = pluralToStream.stream().filter(stringRes -> arrayRes.getName().equals(stringRes.getName())).findFirst();
//        return hasTranslated.orElse(null);


        if (translatedApp == null) return null;
//        Map<String, PluralRes> mapstringToStream = !isSpecific && SearchOptions.deepFilterForUnTranslated ? transDevices.getAllPlurals() : translatedApp.getOriginPlural();
        PluralRes hasTranslated = translatedApp.getMapOriginPlurals().get(originPlural.getName());

        PluralRes hasTranslatedInAll = transDevices.getMapAllPlurals().get(originPlural.getName());
        PluralRes result;
        if (!isSpecific)
            result = hasTranslated != null ? hasTranslated : hasTranslatedInAll;
        else result = hasTranslated;

        if (!isSpecific && hasTranslated == null && hasTranslatedInAll != null) {
            if (!hasTranslatedInAll.getName().contains("app_name") && !originPlural.isWrongFormat(hasTranslatedInAll)) {
                UnTranslateable unTranslateable = MainGUI.unTranslateables.get(name + "plurals.xml" + hasTranslatedInAll.getName());
                if (unTranslateable == null)
                    mapTranslatedFromOtherPlural.put(originPlural.getName(), hasTranslatedInAll);
            }
        }
        return result;
    }

    private ArrayRes findTranslatedArray(ArrayRes originArray, Application translatedApp, TargetDevice transDevices, boolean isSpecific) {
        if (translatedApp == null) return null;
//        List<ArrayRes> stringToStream = !isSpecific && SearchOptions.deepFilterForUnTranslated ? transDevices.getAllArrays() : translatedApp.getOriginArray();
        ArrayRes hasTranslated = translatedApp.getMapOriginArrays().get(originArray.getName());

        ArrayRes hasTranslatedInAll = transDevices.getMapAllArrays().get(originArray.getName());
        ArrayRes result;
        if (!isSpecific)
            result = hasTranslated != null ? hasTranslated : hasTranslatedInAll;
        else result = hasTranslated;

        if (!isSpecific && hasTranslated == null && hasTranslatedInAll != null) {
            if (!hasTranslatedInAll.getName().contains("app_name") && !originArray.isWrongFormat(hasTranslatedInAll)) {
                UnTranslateable unTranslateable = MainGUI.unTranslateables.get(name + "arrays.xml" + hasTranslatedInAll.getName());
                if (unTranslateable == null)
                    mapTranslatedFromOtherArray.put(originArray.getName(), hasTranslatedInAll);
            }
        }
        return result;
    }


    @Nullable
    private StringRes findTranslatedString(StringRes resourceToFind, Application translatedApp, TargetDevice transDevices, boolean isSpecific) {
        if (translatedApp == null) return null;
//        List<StringRes> stringToStream = !isSpecific && SearchOptions.deepFilterForUnTranslated ? transDevices.getAllStrings() : translatedApp.getOriginString();

        StringRes hasTranslated = translatedApp.getMapOriginStrings().get(resourceToFind.getName());

        ApplicationStringRes hasTranslatedInAll = transDevices.getMapAllStrings().get(resourceToFind.getName());
        StringRes result;
        if (!isSpecific)
            result = hasTranslated != null ? hasTranslated : hasTranslatedInAll;
        else result = hasTranslated;

        if (!isSpecific && hasTranslated == null && hasTranslatedInAll != null) {
            if (!hasTranslatedInAll.getName().contains("app_name") && !resourceToFind.isWrongFormat(hasTranslatedInAll)) {
                UnTranslateable unTranslateable = MainGUI.unTranslateables.get(name + "strings.xml" + hasTranslatedInAll.getName());
                if (unTranslateable == null) {
                    if (hasTranslatedInAll.getUntranslatedString().equals(resourceToFind.getValue())) {
                        mapTranslatedFromOtherString.put(resourceToFind.getName(), hasTranslatedInAll);
                    } else result = null;
                }
            }
        }
        return result;
    }

//    public List<StringRes> getOriginString() {
//        return originStrings;
//    }

    public String getName() {
        return name;
    }

    public File getTranslation() {
        return translation;
    }

    public void setDuplicateString(List<StringRes> duplicateString) {
        this.duplicateString = duplicateString;
    }

    public List<StringRes> getDuplicateString() {
        return duplicateString;
    }

    @Override
    public String toString() {
        return name;
    }
}
