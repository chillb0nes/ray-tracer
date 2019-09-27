package com.example.renderer.controller;

import com.example.renderer.model.Material;
import com.example.renderer.model.Scene;
import com.example.renderer.model.light.LightSource;
import com.example.renderer.model.object.Mesh;
import com.example.renderer.model.object.Object3D;
import com.example.renderer.model.object.Sphere;
import com.example.renderer.model.object.Triangle;
import com.example.renderer.service.DialogFactory;
import com.example.renderer.service.RenderService;
import com.example.renderer.service.SerializationService;
import com.example.renderer.view.component.InputGroup;
import com.example.renderer.view.component.ScrollablePane;
import com.example.renderer.view.control.Point3DSpinner;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.ThreadContext;

import java.util.Locale;
import java.util.Optional;
import java.util.Random;

import static com.example.renderer.view.util.ObservableUtils.addListener;

@Log4j2
@Setter
public class UIController {
    @FXML
    public HBox root;
    @FXML
    public VBox sidebar;
    @FXML
    public ProgressIndicator loader;
    @FXML
    private ImageView image;
    @FXML
    private Label fovLabel;
    @FXML
    private Slider fovSlider;
    @FXML
    private Point3DSpinner cameraOriginSpinner;
    @FXML
    private InputGroup sceneControls;
    @FXML
    private Button importBtn;
    @FXML
    private Button exportBtn;
    @FXML
    private ListView<Object3D> objectList;
    @FXML
    private Point3DSpinner objectPosition;
    @FXML
    private MenuButton newObjectBtn;
    @FXML
    private MenuItem sphereItem;
    @FXML
    private MenuItem triangleItem;
    @FXML
    private MenuItem meshItem;
    @FXML
    private MenuItem lightSourceItem;
    @FXML
    private CheckBox aaCheckBox;
    @FXML
    private Button saveBtn;
    @FXML
    private ScrollablePane imageArea;
    @FXML
    private ScrollablePane sliderArea;
    @FXML
    private HBox errorBox;
    @FXML
    private Label errorBoxText;

    private Scene scene;
    private MultipleSelectionModel<Object3D> selectionModel;
    private ObservableList<Object3D> sceneObjects;
    private RenderService renderService;
    private DialogFactory dialogFactory;
    private Stage stage;
    private Timeline errorBoxAnimation;

    @FXML
    public void initialize() {
        scene = new Scene();
        addSceneListeners();
        sceneObjects = FXCollections.observableArrayList();

        renderService = new RenderService();//todo DI
        renderService.setOnRunning(e -> {
            loader.setVisible(true);
            ThreadContext.push(String.valueOf(System.currentTimeMillis()));
        });
        renderService.setOnSucceeded(e -> {
            image.setImage(renderService.getValue());
            loader.setVisible(false);
            log.trace("Image is rendered in {}ms", () -> {
                String start = ThreadContext.pop();
                return System.currentTimeMillis() - Long.parseLong(start);
            });
            renderService.reset();
        });

        fovSlider.valueProperty().bindBidirectional(scene.fovProperty());
        fovSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double increment = fovSlider.getBlockIncrement();
            double value = Math.round(newValue.doubleValue() / increment) * increment;
            fovSlider.setValue(value);
        });
        fovSlider.setValue(45);
        fovLabel.textProperty().bind(fovSlider.valueProperty().asString(Locale.US, "%.1f"));

        cameraOriginSpinner.valueProperty().bindBidirectional(scene.cameraOriginProperty());

        fovSlider.setOnScroll(this::changeFovValue);

        ReadOnlyDoubleProperty widthProperty = objectPosition.widthProperty();
        objectList.prefWidthProperty().bind(widthProperty);
        importBtn.prefWidthProperty().bind(widthProperty.divide(2));
        exportBtn.prefWidthProperty().bind(widthProperty.divide(2));
        newObjectBtn.prefWidthProperty().bind(widthProperty);
        aaCheckBox.prefWidthProperty().bind(widthProperty);
        saveBtn.prefWidthProperty().bind(sceneControls.widthProperty());

        selectionModel = objectList.getSelectionModel();
        objectPosition.setDisable(false);
        //todo bind objectPosition and selected item center

        aaCheckBox.selectedProperty().bindBidirectional(scene.aaEnabledProperty());

        errorBoxAnimation = slideFromTopAnimation();

        sphereItem.setUserData(Sphere.class);
        triangleItem.setUserData(Triangle.class);
        meshItem.setUserData(Mesh.class);
        lightSourceItem.setUserData(LightSource.class);
    }

    private void addSceneListeners() {
        addListener(scene.fovProperty(), newValue -> update());
        addListener(scene.aaEnabledProperty(), newValue -> update());
        addListener(scene.cameraOriginProperty(), newValue -> update());
        addListener(scene.getObjects(), change -> update());
        addListener(scene.getLights(), change -> update());
        addListener(scene.getSelected(), change -> update());
    }

    public void setUp() {
        dialogFactory = new DialogFactory(stage, new SerializationService());//todo DI
        hideErrorBox();
        resetFocus();
    }

    public void updateModel() {
        generateModel();
    }

    private void update() {
        sceneObjects.clear();
        sceneObjects.addAll(scene.getObjects());
        sceneObjects.addAll(scene.getLights());
        objectList.setItems(sceneObjects);
        renderService.render(scene);
    }

    public void resetFocus() {
        selectionModel.clearSelection();
        scene.getSelected().clear();
        root.requestFocus();
    }

    private void changeFovValue(ScrollEvent scrollEvent) {
        if (scrollEvent.getTextDeltaY() > 0) {
            fovSlider.increment();
        } else {
            fovSlider.decrement();
        }
    }

    public void importScene() {
        throw new UnsupportedOperationException("importScene not implemented");
    }

    public void exportScene() {
        throw new UnsupportedOperationException("exportScene not implemented");
    }

    public void saveImage() {
        throw new UnsupportedOperationException("saveImage not implemented");
    }

    @SuppressWarnings("unchecked")
    public void newObject(Event event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        Class<Object3D> userData = (Class<Object3D>) menuItem.getUserData();
        Dialog<Object3D> newDialog = dialogFactory.createNewDialog(userData);
        Optional<Object3D> result = newDialog.showAndWait();
        result.ifPresent(object3D -> scene.addObject(object3D));
    }

    public void editObject() {
        if (!sceneObjects.isEmpty()) {
            Object3D selectedItem = selectionModel.getSelectedItem();
            Dialog<Object3D> editDialog = dialogFactory.createEditDialog(selectedItem);
            Optional<Object3D> result = editDialog.showAndWait();
            result.ifPresent(object3D -> scene.updateObject(selectedItem, object3D));
        }
    }

    public void deleteObject() {
        if (!sceneObjects.isEmpty()) {
            Object3D selectedItem = selectionModel.getSelectedItem();
            scene.deleteObject(selectedItem);
        }
    }

    public void closeErrorBox() {
        hideErrorBox();
        errorBoxText.setText("");
    }

    private void hideErrorBox() {
        double totalHeight = errorBox.getHeight() + errorBox.getInsets().getTop() + errorBox.getLayoutY();
        errorBox.translateYProperty().setValue(-totalHeight);
    }

    private Timeline slideFromTopAnimation() {
        Timeline timeline = new Timeline();
        KeyValue keyValue = new KeyValue(errorBox.translateYProperty(), 0, Interpolator.EASE_IN);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(300), keyValue);
        timeline.getKeyFrames().add(keyFrame);
        return timeline;
    }

    public Thread.UncaughtExceptionHandler getExceptionHandler() {
        return (t, e) -> {
            log.error("Uncaught exception:", e);
            hideErrorBox();

            String message = ExceptionUtils.getMessage(e);
            String cause = ExceptionUtils.getRootCauseMessage(e);
            String errorMessage = String.format("[ERROR] %s \nCaused by %s", message, cause);

            errorBoxText.setText(errorMessage);
            errorBoxAnimation.play();
        };
    }

    public void printPixelPosition(MouseEvent e) {
        log.debug("Mouse click at {}:{}", (int) e.getSceneX(), (int) e.getSceneY());
    }

    private void generateModel() {
        scene.getObjects().clear();
        Random random = new Random();
        Sphere sphere1 = new Sphere(new Point3D(0, -1, -7), 0.5, Material.random());

        Sphere sphere2 = new Sphere(new Point3D(1, -1, -6), 0.5, Material.random());
        Sphere sphere3 = new Sphere(new Point3D(2, -1, -5), 0.5, Material.random());

        Sphere sphere4 = new Sphere(new Point3D(-1, -1, -6), 0.5, Material.random());
        Sphere sphere5 = new Sphere(new Point3D(-2, -1, -5), 0.5, Material.random());

        Sphere sphere6 = new Sphere(new Point3D(0, 0, -7), 0.5, Material.random());
        Sphere sphere7 = new Sphere(new Point3D(0, 1, -7), 0.5, Material.random());

        Sphere sphere8 = new Sphere(new Point3D(0, 0, -3), 0.5, Material.GLASS);

        Triangle triangle1 = new Triangle(
                new Point3D(0, 1, -7),
                new Point3D(2, -1, -5),
                new Point3D(1, 2, -6),
                Material.RUBBER);

        Triangle triangle2 = new Triangle(
                new Point3D(0, 1, -7),
                new Point3D(-1, 2, -6),
                new Point3D(-2, -1, -5),
                Material.RUBBER);

        Triangle triangle3 = new Triangle(
                new Point3D(0, 1, -7),
                new Point3D(2, -1, -5),
                new Point3D(-2, -1, -5),
                Material.IVORY);

        Mesh mesh1 = new Mesh();

        scene.getObjects().setAll(
                sphere1, sphere2, sphere3, sphere4, sphere5, sphere6, sphere7, sphere8,
                triangle1, triangle2, triangle3,
                mesh1);

        LightSource light1 = new LightSource(new Point3D(0, 5, 5), random.nextDouble());
        LightSource light2 = new LightSource(new Point3D(-2, 2, 2), random.nextDouble());
        LightSource light3 = new LightSource(new Point3D(5, 1, 0), random.nextDouble());

        scene.getLights().setAll(light1, light2, light3);
    }
}
