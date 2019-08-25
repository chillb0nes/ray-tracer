package com.example.renderer.service;

import com.example.renderer.model.Scene;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class RenderService extends Service<Image> {

    private RayTracer rayTracer = new RayTracer();
    private Scene scene;
    private Map<Scene, WritableImage> cache = new HashMap<>(); //todo: cache, size ~100

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
                    WritableImage image = rayTracer.renderScene(scene, this);

                    if (!scene.getSelected().isEmpty()) {
                        Image outline = rayTracer.renderOutline(scene, this);
                        PixelReader pixelReader = outline.getPixelReader();
                        for (int j = 0; j < image.getHeight(); j++) {
                            for (int i = 0; i < image.getWidth(); i++) {
                                if (isCancelled()) {
                                    break;
                                }
                                if (pixelReader.getColor(i, j).isOpaque()) {
                                    image.getPixelWriter().setColor(i, j, pixelReader.getColor(i, j));
                                }
                            }
                        }
                    }
                    return image;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return null;
            }
        };
    }
}
