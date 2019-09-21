package com.example.renderer.view.control;

import com.example.renderer.model.object.Sphere;
import com.example.renderer.view.component.ValueNode;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Point3D;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.Getter;

import static com.example.renderer.view.util.ObservableUtils.addListener;

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
        addListener(value, newSphere -> {
            centerSpinner.setValue(newSphere.getCenter());
            radiusSpinner.setValue(newSphere.getRadius());
        });

        addListener(centerSpinner.valueProperty(),
                newPoint -> value.get().setCenter(newPoint));

        addListener(radiusSpinner.valueProperty(),
                newRadius -> value.get().setRadius(newRadius));

        setDefaultValue();
    }

    @Override
    public Sphere getDefaultValue() {
        return new Sphere(Point3D.ZERO, 0);
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
