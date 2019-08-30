package com.example.renderer.view.control;

import com.example.renderer.model.object.Mesh;
import com.example.renderer.model.object.Triangle;
import com.example.renderer.view.component.ExpandableListView;
import com.example.renderer.view.component.ValueNode;
import com.example.renderer.view.util.Icon;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import lombok.Getter;

import java.util.function.Supplier;

@Getter
public class MeshControl extends VBox implements ValueNode<Mesh> {
    private ExpandableListView<Triangle> listView;
    private Button addButton;
    private ObjectProperty<Mesh> value;

    //TODO simplify
    public MeshControl() {
        Mesh mesh = new Mesh();
        value = new SimpleObjectProperty<>(mesh);
        listView = new ExpandableListView<>(mesh.getTriangles());
        mesh.setTriangles(listView.getItems());

        addButton = new Button("Add");
        addButton.setOnAction(event -> listView.getItems().add(Triangle.EMPTY));
        addButton.setMaxWidth(Double.MAX_VALUE);

        double fontSize = addButton.getFont().getSize();
        addButton.setGraphic(Icon.ADD.withSize(fontSize));

        getChildren().addAll(listView, addButton);
    }

    public MeshControl(Supplier<ValueNode<Triangle>> nodeFactory) {
        Mesh mesh = new Mesh();
        value = new SimpleObjectProperty<>(mesh);
        listView = new ExpandableListView<>(mesh.getTriangles(), nodeFactory);
        mesh.setTriangles(listView.getItems());

        addButton = new Button("Add");
        addButton.setOnAction(event -> listView.getItems().add(Triangle.EMPTY));
        addButton.setMaxWidth(Double.MAX_VALUE);

        double fontSize = addButton.getFont().getSize();
        addButton.setGraphic(Icon.ADD.withSize(fontSize));

        getChildren().addAll(listView, addButton);
    }

    @Override
    public ObjectProperty<Mesh> valueProperty() {
        return value;
    }

    @Override
    public void setValue(Mesh value) {
        valueProperty().set(value);
    }

    @Override
    public Mesh getValue() {
        return value.get();
    }
}
