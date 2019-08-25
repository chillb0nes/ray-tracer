package com.example.renderer.model;

import com.example.renderer.model.light.LightSource;
import com.example.renderer.model.object.Renderable;
import com.google.common.collect.Sets;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point3D;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Data
public class Scene {
    public final static int DEFAULT_WIDTH = 640;
    public final static int DEFAULT_HEIGHT = 480;

    private final int width;
    private final int height;
    private double fov;
    private boolean aaEnabled;

    private Point3D cameraOrigin;
    private Set<Renderable> objects;
    private Set<LightSource> lights;
    @EqualsAndHashCode.Exclude
    private Set<Renderable> selected;

    public Scene(int width, int height) {
        this.width = width;
        this.height = height;

        cameraOrigin = Point3D.ZERO;
        objects = new HashSet<>();
        lights = new HashSet<>();
        selected = new HashSet<>();
    }

    public void setFov(double fov) {
        this.fov = fov * Math.PI / 180;
    }

    public int getWidth() {
        return aaEnabled ? width * 2 : width;
    }

    public int getHeight() {
        return aaEnabled ? height * 2 : height;
    }

    /*public void addObject(Renderable object) {
        objects.add(object);
    }

    public void removeObject(Renderable object) {
        objects.remove(object);
    }

    public void addLight(LightSource light) {
        lights.add(light);
    }

    public void removeLight(LightSource light) {
        lights.remove(light);
    }*/
}
