package com.example.renderer.service.render;

import com.example.renderer.model.Scene;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RenderService extends Service<Image> {

    private TaskAwareRenderer renderer;
    private TaskAwareRenderer selectionRenderer;
    private Map<Scene, Image> cache;
    private Scene scene;

    @Autowired
    public RenderService(@Qualifier("defaultRayTracer") TaskAwareRenderer renderer,
                         @Qualifier("outlineRayTracer") TaskAwareRenderer selectionRenderer) {
        this.renderer = renderer;
        this.selectionRenderer = selectionRenderer;
        this.cache = new ConcurrentHashMap<>();
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
                Image image = cache.computeIfAbsent(scene, scene -> renderer.getImage(scene, this));
                if (image == null || scene.getSelected() == null) {
                    return image;
                } else {
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
                }
            }
        };
    }
}
