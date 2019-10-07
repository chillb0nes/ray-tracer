package com.example.renderer.service.render;

import com.example.renderer.model.Scene;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class RenderService extends Service<Image> {

    private TaskAwareExecutorRenderer renderer;
    private TaskAwareExecutorRenderer selectionRenderer;
    private Scene scene;
    private Cache<Scene, Image> cache;

    public RenderService(TaskAwareExecutorRenderer renderer,
                         TaskAwareExecutorRenderer selectionRenderer) {
        this.renderer = renderer;
        this.selectionRenderer = selectionRenderer;
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
                    Image image = cache.get(scene, () -> renderer.getImage(scene, this));
                    Image selection = selectionRenderer.getImage(scene, this);

                    int width = scene.getWidth();
                    int height = scene.getHeight();
                    WritableImage toReturn = new WritableImage(image.getPixelReader(), width, height);
                    PixelReader reader = selection.getPixelReader();
                    PixelWriter writer = toReturn.getPixelWriter();

                    for (int j = 0; j < height; j++) {
                        for (int i = 0; i < width; i++) {
                            Color color = reader.getColor(i, j);
                            if (!Color.TRANSPARENT.equals(color)) {
                                writer.setColor(i, j, color);
                            }
                        }
                    }
                    return toReturn;
                } catch (ExecutionException | InterruptedException e) {
                    return null;
                }
            }
        };
    }
}
