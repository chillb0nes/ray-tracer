package com.example.renderer.service;

import javafx.scene.Node;

@Deprecated
public interface ControlFactory {

    Node getByClass(Class clazz);

}
