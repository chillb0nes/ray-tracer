package com.example.renderer.service;

import com.example.renderer.BaseTest;
import com.example.renderer.model.Material;
import com.example.renderer.model.Scene;
import com.example.renderer.model.light.LightSource;
import com.example.renderer.model.object.Mesh;
import com.example.renderer.model.object.Sphere;
import com.example.renderer.model.object.Triangle;
import com.example.renderer.service.serialization.SerializationService;
import javafx.geometry.Point3D;
import org.junit.Test;

public class SerializationServiceTest extends BaseTest {

    private static SerializationService serializationService = new SerializationService();

    private static <T> void assertCopyEquals(T value) {
        T copy = serializationService.copy(value);
        assertEquals(value, copy);
    }

    @Test
    public void testPoint3DSerialization() throws Exception {
        Point3D point3D = randomPoint3D();
        assertCopyEquals(point3D);
    }

    @Test
    public void testMaterialSerialization() throws Exception {
        Material material = Material.random();
        assertCopyEquals(material);
    }

    @Test
    public void testTriangleSerialization() throws Exception {
        Triangle triangle = randomTriangle();
        triangle.setMaterial(Material.random());
        assertCopyEquals(triangle);
    }

    @Test
    public void testMeshSerialization() throws Exception {
        Mesh mesh = randomMesh();
        mesh.setMaterial(Material.random());
        assertCopyEquals(mesh);
    }

    @Test
    public void testSphereSerialization() throws Exception {
        Sphere sphere = new Sphere(randomPoint3D(), random.nextDouble());
        sphere.setMaterial(Material.random());
        assertCopyEquals(sphere);
    }

    @Test
    public void testLightSourceSerialization() throws Exception {
        LightSource lightSource = new LightSource(randomPoint3D(), random.nextDouble());
        assertCopyEquals(lightSource);
    }

    @Test
    public void testSceneSerialization() throws Exception {
        Scene scene = new Scene();
        for (int i = 0; i < random.nextInt(100) + 10; i++) {
            switch (random.nextInt(4)) {
                case 0:
                    Sphere sphere = new Sphere(randomPoint3D(), random.nextDouble());
                    sphere.setMaterial(Material.random());
                    scene.addObject(sphere);
                    break;
                case 1:
                    Triangle triangle = randomTriangle();
                    triangle.setMaterial(Material.random());
                    scene.addObject(triangle);
                    break;
                case 2:
                    Mesh mesh = randomMesh();
                    mesh.setMaterial(Material.random());
                    scene.addObject(mesh);
                    break;
                case 3:
                    LightSource lightSource = new LightSource(randomPoint3D(), random.nextDouble());
                    scene.addObject(lightSource);
                    break;
            }
        }
        assertCopyEquals(scene);
    }
}