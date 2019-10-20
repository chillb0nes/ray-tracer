package com.example.renderer;

import com.example.renderer.model.Scene;
import com.example.renderer.model.light.LightSource;
import com.example.renderer.model.object.Mesh;
import com.example.renderer.model.object.Sphere;
import com.example.renderer.model.object.Triangle;
import javafx.geometry.Point3D;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import java.util.Random;

public class BaseTest {

    protected static Random random = new Random();

    protected static Point3D randomPoint3D() {
        return new Point3D(
                (double) Math.round(random.nextDouble() * 10_000_000_000d) / 1_000_000_000d,
                (double) Math.round(random.nextDouble() * 10_000_000_000d) / 1_000_000_000d,
                (double) Math.round(random.nextDouble() * 10_000_000_000d) / 1_000_000_000d);
    }

    protected static Triangle randomTriangle() {
        return new Triangle(randomPoint3D(), randomPoint3D(), randomPoint3D());
    }

    protected static Mesh randomMesh() {
        Mesh mesh = new Mesh();
        for (int i = 0; i < random.nextInt(100) + 10; i++) {
            mesh.getTriangles().add(randomTriangle());
        }
        return mesh;
    }

    // to handle double fields
    protected static <T> void assertEquals(String message, T expected, T actual) {
        if (StringUtils.isEmpty(message)) {
            message = "at root";
        }
        if (expected instanceof Double) {
            assertDoubleEquals(message, (Double) expected, (Double) actual);
        } else if (expected instanceof Point3D) {
            assertPoint3DEquals(message, (Point3D) expected, (Point3D) actual);
        } else if (expected instanceof Triangle) {
            assertTriangleEquals(message, (Triangle) expected, (Triangle) actual);
        } else if (expected instanceof Mesh) {
            assertMeshEquals(message, (Mesh) expected, (Mesh) actual);
        } else if (expected instanceof Sphere) {
            assertSphereEquals(message, (Sphere) expected, (Sphere) actual);
        } else if (expected instanceof LightSource) {
            assertLightSourceEquals(message, (LightSource) expected, (LightSource) actual);
        } else if (expected instanceof Scene) {
            assertSceneEquals(message, (Scene) expected, (Scene) actual);
        } else {
            Assert.assertEquals(message, expected, actual);
        }
    }

    protected static <T> void assertEquals(T expected, T actual) {
        assertEquals(null, expected, actual);
    }

    private static void assertDoubleEquals(String message, double expected, double actual) {
        Assert.assertEquals(message, expected, actual, 0.000_000_000_1);
    }

    private static void assertPoint3DEquals(String message, Point3D expected, Point3D actual) {
        assertDoubleEquals(join(message, "X"), expected.getX(), actual.getX());
        assertDoubleEquals(join(message, "Y"), expected.getY(), actual.getY());
        assertDoubleEquals(join(message, "Z"), expected.getZ(), actual.getZ());
    }

    private static void assertTriangleEquals(String message, Triangle expected, Triangle actual) {
        assertPoint3DEquals(join(message, "V0"), expected.getV0(), actual.getV0());
        assertPoint3DEquals(join(message, "V1"), expected.getV1(), actual.getV1());
        assertPoint3DEquals(join(message, "V2"), expected.getV2(), actual.getV2());
        assertPoint3DEquals(join(message, "center"), expected.getCenter(), actual.getCenter());
        Assert.assertEquals(expected.getMaterial(), actual.getMaterial());
    }

    private static void assertMeshEquals(String message, Mesh expected, Mesh actual) {
        expected.getTriangles().forEach(triangle -> {
            int i = expected.getTriangles().indexOf(triangle);
            String join = join(message, String.format("triangle[%d]", i));
            assertTriangleEquals(join, triangle, actual.getTriangles().get(i));
        });
        assertPoint3DEquals(join(message, "center"), expected.getCenter(), actual.getCenter());
        Assert.assertEquals(expected.getMaterial(), actual.getMaterial());
    }

    private static void assertSphereEquals(String message, Sphere expected, Sphere actual) {
        assertPoint3DEquals(join(message, "center"), expected.getCenter(), actual.getCenter());
        assertDoubleEquals(join(message, "radius"), expected.getRadius(), actual.getRadius());
        Assert.assertEquals(expected.getMaterial(), actual.getMaterial());
    }

    private static void assertLightSourceEquals(String message, LightSource expected, LightSource actual) {
        assertPoint3DEquals(join(message, "center"), expected.getCenter(), actual.getCenter());
        assertDoubleEquals(join(message, "intensity"), expected.getIntensity(), actual.getIntensity());
    }

    private static void assertSceneEquals(String message, Scene expected, Scene actual) {
        assertEquals(join(message, "width"), expected.getWidth(), actual.getWidth());
        assertEquals(join(message, "height"), expected.getHeight(), actual.getHeight());
        assertEquals(join(message, "fov"), expected.getFov(), actual.getFov());
        assertEquals(join(message, "aa"), expected.isAaEnabled(), actual.isAaEnabled());
        assertEquals(join(message, "selected"), expected.getSelected(), actual.getSelected());
        assertEquals(join(message, "cameraOrigin"), expected.getCameraOrigin(), actual.getCameraOrigin());

         /*expected.getObjectsList().forEach(object3d -> {
            int i = expected.getObjectsList().indexOf(object3d);
            String join = join(message, String.format("object[%d %s]", i, object3d.getClass()));
            assertEquals(join, object3d, actual.getObjectsList().get(i));
        });*/
    }

    private static String join(String s1, String s2) {
        return s1 + " -> " + s2;
    }
}
