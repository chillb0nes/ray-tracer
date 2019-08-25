package com.example.renderer.view.component;

import com.example.renderer.service.DefaultControlFactory;
import com.example.renderer.view.control.CloseButton;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.When;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import lombok.extern.java.Log;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.logging.Level;

import static javafx.geometry.Pos.CENTER;

@Log
public class ExpandableListView<T> extends ListView<T> {
    private Class<T> clazz;
    private Bounds cellSize;
    private DoubleBinding cellWidthProperty;

    private ExpandableListView() {
        // TODO
    }

    public ExpandableListView(Class<T> clazz) {
        // TODO
    }

    public ExpandableListView(Collection<? extends T> items, Class<T> clazz) {//TODO without clazz, with check
        this.clazz = clazz;
        //setSelectionModel(NoSelectionModel.getInstance());
        //setFocusModel(NoFocusModel.getInstance());

        setCellFactory(ExpandableListCell::new);

        calculateCellSize();

        setItems(FXCollections.observableArrayList(items));

        BooleanBinding scrollBarVisible = Bindings.size(getItems())
                .multiply(cellSize.getHeight())
                .greaterThan(heightProperty());

        cellWidthProperty = new When(scrollBarVisible)
                .then(cellSize.getWidth() - calculateScrollBarWidth())
                .otherwise(cellSize.getWidth());

        setPrefWidth(cellSize.getWidth());
        paddingProperty().setValue(Insets.EMPTY);
    }

    private void calculateCellSize() {
        Region cellContent = (Region) new DefaultControlFactory().getByClass(clazz);
        TitledPane titledPane = createTitledPane(null);
        titledPane.setContent(cellContent);
        Pane root = new Pane(titledPane);
        new Scene(root);
        root.applyCss();
        root.layout();
        cellSize = titledPane.getLayoutBounds();
    }

    private double calculateScrollBarWidth() {
        ScrollBar scrollBar = new ScrollBar();
        scrollBar.setOrientation(Orientation.VERTICAL);
        Pane root = new Pane(scrollBar);
        new Scene(root);
        root.applyCss();
        root.layout();
        return scrollBar.getWidth();
    }

    private static TitledPane createTitledPane(EventHandler<ActionEvent> deleteAction) {
        TitledPane titledPane = new TitledPane();
        CloseButton deleteButton = new CloseButton(deleteAction);

        BorderPane header = new BorderPane();
        header.setRight(deleteButton);
        header.setMinWidth(USE_PREF_SIZE);
        BorderPane.setAlignment(deleteButton, CENTER);

        header.needsLayoutProperty().addListener(((observable, oldValue, newValue) -> {
            Region arrowButton = (Region) titledPane.lookup(".arrow-button");
            header.prefWidthProperty().bind(titledPane.widthProperty()
                    .subtract(arrowButton.getWidth())
                    .subtract(arrowButton.getLayoutX())
                    .subtract(arrowButton.getPadding().getRight())
                    .subtract(2));
        }));

        titledPane.setGraphic(header);
        titledPane.setMinWidth(USE_PREF_SIZE);

        return titledPane;
    }

    private class ExpandableListCell<T> extends ListCell<T> {
        private TitledPane titledPane;

        public ExpandableListCell(ListView<T> listView) {
            titledPane = createTitledPane(event -> listView.getItems().remove(getItem()));
            titledPane.prefWidthProperty().bind(cellWidthProperty);
            paddingProperty().setValue(Insets.EMPTY);
        }

        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                String title = item.getClass().getSimpleName() + " " + getIndex();
                updateTitle(title);
                updateContent(item);
                setGraphic(titledPane);
            }
        }

        private void updateTitle(String title) {
            BorderPane header = (BorderPane) titledPane.getGraphic();
            header.setLeft(new Label(title));
        }

        private void updateContent(T item) {
            Region content = (Region) new DefaultControlFactory().getByClass(clazz);
            try {
                Method setValue = content.getClass().getDeclaredMethod("setValue", clazz);
                setValue.invoke(content, item);
            } catch (ReflectiveOperationException e) {
                log.log(Level.WARNING, "Couldn't update content of ExpandableListView", e);
            }
            titledPane.setContent(content);
        }
    }
}
