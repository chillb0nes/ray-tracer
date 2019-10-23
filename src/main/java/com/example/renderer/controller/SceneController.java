package com.example.renderer.controller;

import com.example.renderer.model.Scene;
import com.example.renderer.model.light.LightSource;
import com.example.renderer.model.object.Mesh;
import com.example.renderer.model.object.Object3D;
import com.example.renderer.model.object.Sphere;
import com.example.renderer.model.object.Triangle;
import com.example.renderer.service.dialog.DialogFactory;
import com.example.renderer.service.serialization.SerializationService;
import com.example.renderer.view.component.InputGroup;
import com.example.renderer.view.control.Point3DSpinner;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point3D;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.example.renderer.view.util.ObservableUtils.addListener;

@Log4j2
@Getter
public class SceneController implements Initializable {
    @FXML
    private InputGroup root;
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

    @Autowired
    private SerializationService serializationService;

    @Autowired
    private DialogFactory dialogFactory;

    @Autowired
    private SceneHolder sceneHolder;

    @Autowired
    private Stage stage;

    private MultipleSelectionModel<Object3D> selectionModel;
    @Setter
    private File lastSelectedFile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        selectionModel = objectList.getSelectionModel();
        lastSelectedFile = new File(System.getProperty("user.home"));
        dialogFactory.setOwner(stage);

        ReadOnlyDoubleProperty widthProperty = objectPosition.widthProperty();
        objectList.prefWidthProperty().bind(widthProperty);
        importBtn.prefWidthProperty().bind(widthProperty.divide(2));
        exportBtn.prefWidthProperty().bind(widthProperty.divide(2));
        newObjectBtn.prefWidthProperty().bind(widthProperty);
        aaCheckBox.prefWidthProperty().bind(widthProperty);

        setMenuItemsUserData();
        addListener(sceneHolder.sceneProperty(), this::resetBindings);
    }

    private void resetBindings(Scene scene) {
        aaCheckBox.selectedProperty().bindBidirectional(scene.aaEnabledProperty());
        scene.selectedProperty().bind(objectList.getSelectionModel().selectedItemProperty());

        addListener(objectPosition.valueProperty(), newValue -> {
            Object3D selected = scene.getSelected();
            if (selected != null) {
                if (!newValue.equals(selected.getCenter())) {
                    selected.setCenter(newValue);
                    selectionModel.select(selected);
                    sceneHolder.requestUpdate();
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

    public void importScene() throws IOException {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(lastSelectedFile);
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("JSON", "*.json"),
                new FileChooser.ExtensionFilter("YAML", "*.yaml", "*.yml")
        );
        fc.setTitle("Import Scene");
        File file = fc.showOpenDialog(stage);
        if (file != null) {
            lastSelectedFile = file.getParentFile();
            String value = new String(Files.readAllBytes(file.toPath()));
            String format = fc.getSelectedExtensionFilter().getDescription();
            Scene scene;
            switch (format) {
                case "JSON":
                    scene = serializationService.fromJson(value, Scene.class);
                    break;
                case "YAML":
                    scene = serializationService.fromYaml(value, Scene.class);
                    break;
                default:
                    scene = serializationService.fromFile(value, Scene.class);
                    break;
            }
            sceneHolder.setScene(scene);
            log.debug("Loaded current scene from {}", file);
        }
    }

    public void exportScene() throws IOException {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(lastSelectedFile);
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json"),
                new FileChooser.ExtensionFilter("YAML", "*.yaml", "*.yml")
        );
        fc.setTitle("Export Scene");
        File file = fc.showSaveDialog(stage);
        if (file != null) {
            lastSelectedFile = file.getParentFile();
            String value = "";
            String format = fc.getSelectedExtensionFilter().getDescription();
            switch (format) {
                case "JSON":
                    value = serializationService.toJson(sceneHolder.getScene());
                    break;
                case "YAML":
                    value = serializationService.toYaml(sceneHolder.getScene());
                    break;
            }
            Files.write(file.toPath(), value.getBytes());
            log.debug("Saved current scene to {}", file);
        }
    }

    @SuppressWarnings("unchecked")
    public void newObject(Event event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        Class<Object3D> userData = (Class<Object3D>) menuItem.getUserData();
        Dialog<Object3D> newDialog = dialogFactory.createNewDialog(userData);
        Optional<Object3D> result = newDialog.showAndWait();
        result.ifPresent(object3D -> {
            sceneHolder.getScene().addObject(object3D);
            objectList.getSelectionModel().select(object3D);
        });
    }

    public void editObject() {
        if (!objectList.getItems().isEmpty()) {
            Object3D selectedItem = selectionModel.getSelectedItem();
            Dialog<Object3D> editDialog = dialogFactory.createEditDialog(selectedItem);
            Optional<Object3D> result = editDialog.showAndWait();
            result.ifPresent(object3D -> {
                sceneHolder.getScene().updateObject(selectedItem, object3D);
                objectList.getSelectionModel().select(object3D);
            });
        }
    }

    public void deleteObject() {
        if (!objectList.getItems().isEmpty()) {
            Object3D selectedItem = selectionModel.getSelectedItem();
            sceneHolder.getScene().removeObject(selectedItem);
        }
    }

    public void clearScene() {
        sceneHolder.getScene().setFov(45);
        sceneHolder.getScene().getObjects().clear();
        sceneHolder.getScene().getLights().clear();
    }
}
