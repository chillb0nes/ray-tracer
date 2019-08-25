package com.example.renderer.model.object;

import com.example.renderer.model.Material;
import com.example.renderer.model.RayHit;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

public interface Renderable extends Object3D {

    Material getMaterial();

    void setMaterial(Material material);

    RayHit intersection(Point3D origin, Point3D direction);

}
