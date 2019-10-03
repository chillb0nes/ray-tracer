package com.example.renderer.service.render;

import com.example.renderer.model.Scene;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class RenderService extends Service<Image> {

    private TaskAwareRenderer renderer;
    private Scene scene;
    private Cache<Scene, Image> cache;

    public RenderService(TaskAwareRenderer renderer) {
        this.renderer = renderer;
        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();
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
                    return cache.get(scene, () -> renderer.getImage(scene, this));
                } catch (ExecutionException e) {
                    return null;
                }
            }
        };
    }
}
