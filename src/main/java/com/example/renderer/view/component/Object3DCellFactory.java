package com.example.renderer.view.component;

import com.example.renderer.model.object.Object3D;
import com.example.renderer.view.util.Icon;
import javafx.geometry.Insets;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

public class Object3DCellFactory implements Callback<ListView, ListCell> {

    @Override
    public ListCell<Object3D> call(ListView object3DListView) {
        return new ListCell<Object3D>() {
            @Override
            protected void updateItem(Object3D item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setPadding(new Insets(1, 0, 1, 4));
                    setGraphic(Icon.forClass(item.getClass()).withSize(20));
                    setText(item.getClass().getSimpleName());
                }
            }
        };
    }
}
