package com.example.renderer.model.object;

import com.example.renderer.model.Material;
import com.example.renderer.model.RayHit;
import com.google.common.collect.Lists;
import javafx.geometry.Point3D;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

import static java.util.Comparator.comparing;

@Data
public class Mesh extends Renderable {

    private Point3D center;
    private List<Triangle> triangles;

    public Mesh() {
        triangles = Lists.newArrayList();
    }

    public Mesh(List<Triangle> triangles) {
        this.triangles = triangles;
    }

    public Mesh(Triangle... triangles) {
        this.triangles = Arrays.asList(triangles);
    }

    public Mesh(Material material) {
        this();
        this.material = material;
    }

    public Mesh(Material material, List<Triangle> triangles) {
        this(triangles);
        this.material = material;
    }

    public Mesh(Material material, Triangle... triangles) {
        this(triangles);
        this.material = material;
    }

    @Override
    public RayHit intersection(Point3D origin, Point3D direction) {
        return triangles.stream()
                .map(triangle -> triangle.intersection(origin, direction))
                .filter(RayHit::isHit)
                .min(comparing(RayHit::getDistance))
                .orElse(RayHit.MISS);
    }
}
