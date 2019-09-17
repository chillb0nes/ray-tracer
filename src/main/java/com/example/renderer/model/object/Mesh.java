package com.example.renderer.model.object;

import com.example.renderer.model.Material;
import com.example.renderer.model.RayHit;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import lombok.Data;

import java.util.List;

import static java.util.Comparator.comparing;

@Data
public class Mesh extends Renderable {

    private ObservableList<Triangle> triangles;
    private transient ReadOnlyObjectWrapper<Point3D> center;

    public Mesh() {
        triangles = FXCollections.observableArrayList();
        bindCenter();
    }

    public Mesh(List<Triangle> triangles) {
        this.triangles = FXCollections.observableList(triangles);
        bindCenter();
    }

    public Mesh(Triangle... triangles) {
        this.triangles = FXCollections.observableArrayList(triangles);
        bindCenter();
    }

    public Mesh(Material material) {
        this();
        this.material = material;
        bindMaterial();
    }

    public Mesh(Material material, List<Triangle> triangles) {
        this(triangles);
        this.material = material;
        bindMaterial();
    }

    public Mesh(Material material, Triangle... triangles) {
        this(triangles);
        this.material = material;
        bindMaterial();
    }

    public void setTriangles(ObservableList<Triangle> triangles) {
        this.triangles = triangles;
        bindCenter();
    }

    private void bindCenter() {
        center = new ReadOnlyObjectWrapper<>();
        center.bind(Bindings.createObjectBinding(() -> {
            Point3D sum = Point3D.ZERO;
            for (Triangle triangle : triangles) {
                sum = sum.add(triangle.getCenter());
            }
            return sum.multiply(1. / triangles.size());
        }, triangles));
    }

    private void bindMaterial() {
        triangles.forEach(triangle -> triangle.setMaterial(material));
    }

    @Override
    public RayHit intersection(Point3D origin, Point3D direction) {
        return triangles.stream()
                .map(triangle -> triangle.intersection(origin, direction))
                .filter(RayHit::isHit)
                .min(comparing(RayHit::getDistance))
                .orElse(RayHit.MISS);
    }

    public Point3D getCenter() {
        return center.get();
    }

    public ReadOnlyProperty<Point3D> centerProperty() {
        return center.getReadOnlyProperty();
    }

    public void setCenter(Point3D newPoint) {
        Point3D oldPoint = this.center.get();

        double deltaX = newPoint.getX() - oldPoint.getX();
        double deltaY = newPoint.getY() - oldPoint.getY();
        double deltaZ = newPoint.getZ() - oldPoint.getZ();

        triangles.forEach(triangle -> {
            Point3D v0 = triangle.getV0();
            Point3D v1 = triangle.getV1();
            Point3D v2 = triangle.getV2();

            triangle.setV0(new Point3D(
                    v0.getX() + deltaX,
                    v0.getY() + deltaY,
                    v0.getZ() + deltaZ));

            triangle.setV1(new Point3D(
                    v1.getX() + deltaX,
                    v1.getY() + deltaY,
                    v1.getZ() + deltaZ));

            triangle.setV2(new Point3D(
                    v2.getX() + deltaX,
                    v2.getY() + deltaY,
                    v2.getZ() + deltaZ));
        });
    }
}
