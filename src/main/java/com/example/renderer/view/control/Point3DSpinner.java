package com.example.renderer.view.control;

import com.example.renderer.view.component.ValueNode;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Point3D;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import lombok.Getter;

@Getter
public class Point3DSpinner extends HBox implements ValueNode<Point3D> {
    private Spinner<Double> xSpinner;
    private Spinner<Double> ySpinner;
    private Spinner<Double> zSpinner;
    private ObjectProperty<Point3D> value;

    public Point3DSpinner() {
        xSpinner = new DoubleSpinner();
        ySpinner = new DoubleSpinner();
        zSpinner = new DoubleSpinner();

        getChildren().addAll(
                labeled(xSpinner, "X"),
                labeled(ySpinner, "Y"),
                labeled(zSpinner, "Z")
        );
        //setSpacing(-1);

        value = new ReadOnlyObjectWrapper<>(Point3D.ZERO);
        value.addListener(((observable, oldValue, newValue) -> {
            xSpinner.getValueFactory().setValue(newValue.getX());
            ySpinner.getValueFactory().setValue(newValue.getY());
            zSpinner.getValueFactory().setValue(newValue.getZ());
        }));

        xSpinner.valueProperty().addListener((observable, oldX, newX) -> {
            Point3D oldPoint = value.get();
            Point3D newPoint = new Point3D(newX, oldPoint.getY(), oldPoint.getZ());
            value.set(newPoint);
        });

        ySpinner.valueProperty().addListener((observable, oldY, newY) -> {
            Point3D oldPoint = value.get();
            Point3D newPoint = new Point3D(oldPoint.getX(), newY, oldPoint.getZ());
            value.set(newPoint);
        });

        zSpinner.valueProperty().addListener((observable, oldZ, newZ) -> {
            Point3D oldPoint = value.get();
            Point3D newPoint = new Point3D(oldPoint.getX(), oldPoint.getY(), newZ);
            value.set(newPoint);
        });
    }

    @Override
    public ObjectProperty<Point3D> valueProperty() {
        return value;
    }

    @Override
    public Point3D getValue() {
        return value.get();
    }

    @Override
    public void setValue(Point3D value) {
        this.value.set(value);
    }

    private StackPane labeled(Spinner spinner, String text) {
        spinner.setPrefWidth(60);
        Label promptText = new Label(text);
        promptText.setDisable(true);
        promptText.prefWidthProperty().bind(spinner.widthProperty().subtract(10));
        return new StackPane(spinner, promptText);
    }
}
