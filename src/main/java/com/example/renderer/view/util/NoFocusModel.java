package com.example.renderer.view.util;

import javafx.scene.control.FocusModel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NoFocusModel<T> extends FocusModel<T> {

    public static <T> NoFocusModel<T> getInstance() {
        return new NoFocusModel<>();
    }

    @Override
    protected int getItemCount() {
        return 0;
    }

    @Override
    protected T getModelItem(int i) {
        return null;
    }
}
