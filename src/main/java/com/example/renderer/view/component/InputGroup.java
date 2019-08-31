package com.example.renderer.view.component;

import javafx.beans.NamedArg;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Getter;

import static javafx.geometry.Pos.CENTER;

public class InputGroup extends StackPane {
    private static final String DEFAULT_NAME = "Input group";
    private static final int PADDING = 10;

    private Label groupLabel;
    private VBox children;
    private DoubleProperty spacing;

    public InputGroup() {
        getStyleClass().add("input-group");
        setPadding(new Insets(PADDING * 1.5, PADDING, PADDING, PADDING));

        groupLabel = new Label(DEFAULT_NAME);
        groupLabel.getStyleClass().add("group-label");
        groupLabel.setPadding(new Insets(3));
        groupLabel.translateYProperty().bind(heightProperty().add(PADDING / 2).divide(-2));

        children = new VBox();
        children.setAlignment(CENTER);
        children.setSpacing(PADDING);

        spacing = new SimpleDoubleProperty();
        spacing.bindBidirectional(children.spacingProperty());

        getChildren().addAll(groupLabel, children);
    }

    public InputGroup(@NamedArg("title") String title) {
        this();
        groupLabel.setText(title);
    }

    public InputGroup(@NamedArg("children") Node... children) {
        this();
        getChildrenContainer().addAll(children);
    }

    public InputGroup(@NamedArg("title") String title,
                      @NamedArg("children") Node... children) {
        this(title);
        getChildrenContainer().addAll(children);
    }

    public Label getLabel() {
        return groupLabel;
    }

    public ObservableList<Node> getChildrenContainer() {
        return children.getChildren();
    }

    public final DoubleProperty spacingProperty() {
        return spacing;
    }

    public final double getSpacing() {
        return spacing.get();
    }

    public final void setSpacing(double value) {
        spacing.setValue(value);
    }
}
