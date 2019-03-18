package com.anhtt.miui.translation.helper.model.res;

import com.sun.istack.internal.Nullable;
import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;
import com.sun.org.apache.xerces.internal.dom.DeferredTextImpl;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class ArrayRes implements Resource<ArrayRes> {
    String name;
    String arrayType;
    List<Item> items = new ArrayList<>();

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public List<Item> getItems() {
        return items;
    }

    public ArrayRes(String name, String arrayType) {
        this.name = name;
        this.arrayType = arrayType;
    }

    public String getArrayType() {
        return arrayType;
    }

    @Nullable
    public static ArrayRes create(Element element) {
        String name = "";
        String arrayType = "";

        NamedNodeMap curAttr = element.getAttributes();
        arrayType = element.getNodeName();
        for (int j = 0; j < curAttr.getLength(); j++) {
            Node attr = curAttr.item(j);
            if (attr.getNodeName().equals("name"))
                name = attr.getNodeValue();
        }

        List<Item> items = new ArrayList<>();
        NodeList list = element.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node child =  list.item(i);
            if(child.getNodeName().equals("item"))
            items.add(new Item(child.getTextContent()));
        }
        if (name != null && name.length() > 0 && arrayType != null && arrayType.length() > 0) {
            ArrayRes arrayRes = new ArrayRes(name, arrayType);
            arrayRes.setItems(items);
            return arrayRes;
        }

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
    public boolean equalsExact(ArrayRes origin) {
        if (!name.equals(origin.getName())) return false;
        if (items == null && origin.getItems() != null) return false;
        if (origin.getItems() == null && items != null) return false;
        if (items == null && origin.getItems() == null) return true;
        if (items.size() != origin.getItems().size()) return false;
        for (int i = 0; i < items.size(); i++)
            if (!items.get(i).getValue().equals(origin.getItems().get(i).getValue())) return false;
        return true;
    }
}
