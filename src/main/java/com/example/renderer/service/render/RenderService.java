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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class RenderService extends Service<Image> {

    private TaskAwareExecutorRenderer renderer;
    private TaskAwareExecutorRenderer selectionRenderer;
    private Scene scene;
    private Cache<Scene, Image> cache;

    @Autowired
    public RenderService(@Qualifier("defaultRayTracer") TaskAwareExecutorRenderer renderer,
                         @Qualifier("outlineRayTracer") TaskAwareExecutorRenderer selectionRenderer) {
        this.renderer = renderer;
        this.selectionRenderer = selectionRenderer;
        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();
    }

    @PostConstruct
    private void init() throws ReflectiveOperationException {
        final Field EXECUTOR = getClass().getSuperclass().getDeclaredField("EXECUTOR");
        EXECUTOR.setAccessible(true);
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) EXECUTOR.get(this);
        setExecutor(Executors.newFixedThreadPool(1, threadPoolExecutor.getThreadFactory()));
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
