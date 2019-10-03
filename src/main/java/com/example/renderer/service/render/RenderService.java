package com.example.renderer.service.render;

import com.example.renderer.model.Scene;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.image.Image;

public class RenderService extends Service<Image> {

    private TaskAwareRenderer renderer;
    private Scene scene;

    public RenderService(TaskAwareRenderer renderer) {
        this.renderer = renderer;
    }

    public void render(Scene scene) {
        this.scene = scene;
        restart();
    }

    @Override
    protected Task<Image> createTask() {
        return new Task<Image>() {
            @Override
            protected Image call() {
                try {
                    return renderer.getImage(scene, this);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
        };
    }
}
