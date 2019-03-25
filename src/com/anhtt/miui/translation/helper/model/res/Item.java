package com.anhtt.miui.translation.helper.model.res;

public class Item {
    String value;
    String quantity;

    public Item(String value) {
        this.value = value;
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
}
