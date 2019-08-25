package com.example.renderer.view.component;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import lombok.extern.java.Log;

import java.util.logging.Level;

@Log
public class ScrollablePane extends StackPane {
    private static final int DELAY_MS = 500;
    private int scrollCounter;

    private final ObjectProperty<EventHandler<? super ScrollEvent>> onScrollEnded;

    public ObjectProperty<EventHandler<? super ScrollEvent>> onScrollEndedProperty() {
        return onScrollEnded;
    }

    public void setOnScrollEnded(EventHandler<? super ScrollEvent> handler) {
        onScrollEnded.setValue(handler);
    }

    public ScrollablePane() {
        scrollCounter = 0;
        onScrollEnded = new SimpleObjectProperty<>(scrollEvent -> {});
        setOnScroll(scrollEvent -> {
            scrollCounter++;

            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(DELAY_MS);
                    if (scrollCounter == 1) {
                        onScrollEnded.get().handle(scrollEvent);
                    }
                    scrollCounter--;
                } catch (InterruptedException e) {
                    log.log(Level.WARNING, "ScrollablePane helper thread interrupted", e);
                    Thread.currentThread().interrupt();
                }
            });
            thread.setDaemon(true);
            thread.start();
        });
    }
}