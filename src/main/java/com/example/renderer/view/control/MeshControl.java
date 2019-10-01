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

@Getter
public class MeshControl extends VBox implements ValueNode<Mesh> {
    private ExpandableListView<Triangle> listView;
    private Button addButton;
    private ObjectProperty<Mesh> value;

    public MeshControl() {
        listView = new ExpandableListView<>(TriangleControl::new);

        value = new SimpleObjectProperty<>(new Mesh());
        value.get().trianglesProperty().bindBidirectional(listView.itemsProperty());

        addButton = new Button("Add");
        addButton.setOnAction(event -> listView.getItems().add(new Triangle()));
        addButton.setMaxWidth(Double.MAX_VALUE);

        double fontSize = addButton.getFont().getSize();
        addButton.setGraphic(Icon.ADD.withSize(fontSize));

        getChildren().addAll(listView, addButton);

        setDefaultValue();
    }

    @Override
    public Mesh getDefaultValue() {
        return new Mesh();
    }

    @Override
    public ObjectProperty<Mesh> valueProperty() {
        return value;
    }

    @Override
    public Mesh getValue() {
        return value.get();
    }

    @Override
    public void setValue(Mesh value) {
        this.value.set(value);
        this.value.get().trianglesProperty().bindBidirectional(listView.itemsProperty());
    }
}
