package com.example.renderer.service;

import javafx.scene.Node;

public interface ControlFactory {

    Node getByClass(Class clazz);

}
