package com.example.renderer.service.render;

import com.example.renderer.model.Scene;
import javafx.concurrent.Task;
import javafx.scene.image.Image;

public interface TaskAwareRenderer {

    Image getImage(final Scene scene, Task task) throws InterruptedException;

}
