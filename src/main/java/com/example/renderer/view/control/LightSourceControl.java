package com.example.renderer.view.control;

import com.example.renderer.model.light.LightSource;
import com.example.renderer.view.component.ValueNode;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.Getter;

import static com.example.renderer.view.util.ObservableUtils.addListener;

@Getter
public class LightSourceControl extends VBox implements ValueNode<LightSource> {
    private Point3DSpinner centerSpinner;
    private DoubleSpinner intensitySpinner;
    private ObjectProperty<LightSource> value;

    public LightSourceControl() {
        centerSpinner = new Point3DSpinner();
        intensitySpinner = new DoubleSpinner(0, Double.MAX_VALUE);
        intensitySpinner.prefWidthProperty().bind(centerSpinner.widthProperty());

        getChildren().addAll(
                new Label("Center"),
                centerSpinner,
                new Label("Intensity"),
                intensitySpinner
        );
        setSpacing(2);

        value = new ReadOnlyObjectWrapper<>();
        addListener(value, newLight -> {
            centerSpinner.setValue(newLight.getCenter());
            intensitySpinner.setValue(newLight.getIntensity());
        });

        addListener(centerSpinner.valueProperty(),
                newPoint -> value.get().setCenter(newPoint));

        addListener(intensitySpinner.valueProperty(),
                newValue -> value.get().setIntensity(newValue));
    }

    @Override
    public ObjectProperty<LightSource> valueProperty() {
        return value;
    }

    @Override
    public LightSource getValue() {
        return value.get();
    }

    @Override
    public void setValue(LightSource value) {
        valueProperty().set(value);
    }
}
