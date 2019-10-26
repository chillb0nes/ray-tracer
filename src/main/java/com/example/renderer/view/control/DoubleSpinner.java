package com.example.renderer.view.control;

import com.example.renderer.view.component.ValueNode;
import com.example.renderer.view.util.NumberStringConverter;
import javafx.beans.NamedArg;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.util.StringConverter;

import java.util.function.Function;

import static javafx.geometry.Pos.BASELINE_RIGHT;

public class DoubleSpinner extends Spinner<Double> implements ValueNode<Double> {

    public DoubleSpinner() {
        this(-Double.MAX_VALUE, Double.MAX_VALUE);
    }

    public DoubleSpinner(@NamedArg("min") double min,
                         @NamedArg("max") double max) {
        setEditable(true);
        getEditor().setAlignment(BASELINE_RIGHT);
        setValueFactory(createValueFactory(min, max));
    }

    private SpinnerValueFactory<Double> createValueFactory(double min, double max) {
        SpinnerValueFactory<Double> spinnerValueFactory;
        spinnerValueFactory = new DoubleSpinnerValueFactory(min, max, 0, 0.01);
        spinnerValueFactory.setConverter(new NumberStringConverter<>(Double::valueOf));
        return spinnerValueFactory;
    }

    @Override
    public void setValue(Double value) {
        getValueFactory().setValue(value);
    }

    @Override
    public Double getDefaultValue() {
        return 0d;
    }
}
