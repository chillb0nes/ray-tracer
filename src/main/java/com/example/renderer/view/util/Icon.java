package com.example.renderer.view.util;

import com.example.renderer.model.light.LightSource;
import com.example.renderer.model.object.Mesh;
import com.example.renderer.model.object.Object3D;
import com.example.renderer.model.object.Sphere;
import com.example.renderer.model.object.Triangle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
@RequiredArgsConstructor
public enum Icon {
    LIGHT_SOURCE(new Image("/icons/icons8-light-on-filled-24.png"), LightSource.class),
    SPHERE      (new Image("/icons/icons8-sphere-24.png"), Sphere.class),
    TRIANGLE    (new Image("/icons/icons8-triangle-filled-30.png"), Triangle.class),
    MESH        (new Image("/icons/icons8-orthogonal-view-filled-24.png"), Mesh.class),
    ADD         (new Image("/icons/icons8-сложение-90.png"))
    ;

    @Getter
    private final Image image;
    private Class clazz;

    public static Icon forClass(Class<?> clazz) {
        return Arrays.stream(values())
                .filter(icon -> clazz.equals(icon.clazz))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public ImageView get() {
        return new ImageView(image);
    }

    public ImageView withSize(double size) {
        ImageView icon = new ImageView(image);
        icon.setFitWidth(size);
        icon.setFitHeight(size);
        return icon;
    }
}
