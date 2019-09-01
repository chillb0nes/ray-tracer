package com.example.renderer.model.object;

import com.example.renderer.model.Material;
import com.example.renderer.model.RayHit;
import com.google.common.collect.Lists;
import javafx.geometry.Point3D;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static java.util.Comparator.comparing;

@Data
@NoArgsConstructor
public class Mesh extends Renderable {

    private Point3D center;
    private List<Triangle> triangles = Lists.newArrayList();

    public Mesh(List<Triangle> triangles, Material material) {
        this.triangles = triangles;
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
