package com.example.renderer.service.render;

import com.example.renderer.model.Scene;
import javafx.concurrent.Task;
import javafx.scene.image.Image;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

public interface TaskAwareExecutorRenderer {

    Image getImage(final Scene scene, Task task) throws InterruptedException;

    default ExecutorService getExecutor() {
        return new ForkJoinPool(
                Runtime.getRuntime().availableProcessors(),
                ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                (t, e) -> t.interrupt(),
                true);
    }

}
