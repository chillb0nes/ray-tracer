package com.example.renderer;

import com.example.renderer.model.Material;
import com.example.renderer.model.object.Mesh;
import com.example.renderer.model.object.Triangle;
import javafx.geometry.Point3D;
import org.junit.Assert;

import java.util.Random;

public class BaseTest {

    protected static Random random = new Random();

    protected static Point3D randomPoint3D() {
        return new Point3D(
                random.nextDouble() * 10,
                random.nextDouble() * 10,
                random.nextDouble() * 10);
    }

    protected static Triangle randomTriangle() {
        return new Triangle(randomPoint3D(), randomPoint3D(), randomPoint3D());
    }

    protected static Mesh randomMesh() {
        Mesh mesh = new Mesh();
        randomLoop(10, () -> mesh.getTriangles().add(randomTriangle()));
        mesh.setMaterial(Material.random());
        return mesh;
    }

    protected static void randomLoop(int min, Runnable runnable) {
        for (int i = 0; i < random.nextInt(100) + min; i++) {
            runnable.run();
        }
    }

    protected static void assertDoubleEquals(double expected, double actual) {
        Assert.assertEquals(expected, actual, 0.000001);
    }

    protected static void assertPoint3DEquals(Point3D expected, Point3D actual) {
        assertDoubleEquals(expected.getX(), actual.getX());
        assertDoubleEquals(expected.getY(), actual.getY());
        assertDoubleEquals(expected.getZ(), actual.getZ());
    }

}
