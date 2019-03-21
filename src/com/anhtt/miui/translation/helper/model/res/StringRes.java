package com.anhtt.miui.translation.helper.model.res;

import com.anhtt.miui.translation.helper.Utils;
import com.sun.istack.internal.Nullable;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class StringRes implements Resource<StringRes> {
    private String name;
    private String value;
    private boolean formatted = true;

    @Nullable
    public static StringRes create(Element element, boolean isTranslatedDevice) {
        String name = "";
        boolean formatted = true;
        String value = element.getTextContent();
        NamedNodeMap curAttr = element.getAttributes();
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
            if ((!isTranslatedDevice && Utils.isNumeric(value))
                    || name.contains("abc_prepend")
                    || name.contains("abc_menu")
                    || name.contains("summary_collapsed_preference_list")
                    || name.contains("cityname")
                    || name.contains("country_name")
                    || name.contains("alisdk_message")
                    || name.contains("com_taobao_tae_sdk")
                    || value.contains("@string/")
                    || value.contains("@android:string/")
                    || value.contains("sans-serif")
                    || value.contains("google-sans")
                    || value.equalsIgnoreCase("OK")
                    || value.equals("999+")
                    || value.equals("%d")
                    || value.equals("%1$d")
                    || value.equals("%2$d")
                    || value.equals("%3$d")
                    || value.equals("%s")
                    || value.equals("%1$s")
                    || value.equals("%2$s")
                    || value.equals("%3$s")
                    || (value.length() >= 15 && !value.contains(" "))
                    || value.trim().replaceAll("\n", "").replaceAll("\t", "").length() == 0
            ) return null;


            StringRes stringRes = new StringRes(name, value);
            stringRes.setFormatted(formatted);
            return stringRes;
        }

        return null;
    }

    public void setFormatted(boolean formatted) {
        this.formatted = formatted;
    }

    public boolean isFormatted() {
        return formatted;
    }

    public StringRes(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean isWrongFormat(StringRes other) {
        if (isFormatted() != other.isFormatted()) return true;
        if (!isFormatted() && !isFormatted()) return false;
        if (count(this, "%") != count(other, "%"))
            return true;
        if (count(this, "%d") != count(other, "%d"))
            return true;
        if (count(this, "%s") != count(other, "%s"))
            return true;
        if (count(this, "%1$d") != count(other, "%1$d"))
            return true;
        if (count(this, "%1$s") != count(other, "%1$s"))
            return true;
        if (count(this, "%2$d") != count(other, "%2$d"))
            return true;
        if (count(this, "%2$s") != count(other, "%2$s"))
            return true;
        if (count(this, "%3$d") != count(other, "%3$d"))
            return true;
        if (count(this, "%3$s") != count(other, "%3$s"))
            return true;
        return false;
    }

    public static int count(StringRes string, String match) {
        return (string.getValue().split(match, -1).length) - 1;
    }

    public static int count(String string, String match) {
        return (string.split(match, -1).length) - 1;
    }
}
