package com.example.renderer.model;

import com.example.renderer.model.object.Renderable;
import javafx.geometry.Point3D;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RayHit {

    private boolean hit;
    private Renderable hitObject;
    private double distance;
    private Point3D hitPoint;
    private Point3D normal;

    // I guess they never miss, huh?
    public static final RayHit MISS = new RayHit(false, null, Double.NaN, null, null);
}
