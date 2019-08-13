package com.anhtt.miui.translation.helper.model;

import com.anhtt.miui.translation.helper.MainGUI;
import com.anhtt.miui.translation.helper.SearchOptions;
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
    Map<String, String> mapOriginStrings = new HashMap<>();
    Map<String, String> maptranslatedStrings = new HashMap<>();//String đã dịch
    Map<String, String> mapunTranslatedStrings = new HashMap<>();//String chưa dịch
    Map<String, String> mapwrongTranslatedOriginStrings = new HashMap<>();//String gốc bị dịch sai
    Map<String, String> mapwrongTranslatedStrings = new HashMap<>();//String bị dịch sai
    Map<String, String> mapmightNotTranslatedStrings = new HashMap<>();//String giống nhau giữa gốc và dịch, có thể bỏ
    Map<String, String> mapcanNotTranslateStrings = new HashMap<>();//String không thể dịch
    Map<String, String> maporiginEqualTranslatedStrings = new HashMap<>();//Dịch giống gốc
    Map<String, String> maptranslatedFromOtherString = new HashMap<>();//String đã dịch

    private List<StringRes> originStrings = new ArrayList<>();//String gốc của app
    private List<StringRes> translatedStrings = new ArrayList<>();//String đã dịch
    private List<StringRes> unTranslatedStrings = new ArrayList<>();//String chưa dịch
    private List<StringRes> wrongTranslatedOriginStrings = new ArrayList<>();//String gốc bị dịch sai
    private List<StringRes> wrongTranslatedStrings = new ArrayList<>();//String bị dịch sai
    private List<StringRes> mightNotTranslatedStrings = new ArrayList<>();//String giống nhau giữa gốc và dịch, có thể bỏ
    private List<StringRes> canNotTranslateStrings = new ArrayList<>();//String không thể dịch
    private List<StringRes> originEqualTranslatedStrings = new ArrayList<>();//Dịch giống gốc
    private List<StringRes> translatedFromOtherString = new ArrayList<>();//String đã dịch


    private List<ArrayRes> originArrays = new ArrayList<>();
    private List<ArrayRes> translatedArrays = new ArrayList<>();//Array đã dịch
    private List<ArrayRes> unTranslatedArrays = new ArrayList<>();//Array chưa dịch
    private List<ArrayRes> wrongTranslatedOriginArrays = new ArrayList<>();//Array gốc bị dịch sai
    private List<ArrayRes> wrongTranslatedArrays = new ArrayList<>();//Array bị dịch sai
    private List<ArrayRes> mightNotTranslatedArrays = new ArrayList<>();//Array giống nhau giữa gốc và dịch, có thể bỏ
    private List<ArrayRes> canNotTranslateArrays = new ArrayList<>();//Array không thể dịch
    private List<ArrayRes> originEqualTranslatedArrays = new ArrayList<>();//Dịch giống gốc
    private List<ArrayRes> translatedFromOtherArray = new ArrayList<>();//Dịch giống gốc


    private List<PluralRes> originPlurals = new ArrayList<>();
    private List<PluralRes> translatedPlurals = new ArrayList<>();//Plural đã dịch
    private List<PluralRes> unTranslatedPlurals = new ArrayList<>();//Plural chưa dịch
    private List<PluralRes> wrongTranslatedOriginPlurals = new ArrayList<>();//Plural gốc bị dịch sai
    private List<PluralRes> wrongTranslatedPlurals = new ArrayList<>();//Plural bị dịch sai
    private List<PluralRes> mightNotTranslatedPlurals = new ArrayList<>();//Plural giống nhau giữa gốc và dịch, có thể bỏ
    private List<PluralRes> canNotTranslatePlurals = new ArrayList<>();//Plural không thể dịch
    private List<PluralRes> originEqualTranslatedPlurals = new ArrayList<>();//Dịch giống gốc
    private List<PluralRes> translatedFromOtherPlural = new ArrayList<>();//Dịch giống gốc
    private List<StringRes> duplicateString;

    public List<StringRes> getTranslatedFromOtherString() {
        return translatedFromOtherString;
    }

    public List<ArrayRes> getTranslatedFromOtherArray() {
        return translatedFromOtherArray;
    }

    public List<PluralRes> getTranslatedFromOtherPlural() {
        return translatedFromOtherPlural;
    }

    List<PluralRes> getOriginPlurals() {
        return originPlurals;
    }

    public List<PluralRes> getTranslatedPlurals() {
        return translatedPlurals;
    }

    public List<PluralRes> getUnTranslatedPlurals() {
        return unTranslatedPlurals;
    }

    public List<PluralRes> getWrongTranslatedOriginPlurals() {
        return wrongTranslatedOriginPlurals;
    }

    public List<PluralRes> getWrongTranslatedPlurals() {
        return wrongTranslatedPlurals;
    }

    public List<PluralRes> getMightNotTranslatedPlurals() {
        return mightNotTranslatedPlurals;
    }

    public List<PluralRes> getCanNotTranslatePlurals() {
        return canNotTranslatePlurals;
    }

    public List<PluralRes> getOriginEqualTranslatedPlurals() {
        return originEqualTranslatedPlurals;
    }

    public List<StringRes> getOriginStrings() {
        return originStrings;
    }

    public List<ArrayRes> getTranslatedArrays() {
        return translatedArrays;
    }

    public List<ArrayRes> getUnTranslatedArrays() {
        return unTranslatedArrays;
    }

    public List<ArrayRes> getWrongTranslatedOriginArrays() {
        return wrongTranslatedOriginArrays;
    }

    public List<ArrayRes> getWrongTranslatedArrays() {
        return wrongTranslatedArrays;
    }

    public List<ArrayRes> getMightNotTranslatedArrays() {
        return mightNotTranslatedArrays;
    }

    public List<ArrayRes> getCanNotTranslateArrays() {
        return canNotTranslateArrays;
    }

    public List<ArrayRes> getOriginEqualTranslatedArrays() {
        return originEqualTranslatedArrays;
    }

    public List<StringRes> getTranslatedStrings() {
        return translatedStrings;
    }

    public List<StringRes> getUnTranslatedStrings() {
        return unTranslatedStrings;
    }

    public List<StringRes> getWrongTranslatedOriginStrings() {
        return wrongTranslatedOriginStrings;
    }

    public List<StringRes> getWrongTranslatedStrings() {
        return wrongTranslatedStrings;
    }

    public List<StringRes> getMightNotTranslatedStrings() {
        return mightNotTranslatedStrings;
    }

    public List<StringRes> getCanNotTranslateStrings() {
        return canNotTranslateStrings;
    }

    public List<StringRes> getOriginEqualTranslatedStrings() {
        return originEqualTranslatedStrings;
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
        originPlurals = new ArrayList<>();
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
                originPlurals.add(stringRes);
            }
        }
    }

    private void createStringList(boolean isApplyFilter) throws Exception {
        originStrings = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        File valueFolder = new File(translation.getAbsolutePath() + "\\res\\values");
        if (!valueFolder.exists()) valueFolder = new File(translation.getAbsolutePath() + "\\res\\values-vi");
        if (!valueFolder.exists()) valueFolder = new File(translation.getAbsolutePath() + "\\res\\values-vi-rVN");
        if (!valueFolder.exists()) return;
        File file = new File(valueFolder.getAbsolutePath() + "\\strings.xml");
        Document doc = docBuilder.parse(file);
        NodeList list = doc.getElementsByTagName("string");
        for (int i = 0; i < list.getLength(); i++) {
            StringRes stringRes = StringRes.create(list.item(i), isApplyFilter);
            if (stringRes != null) originStrings.add(stringRes);
        }
    }

    private void createArrayList(boolean isTranslatedDevice) throws Exception {
        originArrays = new ArrayList<>();
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
            if (stringRes != null) originArrays.add(stringRes);
        }
    }

    public void compare(SourceDevice sourceDevice, Application specificedApp, Application translatedApp, TargetDevice transDevices) {
        if (translatedApp == null) {
            if (SearchOptions.searchString) unTranslatedStrings.addAll(originStrings);
            if (SearchOptions.searchArray) unTranslatedArrays.addAll(originArrays);
            if (SearchOptions.searchPlural) unTranslatedPlurals.addAll(originPlurals);
            return;
        }
        if (SearchOptions.searchString)
            filterString(specificedApp, translatedApp, transDevices);

        if (SearchOptions.searchArray)
            filterArray(specificedApp, translatedApp, transDevices);

        if (SearchOptions.searchPlural)
            filterPlural(specificedApp, translatedApp, transDevices);

    }

    private void filterPlural(Application specificedApp, Application translatedApp, TargetDevice transDevices) {
        originPlurals.forEach(originPlural -> {
            boolean canNotTranslate = MainGUI.unTranslateables.stream().anyMatch(unTranslateable -> {
                return unTranslateable.getApplication().equals(getName())
                        && unTranslateable.getName().equals(originPlural.getName());
            });
//            boolean isIgnored = MainGUI.autoIgnoredList.stream().anyMatch(unTranslateable -> {
//                return unTranslateable.getApplication().equals(getName())
//                        && unTranslateable.getName().equals(originPlural.getName());
//            });
            PluralRes translatedPlural = findTranslatedPlural(originPlural, translatedApp, transDevices, false);
            PluralRes specificPlural = findTranslatedPlural(originPlural, specificedApp, transDevices, true);

            if (canNotTranslate) {
                //Nếu là string không thể dịch, mà đã có dịch rồi thì add vào
                if (translatedPlural != null) canNotTranslatePlurals.add(translatedPlural);
            } else {
                if (translatedPlural == null) {//Chưa có dịch cho string này
                    if (specificPlural == null)
                        unTranslatedPlurals.add(originPlural);
                } else {//Đã có dịch
                    boolean isWrongTranslation = originPlural.isWrongFormat(translatedPlural);
                    if (isWrongTranslation) {//Nếu là dịch sai
                        if (specificPlural != null) {//Dịch sai mà đã có dịch riêng biệt
                            boolean isWrongSpecificTranslation = originPlural.isWrongFormat(specificPlural);
                            if (isWrongSpecificTranslation) {//Dịch riêng biệt cũng sai nốt
                                wrongTranslatedOriginPlurals.add(originPlural);
                                wrongTranslatedPlurals.add(translatedPlural);
                            }
                        } else {//Dịch sai mà cũng ko có cả dịch riêng luôn
                            wrongTranslatedOriginPlurals.add(originPlural);
                            wrongTranslatedPlurals.add(translatedPlural);
                        }
                    } else if (originPlural.getItems().equals(translatedPlural.getItems())
                            && originPlural.getName().equals(translatedPlural.getName())) {
                        //Có dịch nhưng mà dịch cũng như ko vì nó giống nhau
                        originEqualTranslatedPlurals.add(originPlural);
                    } else {
                        //Dịch rồi, mà lại còn đúng nữa thì làm gì ở đây nữa =))
                    }
                }
            }
        });
    }

    private void filterArray(Application specificedApp, Application translatedApp, TargetDevice transDevices) {
        originArrays.forEach(originArray -> {
            boolean canNotTranslate = MainGUI.unTranslateables.stream().anyMatch(unTranslateable -> {
                return unTranslateable.getApplication().equals(getName())
                        && unTranslateable.getName().equals(originArray.getName());
            });
//            boolean isIgnored = MainGUI.autoIgnoredList.stream().anyMatch(unTranslateable -> {
//                return unTranslateable.getApplication().equals(getName())
//                        && unTranslateable.getName().equals(originArray.getName());
//            });
            ArrayRes translatedArray = findTranslatedArray(originArray, translatedApp, transDevices, false);
            ArrayRes specificArray = findTranslatedArray(originArray, specificedApp, transDevices, true);

            if (canNotTranslate) {
                //Nếu là string không thể dịch, mà đã có dịch rồi thì add vào
                if (translatedArray != null) canNotTranslateArrays.add(translatedArray);
            } else {
                if (translatedArray == null) {//Chưa có dịch cho string này
                    if (specificArray == null)
                        unTranslatedArrays.add(originArray);
                } else {//Đã có dịch
                    boolean isWrongTranslation = originArray.isWrongFormat(translatedArray);
                    if (isWrongTranslation) {//Nếu là dịch sai
                        if (specificArray != null) {//Dịch sai mà đã có dịch riêng biệt
                            boolean isWrongSpecificTranslation = originArray.isWrongFormat(specificArray);
                            if (isWrongSpecificTranslation) {//Dịch riêng biệt cũng sai nốt
                                wrongTranslatedOriginArrays.add(originArray);
                                wrongTranslatedArrays.add(translatedArray);
                            }
                        } else {//Dịch sai mà cũng ko có cả dịch riêng luôn
                            wrongTranslatedOriginArrays.add(originArray);
                            wrongTranslatedArrays.add(translatedArray);
                        }
                    } else if (originArray.getItems().equals(translatedArray.getItems())
                            && originArray.getName().equals(translatedArray.getName())) {
                        //Có dịch nhưng mà dịch cũng như ko vì nó giống nhau
                        originEqualTranslatedArrays.add(originArray);
                    } else {
                        //Dịch rồi, mà lại còn đúng nữa thì làm gì ở đây nữa =))
                    }
                }
            }
        });

    }

    private void filterString(Application specificedApp, Application translatedApp, TargetDevice transDevices) {
        originStrings.forEach(originString -> {
            boolean canNotTranslate = MainGUI.unTranslateables.stream().anyMatch(unTranslateable -> {
                return unTranslateable.getApplication().equals(getName())
                        && unTranslateable.getName().equals(originString.getName());
            });
//            boolean isIgnored = MainGUI.autoIgnoredList.stream().anyMatch(unTranslateable -> {
//                return unTranslateable.getApplication().equals(getName())
//                        && unTranslateable.getName().equals(originString.getName());
//            });
            StringRes translatedString = findTranslatedString(originString, translatedApp, transDevices, false);
            StringRes specificString = findTranslatedString(originString, specificedApp, transDevices, true);

            if (canNotTranslate) {
                //Nếu là string không thể dịch, mà đã có dịch rồi thì add vào
                if (translatedString != null) canNotTranslateStrings.add(translatedString);
            } else {
                if (translatedString == null) {//Chưa có dịch cho string này
                    if (specificString == null)
                        unTranslatedStrings.add(originString);
                } else {//Đã có dịch
                    boolean isWrongTranslation = originString.isWrongFormat(translatedString);
                    if (isWrongTranslation) {//Nếu là dịch sai
                        if (specificString != null) {//Dịch sai mà đã có dịch riêng biệt
                            boolean isWrongSpecificTranslation = originString.isWrongFormat(specificString);
                            if (isWrongSpecificTranslation) {//Dịch riêng biệt cũng sai nốt
                                wrongTranslatedOriginStrings.add(originString);
                                wrongTranslatedStrings.add(translatedString);
                            }
                        } else {//Dịch sai mà cũng ko có cả dịch riêng luôn
                            wrongTranslatedOriginStrings.add(originString);
                            wrongTranslatedStrings.add(translatedString);
                        }
                    } else if (originString.getValue().equals(translatedString.getValue())
                            && originString.getName().equals(translatedString.getName())) {
                        //Có dịch nhưng mà dịch cũng như ko vì nó giống nhau
                        originEqualTranslatedStrings.add(originString);
                    } else {
                        //Dịch rồi, mà lại còn đúng nữa thì làm gì ở đây nữa =))
                    }
                }
            }
        });
    }

    private PluralRes findTranslatedPlural(PluralRes originPlural, Application translatedApp, TargetDevice transDevices, boolean isSpecific) {
//        if (translatedApp == null) return null;
//        List<PluralRes> pluralToStream = !isSpecific && SearchOptions.deepFilterForUnTranslated ? transDevices.getAllPlurals() : translatedApp.getOriginPlurals();
//        Optional<PluralRes> hasTranslated = pluralToStream.stream().filter(stringRes -> arrayRes.getName().equals(stringRes.getName())).findFirst();
//        return hasTranslated.orElse(null);


        if (translatedApp == null) return null;
//        List<PluralRes> stringToStream = !isSpecific && SearchOptions.deepFilterForUnTranslated ? transDevices.getAllPlurals() : translatedApp.getOriginPlural();
        Optional<PluralRes> hasTranslated = translatedApp.getOriginPlurals().stream().filter(stringRes -> originPlural.getName().equals(stringRes.getName())).findFirst();

        Optional<PluralRes> hasTranslatedInAll = transDevices.getAllPlurals().stream().filter(stringRes -> originPlural.getName().equals(stringRes.getName())).findFirst();
        PluralRes result;
        if (!isSpecific)
            result = hasTranslated.orElse(!hasTranslatedInAll.isPresent() ? null : hasTranslatedInAll.orElse(null));
        else result = hasTranslated.orElse(null);

        if (!isSpecific && !hasTranslated.isPresent() && hasTranslatedInAll.isPresent()) {
            if (!hasTranslatedInAll.get().getName().contains("app_name") && !originPlural.isWrongFormat(hasTranslatedInAll.get())) {
                Optional<UnTranslateable> unTranslateable = MainGUI.unTranslateables.stream().filter(unTranslateable1 -> {
                    return unTranslateable1.getFile().equals("plurals.xml")
                            && unTranslateable1.getApplication().equals(name)
                            && unTranslateable1.getName().equals(hasTranslatedInAll.get().getName());
                }).findFirst();
                if (!unTranslateable.isPresent()) translatedFromOtherPlural.add(hasTranslatedInAll.get());
            }
        }
        return result;
    }

    private ArrayRes findTranslatedArray(ArrayRes originArray, Application translatedApp, TargetDevice transDevices, boolean isSpecific) {
        if (translatedApp == null) return null;
//        List<ArrayRes> stringToStream = !isSpecific && SearchOptions.deepFilterForUnTranslated ? transDevices.getAllArrays() : translatedApp.getOriginArray();
        Optional<ArrayRes> hasTranslated = translatedApp.getOriginArrays().stream().filter(stringRes -> originArray.getName().equals(stringRes.getName())).findFirst();

        Optional<ArrayRes> hasTranslatedInAll = transDevices.getAllArrays().stream().filter(stringRes -> originArray.getName().equals(stringRes.getName())).findFirst();
        ArrayRes result;
        if (!isSpecific)
            result = hasTranslated.orElse(!hasTranslatedInAll.isPresent() ? null : hasTranslatedInAll.orElse(null));
        else result = hasTranslated.orElse(null);

        if (!isSpecific && !hasTranslated.isPresent() && hasTranslatedInAll.isPresent()) {
            if (!hasTranslatedInAll.get().getName().contains("app_name") && !originArray.isWrongFormat(hasTranslatedInAll.get())) {
                Optional<UnTranslateable> unTranslateable = MainGUI.unTranslateables.stream().filter(unTranslateable1 -> {
                    return unTranslateable1.getFile().equals("arrays.xml")
                            && unTranslateable1.getApplication().equals(name)
                            && unTranslateable1.getName().equals(hasTranslatedInAll.get().getName());
                }).findFirst();
                if (!unTranslateable.isPresent()) translatedFromOtherArray.add(hasTranslatedInAll.get());
            }
        }
        return result;
    }


    @Nullable
    private StringRes findTranslatedString(StringRes originString, Application translatedApp, TargetDevice transDevices, boolean isSpecific) {
        if (translatedApp == null) return null;
//        List<StringRes> stringToStream = !isSpecific && SearchOptions.deepFilterForUnTranslated ? transDevices.getAllStrings() : translatedApp.getOriginString();
        Optional<StringRes> hasTranslated = translatedApp.getOriginString().stream().filter(stringRes -> originString.getName().equals(stringRes.getName())).findFirst();

        Optional<StringRes> hasTranslatedInAll = transDevices.getAllStrings().stream().filter(stringRes -> originString.getName().equals(stringRes.getName())).findFirst();
        StringRes result;
        if (!isSpecific)
            result = hasTranslated.orElse(!hasTranslatedInAll.isPresent() ? null : hasTranslatedInAll.orElse(null));
        else result = hasTranslated.orElse(null);

        if (!isSpecific && !hasTranslated.isPresent() && hasTranslatedInAll.isPresent()) {
            if (!hasTranslatedInAll.get().getName().contains("app_name") && !originString.isWrongFormat(hasTranslatedInAll.get())) {
                Optional<UnTranslateable> unTranslateable = MainGUI.unTranslateables.stream().filter(unTranslateable1 -> {
                    return unTranslateable1.getFile().equals("strings.xml")
                            && unTranslateable1.getApplication().equals(name)
                            && unTranslateable1.getName().equals(hasTranslatedInAll.get().getName());
                }).findFirst();
                if (!unTranslateable.isPresent()) translatedFromOtherString.add(hasTranslatedInAll.get());
            }
        }
        return result;
    }

    public List<StringRes> getOriginString() {
        return originStrings;
    }

    public String getName() {
        return name;
    }

    public File getTranslation() {
        return translation;
    }

    public List<ArrayRes> getOriginArrays() {
        return originArrays;
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
