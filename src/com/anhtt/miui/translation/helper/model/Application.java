package com.anhtt.miui.translation.helper.model;

import com.anhtt.miui.translation.helper.MainGUI;
import com.anhtt.miui.translation.helper.model.res.ArrayRes;
import com.anhtt.miui.translation.helper.model.res.StringRes;
import com.sun.istack.internal.Nullable;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Application {
    private String name;
    private File translation;
    List<StringRes> originStrings = new ArrayList<>();//String gốc của app
    List<StringRes> translatedStrings = new ArrayList<>();//String đã dịch
    List<StringRes> unTranslatedStrings = new ArrayList<>();//String chưa dịch
    List<StringRes> wrongTranslatedOriginStrings = new ArrayList<>();//String gốc bị dịch sai
    List<StringRes> wrongTranslatedStrings = new ArrayList<>();//String bị dịch sai
    List<StringRes> mightNotTranslatedStrings = new ArrayList<>();//String giống nhau giữa gốc và dịch, có thể bỏ
    List<StringRes> canNotTranslateStrings = new ArrayList<>();//String không thể dịch
    List<StringRes> originEqualTranslatedStrings = new ArrayList<>();//Dịch giống gốc


    List<ArrayRes> originArrays = new ArrayList<>();
    List<ArrayRes> translatedArrays = new ArrayList<>();//Array đã dịch
    List<ArrayRes> unTranslatedArrays = new ArrayList<>();//Array chưa dịch
    List<ArrayRes> wrongTranslatedOriginArrays = new ArrayList<>();//Array gốc bị dịch sai
    List<ArrayRes> wrongTranslatedArrays = new ArrayList<>();//Array bị dịch sai
    List<ArrayRes> mightNotTranslatedArrays = new ArrayList<>();//Array giống nhau giữa gốc và dịch, có thể bỏ
    List<ArrayRes> canNotTranslateArrays = new ArrayList<>();//Array không thể dịch
    List<ArrayRes> originEqualTranslatedArrays = new ArrayList<>();//Dịch giống gốc
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

    public Application(String name, File translation) {
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
    public static Application create(String path) {
        String deviceName = getDeviceName(path);
        if (deviceName == null || deviceName.isEmpty()) return null;

        File file = new File(path);

        if (file.exists()) {
            Application app = new Application(deviceName, file);
            try {
                app.createStringList();
                app.createArrayList();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return app;
        }
        return null;
    }

    private void createStringList() throws Exception {
        originStrings = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        File valueFolder = new File(translation.getAbsolutePath() + "\\res\\values");
        if (!valueFolder.exists()) valueFolder = new File(translation.getAbsolutePath() + "\\res\\values-vi");
        if (!valueFolder.exists()) valueFolder = new File(translation.getAbsolutePath() + "\\res\\values-vi-rVN");
        if (!valueFolder.exists()) return;
        File file = new File(valueFolder.getAbsolutePath() + "\\strings.xml");
        Document doc = docBuilder.parse(file);
        NodeList list = doc.getElementsByTagName("string");
        for (int i = 0; i < list.getLength(); i++) {
            StringRes stringRes = StringRes.create((Element) list.item(i));
            if (stringRes != null) originStrings.add(stringRes);
        }
    }

    private void createArrayList() throws Exception {
        originArrays = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        File valueFolder = new File(translation.getAbsolutePath() + "\\res\\values");
        if (!valueFolder.exists()) valueFolder = new File(translation.getAbsolutePath() + "\\res\\values-vi");
        if (!valueFolder.exists()) valueFolder = new File(translation.getAbsolutePath() + "\\res\\values-vi-rVN");
        if (!valueFolder.exists()) return;
        File file = new File(valueFolder.getAbsolutePath() + "\\arrays.xml");
        if(!file.exists()) return;
        Document doc = docBuilder.parse(file);
        NodeList list = doc.getElementsByTagName("string-array");
        for (int i = 0; i < list.getLength(); i++) {
            ArrayRes stringRes = ArrayRes.create((Element)list.item(i));
            if (stringRes != null) originArrays.add(stringRes);
        }
    }

    public void compare(OriginDevice originDevice, Application specificedApp, Application translatedApp) {
        if (translatedApp == null) {
            unTranslatedStrings.addAll(originStrings);
            return;
        }
        originStrings.forEach(originString -> {
            boolean canNotTranslate = MainGUI.unTranslateables.stream().anyMatch(unTranslateable -> {
                return unTranslateable.getApplication().equals(getName())
                        && unTranslateable.getName().equals(originString.getName());
            });
//            boolean isIgnored = MainGUI.autoIgnoredList.stream().anyMatch(unTranslateable -> {
//                return unTranslateable.getApplication().equals(getName())
//                        && unTranslateable.getName().equals(originString.getName());
//            });
            StringRes translatedString = findTranslatedString(originString, translatedApp, false);
            StringRes specificString = findTranslatedString(originString, specificedApp, true);

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


        originArrays.forEach(arrayRes -> {
            boolean canNotTranslate = MainGUI.unTranslateables.stream().anyMatch(unTranslateable -> {
                return unTranslateable.getApplication().equals(getName())
                        && unTranslateable.getName().equals(arrayRes.getName());
            });
//            boolean isIgnored = MainGUI.autoIgnoredList.stream().anyMatch(unTranslateable -> {
//                return unTranslateable.getApplication().equals(getName())
//                        && unTranslateable.getName().equals(originArray.getName());
//            });
            ArrayRes translatedArray = findTranslatedArray(arrayRes, translatedApp, false);
            ArrayRes specificArray = findTranslatedArray(arrayRes, specificedApp, true);

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

    private ArrayRes findTranslatedArray(ArrayRes arrayRes, Application translatedApp, boolean isSpecific) {
        if (translatedApp == null) return null;
        Optional<ArrayRes> hasTranslated = translatedApp.getOriginArrays().stream().filter(stringRes -> arrayRes.getName().equals(stringRes.getName())).findFirst();
        return hasTranslated.orElse(null);
    }


    @Nullable
    private StringRes findTranslatedString(StringRes originString, Application translatedApp, boolean isSpecific) {
        if (translatedApp == null) return null;
        Optional<StringRes> hasTranslated = translatedApp.getOriginString().stream().filter(stringRes -> originString.getName().equals(stringRes.getName())).findFirst();
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
