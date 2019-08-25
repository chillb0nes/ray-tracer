package com.example.renderer.model.light;

import com.example.renderer.model.object.Object3D;
import javafx.geometry.Point3D;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LightSource implements Object3D {

    private Point3D center;
    private double intensity;

}
