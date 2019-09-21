package com.example.renderer.view.component;

import javafx.beans.property.ReadOnlyObjectProperty;

public interface ValueNode<V> {

    ReadOnlyObjectProperty<V> valueProperty();

    V getValue();

    void setValue(V value);

    default V getDefaultValue() {
        return null;
    }

    default void setDefaultValue() {
        setValue(getDefaultValue());
    }

}
