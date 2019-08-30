package com.example.renderer.view.control;

import com.example.renderer.view.component.ValueNode;
import javafx.beans.NamedArg;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.util.StringConverter;

import java.util.Locale;

import static javafx.geometry.Pos.BASELINE_RIGHT;

public class DoubleSpinner extends Spinner<Double> implements ValueNode<Double> {

    private static final String DECIMAL_REGEX = "-?\\d+(\\.\\d+)?";

    public DoubleSpinner(@NamedArg("min") double min,
                         @NamedArg("max") double max) {
        setEditable(true);
        getEditor().setAlignment(BASELINE_RIGHT);
        setValueFactory(createValueFactory(min, max));
    }

    private SpinnerValueFactory<Double> createValueFactory(double min, double max) {
        SpinnerValueFactory<Double> spinnerValueFactory;
        spinnerValueFactory = new DoubleSpinnerValueFactory(min, max, 0, 0.01);
        spinnerValueFactory.setConverter(STRING_CONVERTER);
        return spinnerValueFactory;
    }

    @Override
    public void setValue(Double value) {
        getValueFactory().setValue(value);
    }

    public DoubleSpinner() {
        this(-Double.MAX_VALUE, Double.MAX_VALUE);
    }

    private static final StringConverter<Double> STRING_CONVERTER = new StringConverter<Double>() {
        @Override
        public String toString(Double number) {
            if (number == null) {
                return "";
            }
            return String.format(Locale.US, "%.2f", number);
        }

        @Override
        public Double fromString(String number) {
            if (number == null) {
                return null;
            }
            return number.matches(DECIMAL_REGEX) ? Double.parseDouble(number) : 0;
        }
    };
}
