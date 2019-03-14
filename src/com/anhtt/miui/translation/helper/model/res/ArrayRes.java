package com.anhtt.miui.translation.helper.model.res;

import com.sun.istack.internal.Nullable;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class ArrayRes implements Resource<ArrayRes> {
    String name;
    List<Item> items = new ArrayList<>();

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public List<Item> getItems() {
        return items;
    }

    @Nullable
    public static ArrayRes create(Element element) {
//        String name = "";
//
//        NamedNodeMap curAttr = element.getAttributes();
//        for (int j = 0; j < curAttr.getLength(); j++) {
//            Node attr = curAttr.item(j);
//            if (attr.getNodeName().equals("name"))
//                name = attr.getNodeValue();
//        }
//
//        List<Item> items = new ArrayList<>();
//        NodeList list = element.getChildNodes();
//        for (int i = 0; i < list.getLength(); i++) {
//            Element child = (Element) list.item(i);
//            items.add(new Item(child.getTextContent()));
//        }
//        if (name != null && name.length() > 0) {
//            ArrayRes arrayRes = new ArrayRes(name);
//            arrayRes.setItems(items);
//            return arrayRes;
//        }

        return null;
    }

    public ArrayRes(String name) {
        this.name = name;
    }

    @Override
    public boolean isWrongFormat(ArrayRes other) {
        return false;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ArrayRes) {
            ArrayRes other = (ArrayRes) obj;
            if (!name.equals(other.name)) return false;
            if (items.size() != other.items.size()) return false;
            for (int i = 0; i < items.size(); i++) {
                if (!items.get(i).getValue().equals(other.items.get(i).getValue())) return false;
            }

        } else
            return false;
        return true;
    }
}
