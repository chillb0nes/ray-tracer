package com.example.renderer.view.control;

import com.example.renderer.BaseJavaFXTest;
import com.example.renderer.model.Material;
import com.example.renderer.model.light.LightSource;
import com.example.renderer.model.object.Mesh;
import com.example.renderer.model.object.Sphere;
import com.example.renderer.model.object.Triangle;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import org.junit.Test;

import static org.junit.Assert.*;

public class ValueNodeTest extends BaseJavaFXTest {

    @Test
    public void testDoubleSpinner() {
        double value = 0.5;
        DoubleSpinner control = new DoubleSpinner();
        control.setValue(value);
        assertDoubleEquals(value, control.getValue());
    }

    @Test
    public void testPoint3DSpinner() {
        Point3D value = randomPoint3D();
        Point3DSpinner control = new Point3DSpinner();
        control.setValue(value);
        assertPoint3DEquals(value, control.getValue());

        assertDoubleEquals(value.getX(), control.getXSpinner().getValue());
        assertDoubleEquals(value.getY(), control.getYSpinner().getValue());
        assertDoubleEquals(value.getZ(), control.getZSpinner().getValue());
    }

    @Test
    public void testTriangleControl() {
        Triangle value = randomTriangle();
        TriangleControl control = new TriangleControl();
        control.setValue(value);
        assertEquals(value, control.getValue());

        assertEquals(value.getV0(), control.getV0Spinner().getValue());
        assertEquals(value.getV1(), control.getV1Spinner().getValue());
        assertEquals(value.getV2(), control.getV2Spinner().getValue());
    }

    @Test
    public void testMeshControl() {
        Mesh value = randomMesh();
        MeshControl control = new MeshControl();
        control.setValue(value);
        assertEquals(value, control.getValue());

        assertTrue(control.getListView().getItems().containsAll(value.getTriangles()));
    }

    @Test
    public void testSphereControl() {
        Sphere value = new Sphere(randomPoint3D(), 1, null);
        SphereControl control = new SphereControl();
        control.setValue(value);
        assertEquals(value, control.getValue());

        assertPoint3DEquals(value.getCenter(), control.getCenterSpinner().getValue());
        assertDoubleEquals(value.getRadius(), control.getRadiusSpinner().getValue());
    }

    @Test
    public void testLightSourceControl() {
        LightSource value = new LightSource(randomPoint3D(), 0.5);
        LightSourceControl control = new LightSourceControl();
        control.setValue(value);
        assertEquals(value, control.getValue());

        assertPoint3DEquals(value.getCenter(), control.getCenterSpinner().getValue());
        assertDoubleEquals(value.getIntensity(), control.getIntensitySpinner().getValue());
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
        assertDoubleEquals(value.getDiffuse(), control.getDiffuseSpinner().getValue());
        assertDoubleEquals(value.getSpecular(), control.getSpecularSpinner().getValue());
        assertDoubleEquals(value.getReflectivity(), control.getReflectivitySpinner().getValue());
        assertDoubleEquals(value.getTransmittance(), control.getTransmittanceSpinner().getValue());
        assertDoubleEquals(value.getSpecularExp(), control.getSpecularExpSpinner().getValue());
        assertDoubleEquals(value.getIor(), control.getIorSpinner().getValue());
    }
}