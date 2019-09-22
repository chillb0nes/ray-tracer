package com.example.renderer.service;

import com.example.renderer.BaseTest;
import com.example.renderer.model.Material;
import com.example.renderer.model.Scene;
import com.example.renderer.model.light.LightSource;
import com.example.renderer.model.object.Mesh;
import com.example.renderer.model.object.Renderable;
import com.example.renderer.model.object.Sphere;
import com.example.renderer.model.object.Triangle;
import javafx.geometry.Point3D;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SerializationServiceTest extends BaseTest {

    private SerializationService serializationService = new SerializationService();

    @Test
    public void testSimplePoint3DSerialization() throws Exception {
        Point3D point3D = randomPoint3D();
        assertPoint3DEquals(point3D, serializationService.copy(point3D));
    }

    @Test
    public void testSimpleMaterialSerialization() throws Exception {
        Material material = Material.random();
        assertEquals(material, serializationService.copy(material));
    }

    @Test
    public void testSimpleTriangleSerialization() throws Exception {
        Triangle triangle = randomTriangle();
        triangle.setMaterial(Material.random());
        assertEquals(triangle, serializationService.copy(triangle));
    }

    @Test
    public void testSimpleMeshSerialization() throws Exception {
        Mesh mesh = randomMesh();
        mesh.setMaterial(Material.random());
        assertEquals(mesh, serializationService.copy(mesh));
    }

    @Test
    public void testSimpleSphereSerialization() throws Exception {
        Sphere sphere = new Sphere(randomPoint3D(), random.nextDouble());
        sphere.setMaterial(Material.random());
        assertEquals(sphere, serializationService.copy(sphere));
    }

    @Test
    public void testSimpleLightSourceSerialization() throws Exception {
        LightSource lightSource = new LightSource(randomPoint3D(), random.nextDouble());
        assertEquals(lightSource, serializationService.copy(lightSource));
    }

    @Test
    public void testSceneSerialization() throws Exception {
        Scene scene = new Scene();
        randomLoop(0, () -> {
            Renderable renderable;
            switch (random.nextInt(2)) {
                case 0:
                    renderable = new Sphere(randomPoint3D(), random.nextDouble());
                    break;
                case 1:
                    renderable = randomTriangle();
                    break;
                default:
                    renderable = randomMesh();
                    break;
            }
            renderable.setMaterial(Material.random());
            scene.addObject(renderable);
        });
        randomLoop(0, () -> scene.addObject(new LightSource(randomPoint3D(), random.nextDouble())));
        assertEquals(scene, serializationService.copy(scene));
    }

}