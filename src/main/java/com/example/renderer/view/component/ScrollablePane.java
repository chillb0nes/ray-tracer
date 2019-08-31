package com.example.renderer.view.component;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ScrollablePane extends StackPane {
    private static final int DELAY_MS = 500;
    private final ObjectProperty<EventHandler<? super ScrollEvent>> onScrollEnded;
    private int scrollCounter;

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
                    log.warn("ScrollablePane helper thread interrupted", e);
                    Thread.currentThread().interrupt();
                }
            });
            thread.setDaemon(true);
            thread.start();
        });
    }

    public EventHandler<? super ScrollEvent> getOnScrollEnded() {
        return onScrollEnded.get();
    }

    public void setOnScrollEnded(EventHandler<? super ScrollEvent> handler) {
        onScrollEnded.setValue(handler);
    }

    public ObjectProperty<EventHandler<? super ScrollEvent>> onScrollEndedProperty() {
        return onScrollEnded;
    }
}