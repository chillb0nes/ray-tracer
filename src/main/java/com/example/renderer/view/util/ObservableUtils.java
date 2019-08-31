package com.example.renderer.view.util;

import javafx.beans.value.ObservableValue;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Consumer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ObservableUtils {

    public static <T> void addListener(ObservableValue<T> observableValue, Consumer<T> listener) {
        observableValue.addListener(((observable, oldValue, newValue) -> listener.accept(newValue)));
    }

}
