package com.example.renderer.model.object;

import com.example.renderer.model.Material;
import com.example.renderer.model.RayHit;
import javafx.geometry.Point3D;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static java.lang.Math.sqrt;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Sphere extends Renderable {

    private Point3D center;
    private double radius;

    public Sphere(Point3D center, double radius, Material material) {
        this.center = center;
        this.radius = radius;
        this.material = material;
    }

    @Override
    public RayHit intersection(Point3D origin, Point3D direction) {
        Point3D originToCenter = center.subtract(origin);
        double projection = originToCenter.dotProduct(direction);
        if (projection < 0) {
            return RayHit.MISS;
        }

        double centerToProjection = sqrt(originToCenter.dotProduct(originToCenter) - projection * projection);
        if (centerToProjection > radius) {
            return RayHit.MISS;
        }

        double intersection = sqrt(radius * radius - centerToProjection * centerToProjection);
        double originToIntersection0 = projection - intersection;
        double originToIntersection1 = projection + intersection;

        if (originToIntersection0 < 0) {
            // Camera is inside the sphere
            //originToIntersection0 = originToIntersection1;
            return RayHit.MISS;
        }

        return new RayHit(
                true,
                this,
                originToIntersection0,
                origin.add(direction.multiply(originToIntersection0)),
                origin.add(direction.multiply(originToIntersection0)).subtract(center).normalize());
    }
}
