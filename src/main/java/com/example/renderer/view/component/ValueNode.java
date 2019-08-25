package com.example.renderer.view.component;

import javafx.beans.property.ReadOnlyObjectProperty;

public interface ValueNode<V> {

    ReadOnlyObjectProperty<V> valueProperty();

    void setValue(V value);

    V getValue();

}
