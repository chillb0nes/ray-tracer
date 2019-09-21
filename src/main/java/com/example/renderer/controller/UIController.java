package com.example.renderer.controller;

import com.example.renderer.model.Material;
import com.example.renderer.model.Scene;
import com.example.renderer.model.light.LightSource;
import com.example.renderer.model.object.*;
import com.example.renderer.service.DialogFactory;
import com.example.renderer.service.RenderService;
import com.example.renderer.service.SerializationService;
import com.example.renderer.view.component.InputGroup;
import com.example.renderer.view.component.ScrollablePane;
import com.example.renderer.view.control.Point3DSpinner;
import com.google.common.collect.ImmutableSet;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

import java.util.Collections;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private MenuItem polygonItem;
    @FXML
    private MenuItem meshItem;
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
    private ObservableList<Object3D> sceneObjects;
    private RenderService renderService;
    private DialogFactory dialogFactory;
    private Stage stage;
    private Timeline errorBoxAnimation;

    @FXML
    public void initialize() {
        scene = new Scene();
        sceneObjects = FXCollections.observableArrayList();

        renderService = new RenderService();
        renderService.setExecutor(Executors.newSingleThreadExecutor());
        renderService.setOnRunning(e -> {
            loader.setVisible(true);
        });
        renderService.setOnSucceeded(e -> {
            image.setImage(renderService.getValue());
            loader.setVisible(false);
            renderService.reset();
        });

        fovSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double increment = fovSlider.getBlockIncrement();
            double value = Math.round(newValue.doubleValue() / increment) * increment;
            fovSlider.setValue(value);
            fovLabel.setText(String.format(Locale.US, "%.1f", value));
            scene.setFov(value);
            update();
        });
        fovSlider.setValue(45);

        /*imageArea.setOnScrollEnded(this::changeFovValue);
        sliderArea.setOnScrollEnded(this::changeFovValue);*/

        ReadOnlyDoubleProperty widthProperty = objectPosition.widthProperty();
        objectList.prefWidthProperty().bind(widthProperty);
        importBtn.prefWidthProperty().bind(widthProperty.divide(2));
        exportBtn.prefWidthProperty().bind(widthProperty.divide(2));
        newObjectBtn.prefWidthProperty().bind(widthProperty);
        aaCheckBox.prefWidthProperty().bind(widthProperty);
        saveBtn.prefWidthProperty().bind(sceneControls.widthProperty());

        objectList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue instanceof Renderable) {
                //scene.setSelected(ImmutableSet.of((Renderable) newValue));
                //`update();
            }
        });

        aaCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            scene.setAaEnabled(newValue);
            update();
        });

        errorBoxAnimation = slideFromTopAnimation();

        //todo
        meshItem.setUserData(Mesh.class);
    }

    public void setUp() {
        dialogFactory = new DialogFactory(stage, new SerializationService());
        hideErrorBox();
        resetFocus();
    }

    private void updateCamera(Double x, Double y, Double z) {
        if (x == null) x = scene.getCameraOrigin().getX();
        if (y == null) y = scene.getCameraOrigin().getY();
        if (z == null) z = scene.getCameraOrigin().getZ();

        scene.setCameraOrigin(new Point3D(x, y, z));
    }

    public void updateModel() {
        generateModel();

        sceneObjects.clear();
        sceneObjects.addAll(scene.getObjects());
        sceneObjects.addAll(scene.getLights());

        objectList.setItems(sceneObjects);
        update();
    }

    private void update() {
        renderService.render(scene);
    }

    public void resetFocus() {
        objectList.getSelectionModel().clearSelection();
        scene.setSelected(Collections.emptySet());
        root.requestFocus();
    }

    private void changeFovValue(ScrollEvent scrollEvent) {
        if (scrollEvent.getTextDeltaY() > 0) {
            fovSlider.increment();
        } else {
            fovSlider.decrement();
        }
        update();
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

    public void editObject() {
        Object3D selectedItem = objectList.getSelectionModel().getSelectedItem();
        Dialog<Object3D> editDialog = dialogFactory.createEditDialog(selectedItem);
        editDialog.show();
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

    public void shutdown() {
        ((ExecutorService) renderService.getExecutor()).shutdownNow();
    }

    private void generateModel() {
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

        scene.setObjects(ImmutableSet.of(sphere1, sphere2, sphere3, sphere4, sphere5, sphere6, sphere7, sphere8,
                triangle1, triangle2, triangle3, mesh1));

        LightSource light1 = new LightSource(new Point3D(0, 5, 5), random.nextDouble() * 2);
        LightSource light2 = new LightSource(new Point3D(-2, 2, 2), random.nextDouble() * 2);
        LightSource light3 = new LightSource(new Point3D(5, 1, 0), random.nextDouble() * 2);

        scene.setLights(ImmutableSet.of(light1, light2, light3));
    }
}
