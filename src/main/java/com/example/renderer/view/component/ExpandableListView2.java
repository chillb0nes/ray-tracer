package com.example.renderer.view.component;

import com.example.renderer.service.DefaultControlFactory;
import com.example.renderer.view.control.CloseButton;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Callback;

import java.util.Collection;
import java.util.List;

import static javafx.geometry.Pos.CENTER;

@Deprecated
//TODO cache everything, synchronize width changes
public class ExpandableListView2<T> extends ListView<T> {
    private double cellHeight;
    private boolean resized;
    private Node cachedListCell;

    private ExpandableListView2() {
        // TODO
    }

    public ExpandableListView2(Class<T> clazz) {
        // TODO
    }

    public ExpandableListView2(List<? extends T> items) {
        /*setSelectionModel(NoSelectionModel.getInstance());
        setFocusModel(NoFocusModel.getInstance());*/

        setItems(FXCollections.observableArrayList(items));

        getItems().addListener((ListChangeListener<? super T>) System.err::println);

        Class<?> clazz = items.get(0).getClass();

        Bounds bounds = calculateSize(createTitledPane(clazz, 0));
        setPrefWidth(bounds.getWidth());
        cellHeight = bounds.getHeight();
        paddingProperty().setValue(Insets.EMPTY);
        setCellFactory(new Callback<ListView<T>, ListCell<T>>() {
            @Override
            public ListCell<T> call(ListView<T> listView) {
                return new ListCell<T>() {
                    {
                        paddingProperty().setValue(Insets.EMPTY);
                    }

                    @Override
                    protected void updateItem(T item, boolean isEmpty) {
                        super.updateItem(item, isEmpty);
                        if (isEmpty || item == null) {
                            setGraphic(null);
                        } else {
                            TitledPane titledPane = createTitledPane(clazz, getIndex());
                            setGraphic(titledPane);
                            if (/*!resized &&*/ ((getItems().size() - 1) * cellHeight) >= listView.getHeight()) {
                                //listView.setPrefWidth(listView.getPrefWidth() + 12);
                                ScrollBar scrollBar = (ScrollBar) ExpandableListView2.this.lookup(".scroll-bar");
                                double width = scrollBar.getWidth();
                                titledPane.setMaxWidth(listView.getPrefWidth() - 12);
                                ((Region) titledPane.getContent()).setMaxWidth(listView.getPrefWidth() - 12);
                                resized = true;
                            }
                        }
                    }
                };
            }
        });
    }

    public void setValue(Collection<T> value) {
        setItems(FXCollections.observableArrayList(value));
    }

    private TitledPane createTitledPane(Class<?> clazz, int i) {
        TitledPane titledPane = new TitledPane();
        //setFocusModel(NoFocusModel.getInstance());
        String title = String.format("%s %d", clazz.getSimpleName(), i + 1);
        Region content = (Region) new DefaultControlFactory().getByClass(clazz);
        if (content instanceof ValueNode) {
            ((ValueNode) content).setValue(getItems().get(i));
        }
        titledPane.setContent(content);
        titledPane.setMaxWidth(Control.USE_PREF_SIZE);

        CloseButton deleteButton = new CloseButton(event -> getItems().remove(i));

        BorderPane header = new BorderPane();
        header.setLeft(new Label(title));
        header.setRight(deleteButton);

        Pane pane = new Pane(titledPane);
        new Scene(pane);
        pane.applyCss();
        pane.layout();
        Region lookup = (Region) titledPane.lookup(".arrow-button");
        double arrowButtonWidth = lookup.getLayoutX() + lookup.getWidth() + lookup.getPadding().getRight();

        header.prefWidthProperty().bind(content.widthProperty().subtract(arrowButtonWidth));
        BorderPane.setAlignment(deleteButton, CENTER);
        titledPane.setGraphic(header);

        return titledPane;
    }

    private Bounds calculateSize(Node node) {
        Pane pane = new Pane(node);
        new Scene(pane);
        pane.applyCss();
        pane.layout();
        return node.getBoundsInLocal();
    }

}
