package com.example.renderer.model.object;

import com.example.renderer.BaseTest;
import com.example.renderer.service.SerializationService;
import javafx.geometry.Point3D;
import org.junit.Test;

import java.io.IOException;

public class CenterBindingTest extends BaseTest {

    @Test
    public void testSetCenterTriangle() {
        Point3D v0 = randomPoint3D();
        Point3D v1 = randomPoint3D();
        Point3D v2 = randomPoint3D();
        Triangle triangle = new Triangle(v0, v1, v2);

        double expectedX = (v0.getX() + v1.getX() + v2.getX()) / 3;
        double expectedY = (v0.getY() + v1.getY() + v2.getY()) / 3;
        double expectedZ = (v0.getZ() + v1.getZ() + v2.getZ()) / 3;
        
        Point3D expectedCenter = new Point3D(expectedX, expectedY, expectedZ);
        assertEquals(expectedCenter, triangle.getCenter());

        Point3D newCenter = randomPoint3D();
        triangle.setCenter(newCenter);

        double deltaX = newCenter.getX() - expectedX;
        double deltaY = newCenter.getY() - expectedY;
        double deltaZ = newCenter.getZ() - expectedZ;

        assertEquals(deltaX, triangle.getV0().getX() - v0.getX());
        assertEquals(deltaY, triangle.getV0().getY() - v0.getY());
        assertEquals(deltaZ, triangle.getV0().getZ() - v0.getZ());

        assertEquals(deltaX, triangle.getV1().getX() - v1.getX());
        assertEquals(deltaY, triangle.getV1().getY() - v1.getY());
        assertEquals(deltaZ, triangle.getV1().getZ() - v1.getZ());

        assertEquals(deltaX, triangle.getV2().getX() - v2.getX());
        assertEquals(deltaY, triangle.getV2().getY() - v2.getY());
        assertEquals(deltaZ, triangle.getV2().getZ() - v2.getZ());
    }

    @Test
    public void testSetCenterMesh() {
        Mesh mesh = randomMesh();

        Point3D sum = Point3D.ZERO;
        for (Triangle triangle : mesh.getTriangles()) {
            sum = sum.add(triangle.getCenter());
        }
        Point3D expectedCenter = sum.multiply(1. / mesh.getTriangles().size());
        assertEquals(expectedCenter, mesh.getCenter());

        Point3D newCenter = randomPoint3D();
        Mesh newMesh = new SerializationService().copy(mesh);
        newMesh.setCenter(newCenter);

        double deltaX = newCenter.getX() - expectedCenter.getX();
        double deltaY = newCenter.getY() - expectedCenter.getY();
        double deltaZ = newCenter.getZ() - expectedCenter.getZ();

        for (int i = 0; i < mesh.getTriangles().size(); i++) {
            Triangle oldTriangle = mesh.getTriangles().get(i);
            Triangle newTriangle = newMesh.getTriangles().get(i);

            assertEquals(deltaX, newTriangle.getV0().getX() - oldTriangle.getV0().getX());
            assertEquals(deltaY, newTriangle.getV0().getY() - oldTriangle.getV0().getY());
            assertEquals(deltaZ, newTriangle.getV0().getZ() - oldTriangle.getV0().getZ());

            assertEquals(deltaX, newTriangle.getV1().getX() - oldTriangle.getV1().getX());
            assertEquals(deltaY, newTriangle.getV1().getY() - oldTriangle.getV1().getY());
            assertEquals(deltaZ, newTriangle.getV1().getZ() - oldTriangle.getV1().getZ());

            assertEquals(deltaX, newTriangle.getV2().getX() - oldTriangle.getV2().getX());
            assertEquals(deltaY, newTriangle.getV2().getY() - oldTriangle.getV2().getY());
            assertEquals(deltaZ, newTriangle.getV2().getZ() - oldTriangle.getV2().getZ());
        }
    }

}