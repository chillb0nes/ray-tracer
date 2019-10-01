package com.example.renderer.model.object;

import com.example.renderer.model.Material;
import com.example.renderer.model.RayHit;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import lombok.Data;

import java.util.List;

import static java.util.Comparator.comparing;

@Data
public class Mesh extends Renderable {

    private ObjectProperty<ObservableList<Triangle>> triangles;
    private transient ReadOnlyObjectWrapper<Point3D> center;

    public Mesh() {
        setTriangles(FXCollections.observableArrayList());
    }

    public Mesh(List<Triangle> triangles) {
        setTriangles(FXCollections.observableList(triangles));
    }

    public Mesh(Triangle... triangles) {
        setTriangles(FXCollections.observableArrayList(triangles));
    }

    public Mesh(Material material) {
        this();
        setMaterial(material);
    }

    public Mesh(Material material, List<Triangle> triangles) {
        this(triangles);
        setMaterial(material);
    }

    public Mesh(Material material, Triangle... triangles) {
        this(triangles);
        setMaterial(material);
    }

    @Override
    public RayHit intersection(Point3D origin, Point3D direction) {
        return getTriangles().stream()
                .map(triangle -> triangle.intersection(origin, direction))
                .filter(RayHit::isHit)
                .min(comparing(RayHit::getDistance))
                .orElse(RayHit.MISS);
    }

    public Point3D getCenter() {
        return center.get();
    }

    public void setCenter(Point3D newPoint) {
        Point3D oldPoint = this.center.get();

        double deltaX = newPoint.getX() - oldPoint.getX();
        double deltaY = newPoint.getY() - oldPoint.getY();
        double deltaZ = newPoint.getZ() - oldPoint.getZ();

        getTriangles().forEach(triangle -> {
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

    public ReadOnlyProperty<Point3D> centerProperty() {
        return center.getReadOnlyProperty();
    }

    public void setMaterial(Material material) {
        this.material = material;
        bindMaterial();
    }

    private void bindMaterial() {
        getTriangles().forEach(triangle -> triangle.setMaterial(material));
    }

    public ObservableList<Triangle> getTriangles() {
        return triangles == null ? null : triangles.get();
    }

    public void setTriangles(ObservableList<Triangle> triangles) {
        trianglesProperty().set(triangles);
        bindCenter();
    }

    public ObjectProperty<ObservableList<Triangle>> trianglesProperty() {
        if (triangles == null) {
            triangles = new SimpleObjectProperty<>(this, "triangles");
        }
        return triangles;
    }

    private void bindCenter() {
        center = new ReadOnlyObjectWrapper<>();
        center.bind(Bindings.createObjectBinding(() -> {
            Point3D sum = Point3D.ZERO;
            for (Triangle triangle : getTriangles()) {
                sum = sum.add(triangle.getCenter());
            }
            return sum.multiply(1. / getTriangles().size());
        }, getTriangles()));
    }
}
