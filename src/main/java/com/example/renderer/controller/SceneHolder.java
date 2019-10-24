package com.example.renderer.controller;

import com.example.renderer.model.Scene;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

import static com.example.renderer.view.util.ObservableUtils.addListener;

@Log4j2
@Component
public class SceneHolder {
    private ObjectProperty<Scene> scene;
    private List<Runnable> listeners;

    public SceneHolder() {
        this.scene = new SimpleObjectProperty<>();
        this.listeners = new LinkedList<>();
    }

    public Scene getScene() {
        return scene.get();
    }

    public ObjectProperty<Scene> sceneProperty() {
        return scene;
    }

    public void setScene(Scene scene) {
        sceneProperty().set(scene);
        addSceneListeners(scene);
        requestUpdate();
    }

    private void addSceneListeners(Scene scene) {
        addListener(scene.widthProperty(), newValue -> requestUpdate());
        addListener(scene.heightProperty(), newValue -> requestUpdate());
        addListener(scene.fovProperty(), newValue -> requestUpdate());
        addListener(scene.aaEnabledProperty(), newValue -> requestUpdate());
        addListener(scene.cameraOriginProperty(), newValue -> requestUpdate());
        addListener(scene.selectedProperty(), newValue -> requestUpdate());
        addListener(scene.getObjects(), change -> requestUpdate());
        addListener(scene.getLights(), change -> requestUpdate());
    }

    public void registerListener(Runnable runnable) {
        listeners.add(runnable);
    }

    public void requestUpdate() {
        log.trace("Update requested, running {} listeners", listeners.size());
        listeners.forEach(Runnable::run);
    }
}
