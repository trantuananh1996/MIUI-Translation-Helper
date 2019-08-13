package com.anhtt.miui.translation.helper.model.res;

import org.w3c.dom.Node;

public interface Resource<T> {
    boolean isWrongFormat(T other);
}
