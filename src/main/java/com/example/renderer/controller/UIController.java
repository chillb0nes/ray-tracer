package com.example.renderer.controller;

import com.example.renderer.model.Material;
import com.example.renderer.model.Scene;
import com.example.renderer.model.light.LightSource;
import com.example.renderer.model.object.Mesh;
import com.example.renderer.model.object.Object3D;
import com.example.renderer.model.object.Sphere;
import com.example.renderer.model.object.Triangle;
import com.example.renderer.service.dialog.DialogFactory;
import com.example.renderer.service.render.DefaultRayTracer;
import com.example.renderer.service.render.OutlineRayTracer;
import com.example.renderer.service.render.RenderService;
import com.example.renderer.service.render.TaskAwareExecutorRenderer;
import com.example.renderer.service.serialization.SerializationService;
import com.example.renderer.view.component.InputGroup;
import com.example.renderer.view.control.Point3DSpinner;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.ThreadContext;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private HBox errorBox;
    @FXML
    private Label errorBoxText;

    private Scene scene;
    private MultipleSelectionModel<Object3D> selectionModel;
    private ObservableList<Object3D> sceneObjects;
    private RenderService renderService;
    private ExecutorService loaderPresenter;
    private DialogFactory dialogFactory;
    private Stage stage;
    private Timeline errorBoxAnimation;
    private SerializationService serializationService;
    private File lastSelectedFile;

    @FXML
    public void initialize() {
        scene = new Scene();

        cameraOriginSpinner.valueProperty().bindBidirectional(scene.cameraOriginProperty());
        aaCheckBox.selectedProperty().bindBidirectional(scene.aaEnabledProperty());
        selectionModel = objectList.getSelectionModel();
        errorBoxAnimation = slideFromTopAnimation();

        bindFovSlider();
        bindMenuWidth();
        bindSelectedItemCenter();
        setMenuItemsUserData();
        addSceneListeners();

        serializationService = new SerializationService();//todo DI
        TaskAwareExecutorRenderer rayTracer = new DefaultRayTracer(4, Color.LIGHTBLUE);
        TaskAwareExecutorRenderer outlineRayTracer = new OutlineRayTracer(Color.MAGENTA);
        renderService = new RenderService(rayTracer, outlineRayTracer);//todo DI
        renderService.setOnRunning(e -> {
            long startTime = System.currentTimeMillis();
            showLoader();
            ThreadContext.push(String.valueOf(startTime));
        });
        renderService.setOnSucceeded(e -> {
            image.setImage(renderService.getValue());
            hideLoader();
            log.trace("Image is rendered in {}ms", () -> {
                String start = ThreadContext.pop();
                return System.currentTimeMillis() - Long.parseLong(start);
            });
        });
        serializationService = new SerializationService();
        lastSelectedFile = new File(System.getProperty("user.home"));
        saveBtn.disableProperty().bind(image.imageProperty().isNull());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        dialogFactory = new DialogFactory(stage, serializationService);//todo DI
    }

    private void addSceneListeners() {
        addListener(scene.fovProperty(), newValue -> update());
        addListener(scene.aaEnabledProperty(), newValue -> update());
        addListener(scene.cameraOriginProperty(), newValue -> update());
        addListener(scene.selectedProperty(), newValue -> update());
        addListener(scene.getObjects(), change -> update());
        //addListener(scene.getLights(), change -> update());
    }

    private void bindFovSlider() {
        fovSlider.valueProperty().bindBidirectional(scene.fovProperty());
        fovSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double increment = fovSlider.getBlockIncrement();
            double value = Math.round(newValue.doubleValue() / increment) * increment;
            fovSlider.setValue(value);
        });
        fovSlider.setOnScroll(this::changeFovValue);
        fovSlider.setValue(45);
        fovLabel.textProperty().bind(fovSlider.valueProperty().asString(Locale.US, "%.1f"));
    }

    private void bindMenuWidth() {
        ReadOnlyDoubleProperty widthProperty = objectPosition.widthProperty();
        objectList.prefWidthProperty().bind(widthProperty);
        importBtn.prefWidthProperty().bind(widthProperty.divide(2));
        exportBtn.prefWidthProperty().bind(widthProperty.divide(2));
        newObjectBtn.prefWidthProperty().bind(widthProperty);
        aaCheckBox.prefWidthProperty().bind(widthProperty);
        saveBtn.prefWidthProperty().bind(sceneControls.widthProperty());
    }

    private void bindSelectedItemCenter() {
        scene.selectedProperty().bind(objectList.getSelectionModel().selectedItemProperty());

        addListener(objectPosition.valueProperty(), newValue -> {
            Object3D selected = scene.getSelected();
            if (selected != null) {
                if (!newValue.equals(selected.getCenter())) {
                    selected.setCenter(newValue);
                    update();
                    selectionModel.select(selected);
                }
            }
        });

        addListener(scene.selectedProperty(), selected -> {
            Point3D center;
            if (selected != null) {
                center = selected.getCenter();
                objectPosition.setDisable(false);
            } else {
                center = Point3D.ZERO;
                objectPosition.setDisable(true);
            }
            if (!center.equals(objectPosition.getValue())) {
                objectPosition.setValue(center);
            }
        });
    }

    private void setMenuItemsUserData() {
        sphereItem.setUserData(Sphere.class);
        triangleItem.setUserData(Triangle.class);
        meshItem.setUserData(Mesh.class);
        lightSourceItem.setUserData(LightSource.class);

    }

    public void updateModel() {
        generateModel();
    }

    private void update() {
        sceneObjects = FXCollections.observableArrayList();
        sceneObjects.addAll(scene.getObjects());
        sceneObjects.addAll(scene.getLights());
        objectList.setItems(sceneObjects);
        renderService.render(scene);
    }

    public void resetFocus() {
        objectList.getSelectionModel().clearSelection();
        root.requestFocus();
    }

    private void changeFovValue(ScrollEvent scrollEvent) {
        if (scrollEvent.getTextDeltaY() > 0) {
            fovSlider.increment();
        } else {
            fovSlider.decrement();
        }
    }

    public void importScene() throws IOException {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(lastSelectedFile);
        fc.getExtensionFilters().add(new ExtensionFilter("JSON", "*.json"));
        fc.setTitle("Import Scene");
        File file = fc.showOpenDialog(stage);
        if (file != null) {
            lastSelectedFile = file.getParentFile();
            String json = new String(Files.readAllBytes(file.toPath()));
            scene = serializationService.fromJson(json, Scene.class);
            update();
        }
    }

    public void exportScene() throws IOException {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(lastSelectedFile);
        fc.getExtensionFilters().add(new ExtensionFilter("JSON", "*.json"));
        fc.setTitle("Export Scene");
        File file = fc.showSaveDialog(stage);
        if (file != null) {
            lastSelectedFile = file.getParentFile();
            String json = serializationService.toJson(scene);
            Files.write(file.toPath(), json.getBytes());
            log.debug("Saved current scene to {}", file);
        }
    }

    public void saveImage() throws IOException {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(lastSelectedFile);
        fc.getExtensionFilters().addAll(
                new ExtensionFilter("JPG", "*.jpg", "*.jpeg"),
                new ExtensionFilter("GIF", "*.gif"),
                new ExtensionFilter("PNG", "*.png")
        );
        fc.setTitle("Save Image");
        File file = fc.showSaveDialog(stage);
        if (file != null) {
            lastSelectedFile = file.getParentFile();
            String extension = fc.getSelectedExtensionFilter().getDescription();
            ImageIO.write(toBufferedImage(image.getImage(), extension), extension, file);
            log.debug("Saved current image to {}", file);
        }
    }

    private BufferedImage toBufferedImage(Image image, String extension) {
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        if (!"jpg".equalsIgnoreCase(extension)) {
            return bImage;
        }
        // fix for wrong colors in jpg files
        BufferedImage bImage1 = new BufferedImage(bImage.getWidth(), bImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        bImage1.getGraphics().drawImage(bImage, 0, 0, null);
        return bImage1;
    }

    @SuppressWarnings("unchecked")
    public void newObject(Event event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        Class<Object3D> userData = (Class<Object3D>) menuItem.getUserData();
        Dialog<Object3D> newDialog = dialogFactory.createNewDialog(userData);
        Optional<Object3D> result = newDialog.showAndWait();
        result.ifPresent(object3D -> {
            scene.addObject(object3D);
            objectList.getSelectionModel().select(object3D);
        });
    }

    public void editObject() {
        if (!objectList.getItems().isEmpty()) {
            Object3D selectedItem = selectionModel.getSelectedItem();
            Dialog<Object3D> editDialog = dialogFactory.createEditDialog(selectedItem);
            Optional<Object3D> result = editDialog.showAndWait();
            result.ifPresent(object3D -> {
                scene.updateObject(selectedItem, object3D);
                objectList.getSelectionModel().select(object3D);
            });
        }
    }

    public void deleteObject() {
        if (!objectList.getItems().isEmpty()) {
            Object3D selectedItem = selectionModel.getSelectedItem();
            scene.deleteObject(selectedItem);
        }
    }

    public void clearScene() {
        scene.setFov(45);
        scene.getObjects().clear();
        scene.getLights().clear();
        update();
    }

    private void showLoader() {
        loaderPresenter = Executors.newSingleThreadExecutor();
        loaderPresenter.execute(() -> {
            try {
                Thread.sleep(200);
                loader.setVisible(true);
            } catch (InterruptedException e) {
                loader.setVisible(false);
            }
        });
    }

    private void hideLoader() {
        loader.setVisible(false);
        loaderPresenter.shutdownNow();
    }

    public void closeErrorBox() {
        double totalHeight = errorBox.getHeight() + errorBox.getInsets().getTop() + errorBox.getLayoutY();
        errorBox.translateYProperty().setValue(-totalHeight);
        errorBox.setVisible(false);
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
            closeErrorBox();

            String message = ExceptionUtils.getMessage(e);
            String cause = ExceptionUtils.getRootCauseMessage(e);
            String errorMessage = String.format("[ERROR] %s \nCaused by %s", message, cause);

            errorBoxText.setText(errorMessage);
            errorBox.setVisible(true);
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
