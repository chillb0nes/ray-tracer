package com.example.renderer.service.custom;

import com.example.renderer.service.ControlFactory;
import com.example.renderer.view.control.DoubleSpinner;
import javafx.scene.Node;

import java.math.BigDecimal;

public class RadiusControlFactory implements ControlFactory {

    @Override
    public Node getByClass(Class clazz) {
        if (clazz == double.class || clazz == Double.class) {
            return new DoubleSpinner(0, Double.MAX_VALUE);
        }
        if (clazz == BigDecimal.class) {
            // another control can be provided for greater precision
        }
        return null;
    }
}
