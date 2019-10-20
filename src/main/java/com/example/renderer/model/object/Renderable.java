package com.example.renderer.model.object;

import com.example.renderer.model.Material;
import com.example.renderer.model.RayHit;
import javafx.geometry.Point3D;
import lombok.Data;

@Data
public abstract class Renderable implements Object3D {

    protected Material material;

    public abstract RayHit intersection(Point3D origin, Point3D direction);

}
