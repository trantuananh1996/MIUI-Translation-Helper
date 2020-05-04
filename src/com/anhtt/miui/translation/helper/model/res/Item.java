package com.anhtt.miui.translation.helper.model.res;

public class Item {
    String value;
    String quantity;
    private boolean formatted = true;

    public Item(String value) {
        this.value = value;
    }

    public void setFormatted(boolean formatted) {
        this.formatted = formatted;
    }

    public boolean isFormatted() {
        return formatted;
    }

    public Item(String nodeToString, String quantity) {
        this.value = nodeToString;
        this.quantity = quantity;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getValue() {
        return value.trim();
    }

    public boolean isWrongFormat(Item other) {
        if (isFormatted() != other.isFormatted()) return true;
        if (!isFormatted() && !isFormatted()) return false;

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
//        } else if (isDateFormatString()) {
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
//
        return true;
    }

    public static int count(String string, String match) {
        return (string.split(match, -1).length) - 1;
    }

    public boolean isWrongFormatWithQuantity(Item other) {
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

    public static int count(Item string, String match) {
        return (string.getValue().split(match, -1).length) - 1;
    }
}
