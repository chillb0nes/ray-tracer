package com.example.renderer.view.control;

import com.example.renderer.model.object.Sphere;
import com.example.renderer.view.component.ValueNode;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.Getter;

@Getter
public class SphereControl extends VBox implements ValueNode<Sphere> {
    private Point3DSpinner centerSpinner;
    private DoubleSpinner radiusSpinner;
    private ObjectProperty<Sphere> value;

    public SphereControl() {
        centerSpinner = new Point3DSpinner();
        radiusSpinner = new DoubleSpinner(0, Double.MAX_VALUE);
        radiusSpinner.prefWidthProperty().bind(centerSpinner.widthProperty());

        getChildren().addAll(
                new Label("Center"),
                centerSpinner,
                new Label("Radius"),
                radiusSpinner
        );
        setSpacing(2);

        value = new ReadOnlyObjectWrapper<>();
        value.addListener(((observable, oldValue, newValue) -> {
            centerSpinner.setValue(newValue.getCenter());
            radiusSpinner.setValue(newValue.getRadius());
        }));

        centerSpinner.valueProperty().addListener(
                (observable, oldPoint, newPoint) -> value.get().setCenter(newPoint));

        radiusSpinner.valueProperty().addListener(
                (observable, oldRadius, newRadius) -> value.get().setRadius(newRadius));
    }

    @Override
    public ObjectProperty<Sphere> valueProperty() {
        return value;
    }

    @Override
    public Sphere getValue() {
        return value.get();
    }

    @Override
    public void setValue(Sphere value) {
        valueProperty().set(value);
    }
}
