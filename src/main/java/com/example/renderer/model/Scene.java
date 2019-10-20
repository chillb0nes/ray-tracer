package com.example.renderer.model;

import com.example.renderer.model.light.LightSource;
import com.example.renderer.model.object.Object3D;
import com.example.renderer.model.object.Renderable;
import com.example.renderer.view.util.ObservableUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import javafx.util.Pair;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Data
public class Scene {

    private final int width;
    private final int height;

    private DoubleProperty fov;
    private BooleanProperty aaEnabled;
    private ObjectProperty<Point3D> cameraOrigin;
    private transient ObjectProperty<Object3D> selected;

    private final ObservableList<Renderable> objects;
    private final ObservableList<LightSource> lights;

    public Scene() {
        this(640, 480);
    }

    public Scene(int width, int height) {
        this.width = width;
        this.height = height;

        fov = new SimpleDoubleProperty(45);
        aaEnabled = new SimpleBooleanProperty();
        cameraOrigin = new SimpleObjectProperty<>(Point3D.ZERO);
        selected = new SimpleObjectProperty<>();

        objects = FXCollections.observableArrayList();
        lights = FXCollections.observableArrayList();
    }

    public int getWidth() {
        return aaEnabled.get() ? width * 2 : width;
    }

    public int getHeight() {
        return aaEnabled.get() ? height * 2 : height;
    }

    public void addObject(Object3D object3D) {
        if (object3D instanceof Renderable) {
            objects.add((Renderable) object3D);
        }
        if (object3D instanceof LightSource) {
            lights.add((LightSource) object3D);
        }
    }

    public void addObjects(Object3D... objects3D) {
        for (Object3D object3D : objects3D) {
            addObject(object3D);
        }
    }

    public void updateObject(Object3D oldValue, Object3D newValue) {
        Preconditions.checkArgument(oldValue.getClass() == newValue.getClass());
        if (oldValue instanceof Renderable) {
            Preconditions.checkArgument(objects.contains(oldValue));
            objects.set(objects.indexOf(oldValue), (Renderable) newValue);
        }
        if (oldValue instanceof LightSource) {
            Preconditions.checkArgument(lights.contains(oldValue));
            lights.set(lights.indexOf(oldValue), (LightSource) newValue);
        }
    }

    public void removeObject(Object3D object3D) {
        if (object3D instanceof Renderable) {
            objects.remove(object3D);
        }
        if (object3D instanceof LightSource) {
            lights.remove(object3D);
        }
    }

    public double getFov() {
        return fov.get();
    }

    @JsonIgnore
    public double getFovRadians() {
        return Math.toRadians(fov.get());
    }

    public void setFov(double fov) {
        this.fov.set(fov);
    }

    public DoubleProperty fovProperty() {
        return fov;
    }

    public boolean isAaEnabled() {
        return aaEnabled.get();
    }

    public boolean getAaEnabled() {
        return aaEnabled.get();
    }

    public void setAaEnabled(boolean aaEnabled) {
        this.aaEnabled.set(aaEnabled);
    }

    public BooleanProperty aaEnabledProperty() {
        return aaEnabled;
    }

    public Point3D getCameraOrigin() {
        return cameraOrigin.get();
    }

    public void setCameraOrigin(Point3D cameraOrigin) {
        this.cameraOrigin.set(cameraOrigin);
    }

    public ObjectProperty<Point3D> cameraOriginProperty() {
        return cameraOrigin;
    }

    public Object3D getSelected() {
        return selected.get();
    }

    public ObjectProperty<Object3D> selectedProperty() {
        return selected;
    }

    public void setSelected(Object3D selected) {
        this.selected.set(selected);
    }
}
