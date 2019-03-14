package com.anhtt.miui.translation.helper.model;

import com.anhtt.miui.translation.helper.MainGUI;
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
    List<StringRes> wrongTranslatedOrigins = new ArrayList<>();//String gốc bị dịch sai
    List<StringRes> wrongTranslateds = new ArrayList<>();//String bị dịch sai
    List<StringRes> mightNotTranslateds = new ArrayList<>();//String giống nhau giữa gốc và dịch, có thể bỏ
    List<StringRes> canNotTranslates = new ArrayList<>();//String không thể dịch
    List<StringRes> originEqualTranslated = new ArrayList<>();//Dịch giống gốc

    public List<StringRes> getOriginStrings() {
        return originStrings;
    }

    public List<StringRes> getTranslatedStrings() {
        return translatedStrings;
    }

    public List<StringRes> getUnTranslatedStrings() {
        return unTranslatedStrings;
    }

    public List<StringRes> getWrongTranslatedOrigins() {
        return wrongTranslatedOrigins;
    }

    public List<StringRes> getWrongTranslateds() {
        return wrongTranslateds;
    }

    public List<StringRes> getMightNotTranslateds() {
        return mightNotTranslateds;
    }

    public List<StringRes> getCanNotTranslates() {
        return canNotTranslates;
    }

    public List<StringRes> getOriginEqualTranslated() {
        return originEqualTranslated;
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
            String name = "";
            boolean formatted = true;
            Element cur = (Element) list.item(i);
            String value = cur.getTextContent();
            NamedNodeMap curAttr = cur.getAttributes();
            for (int j = 0; j < curAttr.getLength(); j++) {
                Node attr = curAttr.item(j);
                if (attr.getNodeName().equals("name"))
                    name = attr.getNodeValue();
                if (attr.getNodeName().equals("formatted"))
                    try {
                        formatted = Boolean.parseBoolean(attr.getNodeValue());
                    } catch (Exception ignored) {
                    }
            }
            if (name != null && value != null && name.length() > 0 && value.length() > 0) {
                StringRes stringRes = new StringRes(name, value);
                stringRes.setFormatted(formatted);
                originStrings.add(stringRes);
            }
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
            StringRes translatedString = findTranslatedString(originString, translatedApp,false);
            StringRes specificString = findTranslatedString(originString, specificedApp,true);

            if (canNotTranslate) {
                //Nếu là string không thể dịch, mà đã có dịch rồi thì add vào
                if (translatedString != null) canNotTranslates.add(translatedString);
            } else {
                if (translatedString == null) {//Chưa có dịch cho string này
                    if (specificString == null)
                        unTranslatedStrings.add(originString);
                } else {//Đã có dịch
                    boolean isWrongTranslation = isWrongTranslation(originString, translatedString);
                    if (isWrongTranslation) {//Nếu là dịch sai
                        if (specificString != null) {//Dịch sai mà đã có dịch riêng biệt
                            boolean isWrongSpecificTranslation = isWrongTranslation(originString, specificString);
                            if (isWrongSpecificTranslation) {//Dịch riêng biệt cũng sai nốt
                                wrongTranslatedOrigins.add(originString);
                                wrongTranslateds.add(translatedString);
                            }
                        } else {//Dịch sai mà cũng ko có cả dịch riêng luôn
                            wrongTranslatedOrigins.add(originString);
                            wrongTranslateds.add(translatedString);
                        }
                    } else if (originString.getValue().equals(translatedString.getValue())
                            && originString.getName().equals(translatedString.getName())) {
                        //Có dịch nhưng mà dịch cũng như ko vì nó giống nhau
                        originEqualTranslated.add(originString);
                    } else {
                        //Dịch rồi, mà lại còn đúng nữa thì làm gì ở đây nữa =))
                    }
                }
            }
        });
    }


    @Nullable
    private StringRes findTranslatedString(StringRes originString, Application translatedApp, boolean isSpecific) {
        if (translatedApp == null) return null;
        Optional<StringRes> hasTranslated = translatedApp.getOriginString().stream().filter(stringRes -> originString.getName().equals(stringRes.getName())).findFirst();
        return hasTranslated.orElse(null);
    }

    private boolean isWrongTranslation(StringRes string, StringRes otherString) {
        if (string.isFormatted() != otherString.isFormatted()) return true;
        if (!string.isFormatted() && !string.isFormatted()) return false;
        if (count(string, "%") != count(otherString, "%"))
            return true;
        if (count(string, "%d") != count(otherString, "%d"))
            return true;
        if (count(string, "%s") != count(otherString, "%s"))
            return true;
        if (count(string, "%1$d") != count(otherString, "%1$d"))
            return true;
        if (count(string, "%1$s") != count(otherString, "%1$s"))
            return true;
        if (count(string, "%2$d") != count(otherString, "%2$d"))
            return true;
        if (count(string, "%2$s") != count(otherString, "%2$s"))
            return true;
        if (count(string, "%3$d") != count(otherString, "%3$d"))
            return true;
        if (count(string, "%3$s") != count(otherString, "%3$s"))
            return true;
        return false;
    }

    private int count(StringRes string, String match) {
        return (string.getValue().split(match, -1).length) - 1;
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
}
