package com.example.renderer.view.control;

import com.example.renderer.model.object.Triangle;
import com.example.renderer.view.component.ValueNode;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.Getter;

import static com.example.renderer.view.util.ObservableUtils.addListener;

@Getter
public class TriangleControl extends VBox implements ValueNode<Triangle> {
    private Point3DSpinner v0Spinner;
    private Point3DSpinner v1Spinner;
    private Point3DSpinner v2Spinner;
    private ReadOnlyObjectWrapper<Triangle> value;

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
        addListener(value, newTriangle -> {
            v0Spinner.valueProperty().bindBidirectional(newTriangle.v0Property());
            v1Spinner.valueProperty().bindBidirectional(newTriangle.v1Property());
            v2Spinner.valueProperty().bindBidirectional(newTriangle.v2Property());
        });
    }

    @Override
    public ReadOnlyObjectProperty<Triangle> valueProperty() {
        return value.getReadOnlyProperty();
    }

    @Override
    public Triangle getValue() {
        return value.get();
    }

    @Override
    public void setValue(Triangle value) {
        this.value.set(value);
    }

}
