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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Application {
    private String name;
    private File translation;
    private List<StringRes> originStrings = new ArrayList<>();//String gốc của app
    private List<StringRes> translatedStrings = new ArrayList<>();//String đã dịch
    private List<StringRes> unTranslatedStrings = new ArrayList<>();//String chưa dịch
    private List<StringRes> wrongTranslatedOriginStrings = new ArrayList<>();//String gốc bị dịch sai
    private List<StringRes> wrongTranslatedStrings = new ArrayList<>();//String bị dịch sai
    private List<StringRes> mightNotTranslatedStrings = new ArrayList<>();//String giống nhau giữa gốc và dịch, có thể bỏ
    private List<StringRes> canNotTranslateStrings = new ArrayList<>();//String không thể dịch
    private List<StringRes> originEqualTranslatedStrings = new ArrayList<>();//Dịch giống gốc


    private List<ArrayRes> originArrays = new ArrayList<>();
    private List<ArrayRes> translatedArrays = new ArrayList<>();//Array đã dịch
    private List<ArrayRes> unTranslatedArrays = new ArrayList<>();//Array chưa dịch
    private List<ArrayRes> wrongTranslatedOriginArrays = new ArrayList<>();//Array gốc bị dịch sai
    private List<ArrayRes> wrongTranslatedArrays = new ArrayList<>();//Array bị dịch sai
    private List<ArrayRes> mightNotTranslatedArrays = new ArrayList<>();//Array giống nhau giữa gốc và dịch, có thể bỏ
    private List<ArrayRes> canNotTranslateArrays = new ArrayList<>();//Array không thể dịch
    private List<ArrayRes> originEqualTranslatedArrays = new ArrayList<>();//Dịch giống gốc


    private List<PluralRes> originPlurals = new ArrayList<>();
    private List<PluralRes> translatedPlurals = new ArrayList<>();//Plural đã dịch
    private List<PluralRes> unTranslatedPlurals = new ArrayList<>();//Plural chưa dịch
    private List<PluralRes> wrongTranslatedOriginPlurals = new ArrayList<>();//Plural gốc bị dịch sai
    private List<PluralRes> wrongTranslatedPlurals = new ArrayList<>();//Plural bị dịch sai
    private List<PluralRes> mightNotTranslatedPlurals = new ArrayList<>();//Plural giống nhau giữa gốc và dịch, có thể bỏ
    private List<PluralRes> canNotTranslatePlurals = new ArrayList<>();//Plural không thể dịch
    private List<PluralRes> originEqualTranslatedPlurals = new ArrayList<>();//Dịch giống gốc

    private List<PluralRes> getOriginPlurals() {
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
            PluralRes stringRes = PluralRes.create((Element) list.item(i), isApplyFilter);
            if (stringRes != null) originPlurals.add(stringRes);
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

    public void compare(OriginDevice originDevice, Application specificedApp, Application translatedApp, TranslatedDevice transDevices) {
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

    private void filterPlural(Application specificedApp, Application translatedApp, TranslatedDevice transDevices) {
        originPlurals.forEach(arrayRes -> {
            boolean canNotTranslate = MainGUI.unTranslateables.stream().anyMatch(unTranslateable -> {
                return unTranslateable.getApplication().equals(getName())
                        && unTranslateable.getName().equals(arrayRes.getName());
            });
//            boolean isIgnored = MainGUI.autoIgnoredList.stream().anyMatch(unTranslateable -> {
//                return unTranslateable.getApplication().equals(getName())
//                        && unTranslateable.getName().equals(originPlural.getName());
//            });
            PluralRes translatedPlural = findTranslatedPlural(arrayRes, translatedApp, transDevices, false);
            PluralRes specificPlural = findTranslatedPlural(arrayRes, specificedApp, transDevices, true);

            if (canNotTranslate) {
                //Nếu là Plural không thể dịch, mà đã có dịch rồi thì add vào
                if (translatedPlural != null) canNotTranslatePlurals.add(translatedPlural);
            } else {
                if (translatedPlural == null) {//Chưa có dịch cho Plural này
                    if (specificPlural == null)
                        unTranslatedPlurals.add(arrayRes);
                } else {//Đã có dịch
                    boolean isWrongTranslation = arrayRes.isWrongFormat(translatedPlural);
                    if (isWrongTranslation) {//Nếu là dịch sai
                        if (specificPlural != null) {//Dịch sai mà đã có dịch riêng biệt
                            boolean isWrongSpecificTranslation = arrayRes.isWrongFormat(specificPlural);
                            if (isWrongSpecificTranslation) {//Dịch riêng biệt cũng sai nốt
                                wrongTranslatedOriginPlurals.add(arrayRes);
                                wrongTranslatedPlurals.add(translatedPlural);
                            }
                        } else {//Dịch sai mà cũng ko có cả dịch riêng luôn
                            wrongTranslatedOriginPlurals.add(arrayRes);
                            wrongTranslatedPlurals.add(translatedPlural);
                        }
                    } else if (arrayRes.equalsExact(translatedPlural)) {
                        //Có dịch nhưng mà dịch cũng như ko vì nó giống nhau
                        originEqualTranslatedPlurals.add(arrayRes);
                    } else {
                        //Dịch rồi, mà lại còn đúng nữa thì làm gì ở đây nữa =))
                    }
                }
            }
        });
    }

    private void filterArray(Application specificedApp, Application translatedApp, TranslatedDevice transDevices) {
        originArrays.forEach(arrayRes -> {
            boolean canNotTranslate = MainGUI.unTranslateables.stream().anyMatch(unTranslateable -> {
                return unTranslateable.getApplication().equals(getName())
                        && unTranslateable.getName().equals(arrayRes.getName());
            });
//            boolean isIgnored = MainGUI.autoIgnoredList.stream().anyMatch(unTranslateable -> {
//                return unTranslateable.getApplication().equals(getName())
//                        && unTranslateable.getName().equals(originArray.getName());
//            });
            ArrayRes translatedArray = findTranslatedArray(arrayRes, translatedApp, transDevices, false);
            ArrayRes specificArray = findTranslatedArray(arrayRes, specificedApp, transDevices, true);

            if (canNotTranslate) {
                //Nếu là Array không thể dịch, mà đã có dịch rồi thì add vào
                if (translatedArray != null) canNotTranslateArrays.add(translatedArray);
            } else {
                if (translatedArray == null) {//Chưa có dịch cho Array này
                    if (specificArray == null)
                        unTranslatedArrays.add(arrayRes);
                } else {//Đã có dịch
                    boolean isWrongTranslation = arrayRes.isWrongFormat(translatedArray);
                    if (isWrongTranslation) {//Nếu là dịch sai
                        if (specificArray != null) {//Dịch sai mà đã có dịch riêng biệt
                            boolean isWrongSpecificTranslation = arrayRes.isWrongFormat(specificArray);
                            if (isWrongSpecificTranslation) {//Dịch riêng biệt cũng sai nốt
                                wrongTranslatedOriginArrays.add(arrayRes);
                                wrongTranslatedArrays.add(translatedArray);
                            }
                        } else {//Dịch sai mà cũng ko có cả dịch riêng luôn
                            wrongTranslatedOriginArrays.add(arrayRes);
                            wrongTranslatedArrays.add(translatedArray);
                        }
                    } else if (arrayRes.equalsExact(translatedArray)) {
                        //Có dịch nhưng mà dịch cũng như ko vì nó giống nhau
                        originEqualTranslatedArrays.add(arrayRes);
                    } else {
                        //Dịch rồi, mà lại còn đúng nữa thì làm gì ở đây nữa =))
                    }
                }
            }
        });

    }

    private void filterString(Application specificedApp, Application translatedApp, TranslatedDevice transDevices) {
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

    private PluralRes findTranslatedPlural(PluralRes arrayRes, Application translatedApp, TranslatedDevice transDevices, boolean isSpecific) {
        if (translatedApp == null) return null;
        List<PluralRes> pluralToStream = !isSpecific && SearchOptions.deepFilterForUnTranslated ? transDevices.getAllPlurals() : translatedApp.getOriginPlurals();
        Optional<PluralRes> hasTranslated = pluralToStream.stream().filter(stringRes -> arrayRes.getName().equals(stringRes.getName())).findFirst();
        return hasTranslated.orElse(null);
    }

    private ArrayRes findTranslatedArray(ArrayRes arrayRes, Application translatedApp, TranslatedDevice transDevices, boolean isSpecific) {
        if (translatedApp == null) return null;
        List<ArrayRes> arrayToStream = !isSpecific && SearchOptions.deepFilterForUnTranslated ? transDevices.getAllArrays() : translatedApp.getOriginArrays();
        Optional<ArrayRes> hasTranslated = arrayToStream.stream().filter(stringRes -> arrayRes.getName().equals(stringRes.getName())).findFirst();
        return hasTranslated.orElse(null);
    }


    @Nullable
    private StringRes findTranslatedString(StringRes originString, Application translatedApp, TranslatedDevice transDevices, boolean isSpecific) {
        if (translatedApp == null) return null;
        List<StringRes> stringToStream = !isSpecific && SearchOptions.deepFilterForUnTranslated ? transDevices.getAllStrings() : translatedApp.getOriginString();
        Optional<StringRes> hasTranslated = stringToStream.stream().filter(stringRes -> originString.getName().equals(stringRes.getName())).findFirst();
        return hasTranslated.orElse(null);
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
}
