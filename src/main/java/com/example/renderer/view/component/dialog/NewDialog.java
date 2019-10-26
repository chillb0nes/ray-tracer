package com.example.renderer.view.component.dialog;

import com.example.renderer.model.object.Object3D;
import com.example.renderer.model.object.Renderable;
import javafx.scene.control.ButtonType;
import javafx.util.Callback;

public class NewDialog<T extends Object3D> extends CustomDialog<T> {

    public NewDialog(Class<T> clazz) {
        super(clazz);
        setTitle(String.format("New %s", getPrettyClassName()));

        item = objectControl.getValue();
        if (isRenderable()) {
            ((Renderable) item).setMaterial(materialControl.getValue());
        }
    }


    @Override
    protected Callback<ButtonType, T> getCustomResultConverter() {
        return buttonType -> ButtonType.OK == buttonType ? item : null;
    }
}
