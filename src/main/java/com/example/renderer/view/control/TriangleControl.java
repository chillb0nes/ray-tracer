package com.example.renderer.view.control;

import com.example.renderer.model.object.Triangle;
import com.example.renderer.view.component.ValueNode;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.Getter;

@Getter
public class TriangleControl extends VBox implements ValueNode<Triangle> {
    private Point3DSpinner v0Spinner;
    private Point3DSpinner v1Spinner;
    private Point3DSpinner v2Spinner;
    private ObjectProperty<Triangle> value;

    public TriangleControl() {
        this("Vertex A", "Vertex B", "Vertex C");
    }

    public TriangleControl(String v0Name, String v1Name, String v2Name) {
        v0Spinner = new Point3DSpinner();
        v1Spinner = new Point3DSpinner();
        v2Spinner = new Point3DSpinner();

        getChildren().addAll(
                new Label(v0Name), v0Spinner,
                new Label(v1Name), v1Spinner,
                new Label(v2Name), v2Spinner
        );
        setSpacing(2);

        value = new ReadOnlyObjectWrapper<>();
        value.addListener(((observable, oldValue, newValue) -> {
            v0Spinner.setValue(newValue.getV0());
            v1Spinner.setValue(newValue.getV1());
            v2Spinner.setValue(newValue.getV2());
        }));

        v0Spinner.valueProperty().addListener(
                (observable, oldPoint, newPoint) -> value.get().setV0(newPoint));

        v1Spinner.valueProperty().addListener(
                (observable, oldPoint, newPoint) -> value.get().setV1(newPoint));

        v2Spinner.valueProperty().addListener(
                (observable, oldPoint, newPoint) -> value.get().setV2(newPoint));
    }

    @Override
    public ObjectProperty<Triangle> valueProperty() {
        return value;
    }

    @Override
    public void setValue(Triangle value) {
        valueProperty().set(value);
    }

    @Override
    public Triangle getValue() {
        return value.get();
    }

}
