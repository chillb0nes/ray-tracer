package com.example.renderer.view.component;

import com.example.renderer.view.control.CloseButton;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;

import static com.example.renderer.view.util.ObservableUtils.addListener;
import static javafx.geometry.Pos.CENTER;

public class ExpandableListView<T> extends ListView<T> {
    private static final double DEFAULT_WIDTH = 200;
    private double arrowButtonWidth;
    private Bounds cellSize;
    private DoubleBinding cellWidthProperty;
    private Supplier<ValueNode<T>> nodeFactory;
    private Set<Integer> expandedItems;

    public ExpandableListView() {
        this(SimpleValueNode::new);
        setPrefWidth(DEFAULT_WIDTH);
    }

    public ExpandableListView(Supplier<ValueNode<T>> nodeFactory) {
        this.nodeFactory = nodeFactory;
        this.expandedItems = Sets.newHashSet();

        setItems(FXCollections.observableArrayList());
        setCellFactory(ExpandableListCell::new);

        calculateCellSize();

        BooleanBinding scrollBarVisible = Bindings.size(getItems())
                .multiply(cellSize.getHeight())
                .greaterThan(heightProperty());

        cellWidthProperty = Bindings.when(scrollBarVisible)
                .then(cellSize.getWidth() - getScrollBarWidth())
                .otherwise(cellSize.getWidth());

        setPrefWidth(cellSize.getWidth());
        paddingProperty().setValue(Insets.EMPTY);
    }

    public ExpandableListView(Collection<? extends T> items, Supplier<ValueNode<T>> nodeFactory) {
        this(nodeFactory);
        getItems().addAll(items);
    }

    public ExpandableListView(Collection<? extends T> items) {
        this(items, SimpleValueNode::new);
        setPrefWidth(DEFAULT_WIDTH);
    }

    private TitledPane createTitledPane(EventHandler<ActionEvent> deleteAction) {
        TitledPane titledPane = new TitledPane();
        CloseButton deleteButton = new CloseButton(deleteAction);

        BorderPane header = new BorderPane();
        header.setRight(deleteButton);
        header.prefWidthProperty().bind(titledPane.widthProperty().subtract(arrowButtonWidth));
        BorderPane.setAlignment(deleteButton, CENTER);

        titledPane.setGraphic(header);

        return titledPane;
    }

    private void calculateCellSize() {
        //TODO binding?
        ValueNode<T> node = nodeFactory.get();
        Preconditions.checkArgument(node instanceof Node);
        Node cellContent = (Node) node;

        TitledPane titledPane = createTitledPane(null);
        titledPane.setContent(cellContent);

        Pane root = new Pane(titledPane);
        new Scene(root);
        root.applyCss();
        root.layout();

        cellSize = titledPane.getLayoutBounds();

        Region arrowButton = (Region) titledPane.lookup(".arrow-button");
        arrowButtonWidth = arrowButton.getWidth()
                + arrowButton.getLayoutX()
                + arrowButton.getPadding().getRight()
                + 2;
    }

    private double getScrollBarWidth() {
        ScrollBar scrollBar = new ScrollBar();
        scrollBar.setOrientation(Orientation.VERTICAL);
        Pane root = new Pane(scrollBar);
        new Scene(root);
        root.applyCss();
        root.layout();
        return scrollBar.getWidth();
    }

    private static class SimpleValueNode extends Label implements ValueNode {
        @Override
        public ReadOnlyObjectProperty<Object> valueProperty() {
            return new ReadOnlyObjectWrapper<>(getUserData());
        }

        @Override
        public Object getValue() {
            return getUserData();
        }

        @Override
        public void setValue(Object value) {
            setUserData(value);
            setText(value.toString());
        }
    }

    private class ExpandableListCell extends ListCell<T> {
        private TitledPane titledPane;

        ExpandableListCell(ListView<T> listView) {
            titledPane = createTitledPane(event -> listView.getItems().remove(getItem()));
            titledPane.prefWidthProperty().bind(cellWidthProperty);
            titledPane.setAnimated(false);

            addListener(titledPane.expandedProperty(), expanded -> {
                if (expanded) {
                    expandedItems.add(getIndex());
                } else {
                    expandedItems.remove(getIndex());
                }
            });

            paddingProperty().setValue(Insets.EMPTY);
        }

        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                String title = String.format("%s %d", item.getClass().getSimpleName(), getIndex());
                updateTitle(title);
                updateContent(item);
                setGraphic(titledPane);
                titledPane.setExpanded(isExpanded());
            }
        }

        private void updateTitle(String title) {
            BorderPane header = (BorderPane) titledPane.getGraphic();
            header.setLeft(new Label(title));
        }

        private void updateContent(T item) {
            ValueNode<T> node = nodeFactory.get();
            node.setValue(item);
            Preconditions.checkArgument(node instanceof Node);
            titledPane.setContent((Node) node);
        }

        private boolean isExpanded() {
            return expandedItems.contains(getIndex());
        }
    }
}
