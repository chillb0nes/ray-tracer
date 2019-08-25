package com.example.renderer.model.object;

import com.example.renderer.model.Material;
import com.example.renderer.model.RayHit;
import javafx.geometry.Point3D;
import lombok.Data;

import java.util.List;

@Data
public class Mesh implements Renderable {
    private Material material;
    private Point3D center;
    @UIParameter
    private List<Triangle> triangles;

    @Override
    public RayHit intersection(Point3D origin, Point3D direction) {
        return RayHit.MISS;
    }
}
