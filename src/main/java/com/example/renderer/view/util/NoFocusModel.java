package com.example.renderer.view.util;

import javafx.scene.control.FocusModel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "getInstance")
public class NoFocusModel<T> extends FocusModel<T> {

    @Override
    protected int getItemCount() {
        return 0;
    }

    @Override
    protected T getModelItem(int i) {
        return null;
    }
}
