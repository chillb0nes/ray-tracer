package com.example.renderer.controller;

import com.example.renderer.model.Material;
import com.example.renderer.model.Scene;
import com.example.renderer.model.light.LightSource;
import com.example.renderer.model.object.Mesh;
import com.example.renderer.model.object.Object3D;
import com.example.renderer.model.object.Sphere;
import com.example.renderer.model.object.Triangle;
import com.example.renderer.service.render.RenderService;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point3D;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.example.renderer.view.util.ObservableUtils.addListener;

@Log4j2
@Setter
public class UIController implements Initializable {
    @FXML
    public HBox root;
    @FXML
    public VBox sidebar;
    @FXML
    public ProgressIndicator loader;
    @FXML
    private ImageView image;
    @FXML
    private Button saveBtn;
    @FXML
    private HBox errorBox;
    @FXML
    private Label errorBoxText;

    @FXML
    private CameraController cameraController;

    @FXML
    private SceneController sceneController;

    @Autowired
    private RenderService renderService;

    @Autowired
    private Stage stage;

    @Autowired
    private SceneHolder sceneHolder;

    private ObservableList<Object3D> sceneObjects;
    private ExecutorService loaderPresenter;
    private Timeline errorBoxAnimation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        saveBtn.prefWidthProperty().bind(sceneController.getRoot().widthProperty());
        saveBtn.disableProperty().bind(image.imageProperty().isNull());
        errorBoxAnimation = slideFromTopAnimation();

        configureRenderService();

        root.setOnKeyPressed(keyEvent -> {
            if (keyEvent.isControlDown()
                    && keyEvent.isAltDown()
                    && KeyCode.Z == keyEvent.getCode()) {
                generateScene();
            }
        });

        sceneHolder.setScene(new Scene());
        sceneHolder.registerListener(this::update);
        sceneHolder.requestUpdate();
    }

    private void configureRenderService() {
        renderService.setOnRunning(e -> {
            long startTime = System.currentTimeMillis();
            showLoader();
            ThreadContext.push(String.valueOf(startTime));
        });
        renderService.setOnSucceeded(e -> {
            image.setImage(renderService.getValue());
            hideLoader();
            log.debug("Image is rendered in {}ms", () -> {
                String start = ThreadContext.pop();
                return System.currentTimeMillis() - Long.parseLong(start);
            });
        });
        renderService.setOnCancelled(e -> {
            log.trace("Render task cancelled");
            hideLoader();
        });
        renderService.setOnFailed(e -> {
            log.error("Render task failed", renderService.getException());
            hideLoader();
        });
    }

    private void update() {
        sceneObjects = FXCollections.observableArrayList();
        sceneObjects.addAll(sceneHolder.getScene().getObjects());
        sceneObjects.addAll(sceneHolder.getScene().getLights());
        sceneController.getObjectList().setItems(sceneObjects);
        renderService.render(sceneHolder.getScene());
    }

    public void resetFocus() {
        sceneController.getObjectList().getSelectionModel().clearSelection();
        root.requestFocus();
    }

    public void saveImage() throws IOException {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(sceneController.getLastSelectedFile());
        fc.getExtensionFilters().addAll(
                new ExtensionFilter("JPG", "*.jpg", "*.jpeg"),
                new ExtensionFilter("GIF", "*.gif"),
                new ExtensionFilter("PNG", "*.png")
        );
        fc.setTitle("Save Image");
        File file = fc.showSaveDialog(stage);
        if (file != null) {
            sceneController.setLastSelectedFile(file.getParentFile());
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
        if (loaderPresenter != null) {
            loaderPresenter.shutdownNow();
        }
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

    private void generateScene() {
        Scene scene = new Scene();
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
                Material.random());

        Triangle triangle2 = new Triangle(
                new Point3D(0, 1, -7),
                new Point3D(-1, 2, -6),
                new Point3D(-2, -1, -5));

        Triangle triangle3 = new Triangle(
                new Point3D(0, 1, -7),
                new Point3D(2, -1, -5),
                new Point3D(-2, -1, -5));

        Mesh mesh1 = new Mesh(Material.random(), triangle2, triangle3);

        LightSource light1 = new LightSource(new Point3D(0, 5, 5), random.nextDouble());
        LightSource light2 = new LightSource(new Point3D(-2, 2, 2), random.nextDouble());
        LightSource light3 = new LightSource(new Point3D(5, 1, 0), random.nextDouble());

        scene.addObjects(
                sphere1, sphere2, sphere3, sphere4, sphere5, sphere6, sphere7, sphere8,
                triangle1, triangle2, triangle3,
                mesh1,
                light1, light2, light3);
        sceneHolder.setScene(scene);
    }
}
