package com.example.renderer.service;

import com.example.renderer.model.object.Mesh;
import com.example.renderer.model.object.Triangle;
import com.example.renderer.view.component.ExpandableListView1;
import com.example.renderer.view.control.DoubleSpinner;
import com.example.renderer.view.control.MeshControl;
import com.example.renderer.view.control.Point3DSpinner;
import com.example.renderer.view.control.TriangleControl;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.control.Label;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class DefaultControlFactory implements ControlFactory {

    private final Map<Class, Supplier<Node>> CONTROLS;

    public DefaultControlFactory() {
        CONTROLS = new HashMap<>();

        CONTROLS.put(double.class, DoubleSpinner::new);

        CONTROLS.put(Double.class, DoubleSpinner::new);

        CONTROLS.put(Point3D.class, Point3DSpinner::new);

        CONTROLS.put(Triangle.class, TriangleControl::new);

        CONTROLS.put(Mesh.class, MeshControl::new);
    }

    @Override
    public Node getByClass(Class clazz) {
        return CONTROLS.getOrDefault(clazz, () -> new Label(clazz.getName())).get();
    }
}
