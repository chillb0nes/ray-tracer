package com.example.renderer.view.util;

import javafx.util.StringConverter;

import java.util.Locale;
import java.util.function.Function;

public class NumberStringConverter<T extends Number> extends StringConverter<T> {
    private static final String DECIMAL_REGEX = "-?\\d+(\\.\\d+)?";
    private Function<String, T> fromStringFunction;

    public NumberStringConverter(Function<String, T> fromStringFunction) {
        this.fromStringFunction = fromStringFunction;
    }

    @Override
    public String toString(T number) {
        if (number == null) {
            return "";
        }
        return String.format(Locale.US, "%.2f", number.doubleValue());
    }

    @Override
    public T fromString(String number) {
        return number != null && number.trim().matches(DECIMAL_REGEX)
                ? fromStringFunction.apply(number)
                : fromStringFunction.apply("0");
    }
}
