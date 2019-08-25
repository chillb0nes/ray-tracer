package com.example.renderer.view.util;

import com.example.renderer.model.light.LightSource;
import com.example.renderer.model.object.Mesh;
import com.example.renderer.model.object.Object3D;
import com.example.renderer.model.object.Sphere;
import com.example.renderer.model.object.Triangle;
import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public class IconStorage {
    private static final Map<Class, Image> STORAGE;

    static {
        STORAGE = new HashMap<>();

        STORAGE.put(LightSource.class, new Image("/icons/icons8-light-on-filled-24.png"));

        STORAGE.put(Sphere.class, new Image("/icons/icons8-sphere-24.png"));

        STORAGE.put(Triangle.class, new Image("/icons/icons8-triangle-filled-30.png"));

        STORAGE.put(Mesh.class, new Image("/icons/icons8-orthogonal-view-filled-24.png"));
    }

    public static Image get(Class clazz) {
        return STORAGE.get(clazz);
    }
}
