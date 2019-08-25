package com.example.renderer.view.component;

import com.example.renderer.service.DefaultControlFactory;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

@Deprecated
public class ExpandableListView1<T> extends ScrollPane {
    @Getter
    @Setter
    private ObservableList<T> items;
    private VBox container;

    public ExpandableListView1() {
        items = FXCollections.observableArrayList();
        container = new VBox();
        container.setFocusTraversable(true); //todo?
        setContent(container);

        items.addListener(new ExpandableListChangeListener<>());

        setPrefHeight(400);

        setFitToWidth(true);
        setFitToHeight(true);

        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
    }

    public ExpandableListView1(List<T> items) {
        this();
        this.items.addAll(items);

        Class<?> clazz = items.get(0).getClass();
        Node[] panes = IntStream.rangeClosed(0, items.size() - 1)
                .mapToObj(i -> createTitledPane(clazz, i))
                .toArray(Node[]::new);

        container.getChildren().addAll(panes);
    }

    public void setValue(Collection<T> value) {
        items = FXCollections.observableArrayList(value);
    }

    private TitledPane createTitledPane(Class<?> clazz, int i) {
        TitledPane titledPane = new TitledPane();
        String title = String.format("%s %d", clazz.getSimpleName(), i + 1);
        Node content = new DefaultControlFactory().getByClass(clazz);
        if (content instanceof ValueNode) {
            ((ValueNode<T>) content).setValue(items.get(i));
        }
        titledPane.setContent(content);

        StackPane deleteButton = new StackPane();
        deleteButton.getStyleClass().add("close-btn");
        Button button = new Button();
        button.setOnAction(event -> items.remove(i));
        deleteButton.getChildren().add(button);

        Label label = new Label(title);

        BorderPane header = new BorderPane();
        header.setStyle("-fx-background-color:red");
        header.setLeft(label);
        header.setCenter(deleteButton);
        //header.prefWidthProperty().bind(content.widthProperty());

        ((Region) content).widthProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("\n" + newValue);
        });

        deleteButton.setStyle("-fx-background-color:blue");

        //header.setPrefWidth(100);

        titledPane.setGraphic(header);
        titledPane.setText("123");

        return titledPane;
    }

    private class ExpandableListChangeListener<E> implements ListChangeListener<E> {
        @Override
        public void onChanged(ListChangeListener.Change<? extends E> change) {
            while (change.next()) {
                if (change.wasAdded()) {
                    //TODO
                    TitledPane titledPane = createTitledPane(Point3D.class, change.getFrom());
                    container.getChildren().add(titledPane);
                    //titledPane.requestFocus();
                }
                if (change.wasRemoved()) {
                    container.getChildren().remove(change.getFrom());
                }
            }
        }
    }

}

