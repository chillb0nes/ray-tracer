package com.example.renderer.model.object;

import com.example.renderer.model.Material;
import com.example.renderer.model.RayHit;
import javafx.geometry.Point3D;
import lombok.Data;

@Data
public class Triangle extends Renderable {

    private Point3D v0;
    private Point3D v1;
    private Point3D v2;
    private Point3D center;

    public static final Triangle EMPTY = new Triangle(Point3D.ZERO, Point3D.ZERO, Point3D.ZERO);

    public Triangle(Point3D v0, Point3D v1, Point3D v2) {
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;
        center = v0.add(v1).add(v2).multiply(1. / 3);//todo binding
    }

    public Triangle(Point3D v0, Point3D v1, Point3D v2, Material material) {
        this(v0, v1, v2);
        this.material = material;
    }

    @Override
    public RayHit intersection(Point3D origin, Point3D direction) {
        /*Point3D ab = v1.subtract(v0);
        Point3D ac = v2.subtract(v0);
        Point3D normal = ab.crossProduct(ac);
        Point3D normal2 = ac.crossProduct(ab);

        boolean parallel = Math.abs(normal.dotProduct(direction)) < 0.001;
        if (parallel) return RayHit.MISS;

        double originToPlane = center.subtract(origin).dotProduct(normal);
        double originToIntersection = -(normal.dotProduct(origin) + originToPlane) / normal.dotProduct(direction);

        if (originToIntersection < 0) {
            // Triangle is in behind the ray
            return RayHit.MISS;
        }

        Point3D intersectionPoint = origin.add(direction.multiply(originToIntersection));
        Point3D perpendicular;

        Point3D edge0 = v1.subtract(v0);
        Point3D verticleToIntersection0 = intersectionPoint.subtract(v0);
        perpendicular = edge0.crossProduct(verticleToIntersection0);
        if (normal.dotProduct(perpendicular) < 0) {
            return RayHit.MISS;
        }

        Point3D edge1 = v2.subtract(v1);
        Point3D verticleToIntersection1 = intersectionPoint.subtract(v1);
        perpendicular = edge1.crossProduct(verticleToIntersection1);
        if (normal.dotProduct(perpendicular) < 0) {
            return RayHit.MISS;
        }

        Point3D edge2 = v0.subtract(v2);
        Point3D verticleToIntersection2 = intersectionPoint.subtract(v2);
        perpendicular = edge2.crossProduct(verticleToIntersection2);
        if (normal.dotProduct(perpendicular) < 0) {
            return RayHit.MISS;
        }

        return new RayHit(true, originToIntersection, intersectionPoint, normal2.normalize());*/

        Point3D e0 = v1.subtract(v0);
        Point3D e1 = v2.subtract(v0);
        Point3D pvec = direction.crossProduct(e1);
        double det = e0.dotProduct(pvec);

        if (det < 1e-8 && det > -1e-8) {
            return RayHit.MISS;
        }

        double invDet = 1 / det;
        Point3D tvec = origin.subtract(v0);
        double u = tvec.dotProduct(pvec) * invDet;
        if (u < 0 || u > 1) {
            return RayHit.MISS;
        }

        Point3D qvec = tvec.crossProduct(e0);
        double v = direction.dotProduct(qvec) * invDet;
        if (v < 0 || u + v > 1) {
            return RayHit.MISS;
        }

        double distance = e1.dotProduct(qvec) * invDet;
        if (distance < 0) {
            return RayHit.MISS;
        }

        Point3D normal = e1.crossProduct(e0).normalize();
        if (direction.dotProduct(normal) > 0) {
            //because triangles are double-sided
            normal = normal.multiply(-1);
        }

        return new RayHit(
                true,
                this,
                distance,
                origin.add(direction.multiply(distance)),
                normal);
    }
}
