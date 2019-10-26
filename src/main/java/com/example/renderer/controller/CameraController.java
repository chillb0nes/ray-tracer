package com.example.renderer.controller;

import com.example.renderer.model.Scene;
import com.example.renderer.view.component.InputGroup;
import com.example.renderer.view.control.Point3DSpinner;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.example.renderer.view.util.ObservableUtils.addListener;

public class CameraController implements Initializable {
    @FXML
    public InputGroup root;
    @FXML
    private Label fovLabel;
    @FXML
    private Slider fovSlider;
    @FXML
    private Point3DSpinner cameraOriginSpinner;

    @Autowired
    private SceneHolder sceneHolder;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fovSlider.setOnScroll(this::changeFovValue);
        addListener(fovSlider.valueProperty(), newValue -> {
            double increment = fovSlider.getBlockIncrement();
            double value = Math.round(newValue.doubleValue() / increment) * increment;
            fovSlider.setValue(value);
        });
        fovLabel.textProperty().bind(fovSlider.valueProperty().asString(Locale.US, "%.1f"));
        addListener(sceneHolder.sceneProperty(), this::resetBindings);
    }

    private void changeFovValue(ScrollEvent scrollEvent) {
        if (scrollEvent.getTextDeltaY() > 0) {
            fovSlider.increment();
        } else {
            fovSlider.decrement();
        }
    }

    private void resetBindings(Scene scene) {
        cameraOriginSpinner.valueProperty().bindBidirectional(scene.cameraOriginProperty());
        fovSlider.valueProperty().bindBidirectional(scene.fovProperty());
    }
}
