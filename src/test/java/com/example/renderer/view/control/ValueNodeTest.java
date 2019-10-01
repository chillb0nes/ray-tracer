package com.example.renderer.view.control;

import com.example.renderer.BaseJavaFXTest;
import com.example.renderer.model.Material;
import com.example.renderer.model.light.LightSource;
import com.example.renderer.model.object.Mesh;
import com.example.renderer.model.object.Sphere;
import com.example.renderer.model.object.Triangle;
import com.example.renderer.view.component.ValueNode;
import com.google.common.collect.ImmutableMap;
import javafx.geometry.Point3D;
import org.junit.Test;

import java.util.Map;

public class ValueNodeTest extends BaseJavaFXTest {

    private static <T> void testValueNode(ValueNode<T> valueNode,
                                          T value,
                                          Map<ValueNode, Object> parameters) {
        // test initial value
        assertEquals(valueNode.getDefaultValue(), valueNode.getValue());

        // test set value
        valueNode.setValue(value);
        assertEquals(value, valueNode.getValue());
        parameters.forEach((k, v) -> assertEquals(v, k.getValue()));

        // test set value through nested controls
        valueNode.setDefaultValue();
        parameters.forEach(ValueNode::setValue);
        assertEquals(value, valueNode.getValue());
    }

    @Test
    public void testDoubleSpinner() {
        double value = random.nextDouble();
        DoubleSpinner control = new DoubleSpinner();
        testValueNode(control, value, ImmutableMap.of(control, value));
    }

    @Test
    public void testPoint3DSpinner() {
        Point3D point3D = randomPoint3D();
        Point3DSpinner control = new Point3DSpinner();
        testValueNode(control, point3D, ImmutableMap.of(
                control.getXSpinner(), point3D.getX(),
                control.getYSpinner(), point3D.getY(),
                control.getZSpinner(), point3D.getZ()
        ));
    }

    @Test
    public void testTriangleControl() {
        Triangle triangle = randomTriangle();
        TriangleControl control = new TriangleControl();
        testValueNode(control, triangle, ImmutableMap.of(
                control.getV0Spinner(), triangle.getV0(),
                control.getV1Spinner(), triangle.getV1(),
                control.getV2Spinner(), triangle.getV2()
        ));
    }

    @Test
    public void testSphereControl() {
        Sphere sphere = new Sphere(randomPoint3D(), random.nextDouble());
        SphereControl control = new SphereControl();
        testValueNode(control, sphere, ImmutableMap.of(
                control.getCenterSpinner(), sphere.getCenter(),
                control.getRadiusSpinner(), sphere.getRadius()
        ));
    }

    @Test
    public void testLightSourceControl() {
        LightSource lightSource = new LightSource(randomPoint3D(), random.nextDouble());
        LightSourceControl control = new LightSourceControl();
        testValueNode(control, lightSource, ImmutableMap.of(
                control.getCenterSpinner(), lightSource.getCenter(),
                control.getIntensitySpinner(), lightSource.getIntensity()
        ));
    }

    @Test
    public void testMaterialControl() {
        Material material = Material.random();
        MaterialControl control = new MaterialControl();

        // test color picker separately
        assertEquals(control.getDefaultValue().getColor(), control.getColorPicker().getValue());

        control.setValue(material);
        assertEquals(material.getColor(), control.getColorPicker().getValue());

        control.setDefaultValue();
        control.getColorPicker().setValue(material.getColor());
        assertEquals(material.getColor(), control.getValue().getColor());

        control.setDefaultValue();

        // general test
        testValueNode(control, material,ImmutableMap.<ValueNode, Object>builder()
                .put(control.getDiffuseSpinner(), material.getDiffuse())
                .put(control.getSpecularSpinner(), material.getSpecular())
                .put(control.getReflectivitySpinner(), material.getReflectivity())
                .put(control.getTransmittanceSpinner(), material.getTransmittance())
                .put(control.getSpecularExpSpinner(), material.getSpecularExp())
                .put(control.getIorSpinner(), material.getIor())
                .build()
        );
    }

    @Test
    public void testMeshControl() {
        Mesh mesh = randomMesh();
        MeshControl control = new MeshControl();

        assertEquals(control.getDefaultValue().getTriangles(), control.getListView().getItems());

        control.setValue(mesh);
        assertEquals(mesh.getTriangles(), control.getListView().getItems());

        control.setDefaultValue();
        control.getListView().setItems(mesh.getTriangles());
        assertEquals(mesh.getTriangles(), control.getValue().getTriangles());

        control.setDefaultValue();
    }
}