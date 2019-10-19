package com.example.renderer.service.dialog;

import com.example.renderer.service.serialization.SerializationService;
import com.example.renderer.view.component.dialog.EditDialog;
import com.example.renderer.view.component.dialog.NewDialog;
import com.example.renderer.model.object.*;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Setter
@Component
public class DialogFactory {

    @Autowired
    private SerializationService serializationService;

    private Stage owner;

    public <T extends Object3D> Dialog<T> createNewDialog(Class<T> clazz) {
        Dialog<T> dialog = new NewDialog<>(clazz);
        dialog.initOwner(owner);
        return dialog;
    }

    public <T extends Object3D> Dialog<T> createEditDialog(T selected) {
        Dialog<T> dialog = new EditDialog<>(selected, serializationService::copy);
        dialog.initOwner(owner);
        return dialog;
    }
}
