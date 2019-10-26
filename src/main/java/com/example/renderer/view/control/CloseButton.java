package com.example.renderer.view.control;

import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import lombok.Getter;

@Getter
public class CloseButton extends StackPane {
    private Button button;
    private ObjectProperty<EventHandler<ActionEvent>> onAction;

    public CloseButton() {
        button = new Button();
        onAction = button.onActionProperty();
        getChildren().add(button);
        getStyleClass().add("close-btn");
    }

    public CloseButton(EventHandler<ActionEvent> value) {
        this();
        setOnAction(value);
    }

    public EventHandler<ActionEvent> getOnAction() {
        return onAction.get();
    }

    public void setOnAction(EventHandler<ActionEvent> value) {
        onAction.set(value);
    }

    public ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
        return onAction;
    }
}
