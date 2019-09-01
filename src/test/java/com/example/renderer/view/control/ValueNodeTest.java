package com.example.renderer.view.control;

import com.example.renderer.JavaFXThreadingRule;
import com.example.renderer.model.Material;
import com.example.renderer.model.light.LightSource;
import com.example.renderer.model.object.Mesh;
import com.example.renderer.model.object.Sphere;
import com.example.renderer.model.object.Triangle;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class ValueNodeTest {
    @Rule
    public JavaFXThreadingRule jfxRule = new JavaFXThreadingRule();

    @Test
    public void testDoubleSpinner() {
        double value = 0.5;
        DoubleSpinner control = new DoubleSpinner();
        control.setValue(value);
        assertEquals(value, control.getValue(), 0);
    }

    @Test
    public void testPoint3DSpinner() {
        Point3D value = new Point3D(1, 2, 3);
        Point3DSpinner control = new Point3DSpinner();
        control.setValue(value);
        assertEquals(value, control.getValue());

        assertEquals(value.getX(), control.getXSpinner().getValue(), 0);
        assertEquals(value.getY(), control.getYSpinner().getValue(), 0);
        assertEquals(value.getZ(), control.getZSpinner().getValue(), 0);
    }

    @Test
    public void testTriangleControl() {
        Triangle value = new Triangle(
                new Point3D(1, 2, 3),
                new Point3D(4, 5, 6),
                new Point3D(7, 8, 9)
        );
        TriangleControl control = new TriangleControl();
        control.setValue(value);
        assertEquals(value, control.getValue());

        assertEquals(value.getV0(), control.getV0Spinner().getValue());
        assertEquals(value.getV1(), control.getV1Spinner().getValue());
        assertEquals(value.getV2(), control.getV2Spinner().getValue());
    }

    @Test
    public void testMeshControl() {
        Mesh value = new Mesh();
        value.setTriangles(Arrays.asList(
                new Triangle(
                        new Point3D(1, 2, 3),
                        new Point3D(4, 5, 6),
                        new Point3D(7, 8, 9)
                ),
                new Triangle(
                        new Point3D(-1, -2, -3),
                        new Point3D(-4, -5, -6),
                        new Point3D(-7, -8, -9)
                )
        ));
        MeshControl control = new MeshControl();
        control.setValue(value);
        assertEquals(value, control.getValue());

        assertTrue(control.getListView().getItems().containsAll(value.getTriangles()));
    }

    @Test
    public void testSphereControl() {
        Sphere value = new Sphere(new Point3D(1, 2, 3), 1, null);
        SphereControl control = new SphereControl();
        control.setValue(value);
        assertEquals(value, control.getValue());

        assertEquals(value.getCenter(), control.getCenterSpinner().getValue());
        assertEquals(value.getRadius(), control.getRadiusSpinner().getValue(), 0);
    }

    @Test
    public void testLightSourceControl() {
        LightSource value = new LightSource(new Point3D(1, 2, 3), 0.5);
        LightSourceControl control = new LightSourceControl();
        control.setValue(value);
        assertEquals(value, control.getValue());

        assertEquals(value.getCenter(), control.getCenterSpinner().getValue());
        assertEquals(value.getIntensity(), control.getIntensitySpinner().getValue(), 0);
    }

    @Test
    public void testMaterialControl() {
        Material value = Material.builder()
                .color(Color.AQUA)
                .diffuse(0.1)
                .specular(0.2)
                .reflectivity(0.3)
                .transmittance(0.4)
                .specularExp(0.5)
                .ior(0.6)
                .build();
        MaterialControl control = new MaterialControl();
        control.setValue(value);
        assertEquals(value, control.getValue());

        assertEquals(value.getColor(), control.getColorPicker().getValue());
        assertEquals(value.getDiffuse(), control.getDiffuseSpinner().getValue(), 0);
        assertEquals(value.getSpecular(), control.getSpecularSpinner().getValue(), 0);
        assertEquals(value.getReflectivity(), control.getReflectivitySpinner().getValue(), 0);
        assertEquals(value.getTransmittance(), control.getTransmittanceSpinner().getValue(), 0);
        assertEquals(value.getSpecularExp(), control.getSpecularExpSpinner().getValue(), 0);
        assertEquals(value.getIor(), control.getIorSpinner().getValue(), 0);
    }
}