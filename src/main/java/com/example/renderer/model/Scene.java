package com.example.renderer.model;

import com.example.renderer.model.light.LightSource;
import com.example.renderer.model.object.Object3D;
import com.example.renderer.model.object.Renderable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

@Data
@Log4j2
public class Scene {

    private IntegerProperty width;
    private IntegerProperty height;
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
        this.width = new SimpleIntegerProperty(width);
        this.height = new SimpleIntegerProperty(height);

        fov = new SimpleDoubleProperty(45);
        aaEnabled = new SimpleBooleanProperty();
        cameraOrigin = new SimpleObjectProperty<>(Point3D.ZERO);
        selected = new SimpleObjectProperty<>();

        objects = FXCollections.observableArrayList();
        lights = FXCollections.observableArrayList();
    }

    public int getWidth() {
        return isAaEnabled() ? width.get() * 2 : width.get();
    }

    public int getHeight() {
        return isAaEnabled() ? height.get() * 2 : height.get();
    }

    public IntegerProperty widthProperty() {
        return width;
    }

    public void setWidth(int width) {
        this.width.set(width);
    }

    public IntegerProperty heightProperty() {
        return height;
    }

    public void setHeight(int height) {
        this.height.set(height);
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
