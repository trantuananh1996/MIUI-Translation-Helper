package com.anhtt.miui.translation.helper.model.res;

import com.anhtt.miui.translation.helper.Utils;
import com.sun.istack.internal.Nullable;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.text.SimpleDateFormat;

import static com.anhtt.miui.translation.helper.Utils.containsHanScript;
import static com.anhtt.miui.translation.helper.Utils.nodeToString;

public class StringRes implements Resource<StringRes> {
    private String name;
    private String value;
    private boolean formatted = true;

    private static String toString(Node node) {
        StringWriter sw = new StringWriter();
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.transform(new DOMSource(node), new StreamResult(sw));
        } catch (TransformerException te) {
        }
        return sw.toString();
    }

    @Nullable
    public static StringRes create(Node element, boolean isApplyFilter) {
        String name = "";
        boolean formatted = true;
        String value = nodeToString(element);

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
            if (isApplyFilter) if (Utils.isNumeric(value)
                    || name.contains("abc_prepend")
                    || name.contains("abc_menu")
                    || name.contains("summary_collapsed_preference_list")
                    || name.contains("cityname")
                    || name.contains("country_name")
                    || name.contains("alisdk_message")
                    || name.contains("com_taobao_tae_sdk")
                    || name.contains("msp_")
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
                    || (!containsHanScript(value) && value.length() >= 15 && !value.contains(" "))
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

    public String getGroupValue() {
        return name + value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean isWrongFormat(StringRes other) {
        if (value.equals("Discard") && other.value.equals("Hủy")) return true;
        if (value.equals("Hủy") && other.value.equals("Discard")) return true;
        if (isFormatted() != other.isFormatted()) return true;
        if (!isFormatted() && !isFormatted()) return false;
//
//        if (getValue().contains("&lt") || getValue().contains("&gt") || getValue().contains("#")
//                || other.getValue().contains("&lt") || other.getValue().contains("&gt") || other.getValue().contains("#")) {
//            String thisReplaced = getValue().replaceAll("&amp;lt;", "&lt;").replaceAll("&amp;gt;", "&gt;");
//            String otherReplaced = other.getValue().replaceAll("&amp;lt;", "&lt;").replaceAll("&amp;gt;", "&gt;");
//            if (count(thisReplaced, "&lt") != count(otherReplaced, "&lt"))
//                return true;
//            if (count(thisReplaced, "&gt") != count(otherReplaced, "&gt"))
//                return true;
//            if (count(thisReplaced, "#") != count(otherReplaced, "#"))
//                return true;
//            if (count(thisReplaced, "br") != count(otherReplaced, "br"))
//                return true;
//        } else
//            if (isDateFormatString()) {
//            String thisReplaced = this.getValue().replaceAll("'min'", "").replaceAll("'s'", "").replaceAll("'ms'", "")
//                    .replaceAll("giờ", "").replaceAll("phút", "").replaceAll("giây", "").replaceAll("mili", "");
//            String otherReplaced = other.getValue().replaceAll("'min'", "").replaceAll("'s'", "").replaceAll("'ms'", "")
//                    .replaceAll("giờ", "").replaceAll("phút", "").replaceAll("giây", "").replaceAll("mili", "");
//
//            if (count(thisReplaced, "d") != count(otherReplaced, "d"))
//                return true;
//            if (count(thisReplaced, "m") != count(otherReplaced, "m"))
//                return true;
//            if (count(thisReplaced, "y") != count(otherReplaced, "y"))
//                return true;
//            if (count(thisReplaced, "D") != count(otherReplaced, "D"))
//                return true;
//            if (count(thisReplaced, "M") != count(otherReplaced, "M"))
//                return true;
//            if (count(thisReplaced, "Y") != count(otherReplaced, "Y"))
//                return true;
//            if (count(thisReplaced, "h") != count(otherReplaced, "h"))
//                return true;
//            if (count(thisReplaced, "s") != count(otherReplaced, "s"))
//                return true;
//            if (count(thisReplaced, "H") != count(otherReplaced, "H"))
//                return true;
//            if (count(thisReplaced, "S") != count(otherReplaced, "S"))
//                return true;
//            if (count(thisReplaced, ".") != count(otherReplaced, "."))
//                return true;
//            if (count(thisReplaced, "/") != count(otherReplaced, "/"))
//                return true;
//            if (count(thisReplaced, ",") != count(otherReplaced, ","))
//                return true;
//            if (count(thisReplaced, "-") != count(otherReplaced, "-"))
//                return true;
//        }
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
        if (count(this, "%4$d") != count(other, "%4$d"))
            return true;
        if (count(this, "%4$s") != count(other, "%4$s"))
            return true;
        if (count(this, "%5$d") != count(other, "%5$d"))
            return true;
        if (count(this, "%5$s") != count(other, "%5$s"))
            return true;
        return false;
    }

    private boolean isDateFormatString() {
//        return false;
        try {
            if (name.contains("fmt") || name.contains("format") || name.contains("date") || name.contains("time") || name.contains("zone")) {
                new SimpleDateFormat(getValue()).applyLocalizedPattern(getValue());
                return true;
            }
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static int count(StringRes string, String match) {
        return (string.getValue().split(match, -1).length) - 1;
    }

    public static int count(String string, String match) {
        return (string.split(match, -1).length) - 1;
    }
}
