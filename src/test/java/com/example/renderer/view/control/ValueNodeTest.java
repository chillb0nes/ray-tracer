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
        assertDoubleEquals(0, control.getValue());
        control.setValue(value);
        assertDoubleEquals(value, control.getValue());
    }

    @Test
    public void testPoint3DSpinner() {
        Point3D value = randomPoint3D();
        Point3DSpinner control = new Point3DSpinner();

        assertDoubleEquals(control.getDefaultValue().getX(), control.getXSpinner().getValue());
        assertDoubleEquals(control.getDefaultValue().getY(), control.getYSpinner().getValue());
        assertDoubleEquals(control.getDefaultValue().getZ(), control.getZSpinner().getValue());

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

        assertEquals(control.getDefaultValue().getV0(), control.getV0Spinner().getValue());
        assertEquals(control.getDefaultValue().getV1(), control.getV1Spinner().getValue());
        assertEquals(control.getDefaultValue().getV2(), control.getV2Spinner().getValue());

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

        assertTrue(control.getListView().getItems().containsAll(control.getDefaultValue().getTriangles()));

        control.setValue(value);
        assertEquals(value, control.getValue());

        assertTrue(control.getListView().getItems().containsAll(value.getTriangles()));
    }

    @Test
    public void testSphereControl() {
        Sphere value = new Sphere(randomPoint3D(), 1, null);
        SphereControl control = new SphereControl();

        assertPoint3DEquals(control.getDefaultValue().getCenter(), control.getCenterSpinner().getValue());
        assertDoubleEquals(control.getDefaultValue().getRadius(), control.getRadiusSpinner().getValue());

        control.setValue(value);
        assertEquals(value, control.getValue());

        assertPoint3DEquals(value.getCenter(), control.getCenterSpinner().getValue());
        assertDoubleEquals(value.getRadius(), control.getRadiusSpinner().getValue());
    }

    @Test
    public void testLightSourceControl() {
        LightSource value = new LightSource(randomPoint3D(), 0.5);
        LightSourceControl control = new LightSourceControl();

        assertPoint3DEquals(control.getDefaultValue().getCenter(), control.getCenterSpinner().getValue());
        assertDoubleEquals(control.getDefaultValue().getIntensity(), control.getIntensitySpinner().getValue());

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

        assertEquals(control.getDefaultValue().getColor(), control.getColorPicker().getValue());
        assertDoubleEquals(control.getDefaultValue().getDiffuse(), control.getDiffuseSpinner().getValue());
        assertDoubleEquals(control.getDefaultValue().getSpecular(), control.getSpecularSpinner().getValue());
        assertDoubleEquals(control.getDefaultValue().getReflectivity(), control.getReflectivitySpinner().getValue());
        assertDoubleEquals(control.getDefaultValue().getTransmittance(), control.getTransmittanceSpinner().getValue());
        assertDoubleEquals(control.getDefaultValue().getSpecularExp(), control.getSpecularExpSpinner().getValue());
        assertDoubleEquals(control.getDefaultValue().getIor(), control.getIorSpinner().getValue());

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