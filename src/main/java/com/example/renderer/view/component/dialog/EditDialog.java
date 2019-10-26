package com.example.renderer.view.component.dialog;

import com.example.renderer.model.object.Object3D;
import com.example.renderer.model.object.Renderable;
import javafx.scene.control.ButtonType;
import javafx.util.Callback;

import java.util.function.UnaryOperator;

public class EditDialog<T extends Object3D> extends CustomDialog<T> {

    private T copy;

    public EditDialog(T item, UnaryOperator<T> copier) {
        super(item);
        setTitle(String.format("Edit %s", getPrettyClassName()));

        copy = copier.apply(item);
        objectControl.setValue(copy);
        if (isRenderable() && ((Renderable) copy).getMaterial() != null) {
            materialControl.setValue(((Renderable) copy).getMaterial());
        }
    }

    @Override
    protected Callback<ButtonType, T> getCustomResultConverter() {
        return buttonType -> {
            if (ButtonType.OK == buttonType) {
                item = copy;
            }
            return item;
        };
    }
}
