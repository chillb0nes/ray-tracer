package com.example.renderer.service;

import com.example.renderer.model.light.LightSource;
import com.example.renderer.model.object.Object3D;
import com.example.renderer.model.object.Renderable;
import com.example.renderer.model.object.UIParameter;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;

public class ModalService {

    public Stage createEditModal(Object3D edited, Stage owner) {
        Stage stage = new Stage();
        Scene scene = new Scene(renderEditLayout(edited));

        scene.getStylesheets().add("/style.css");

        stage.setScene(scene);
        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setResizable(false);

        return stage;
    }

    public Pane renderEditLayout(Object3D object3D) {
        Pane leftColumn = createObjectParametersPane();
        Pane rightColumn = new Pane();

        Arrays.stream(object3D.getClass().getDeclaredFields())
                .peek(field -> field.setAccessible(true))
                .filter(field -> field.isAnnotationPresent(UIParameter.class))
                .sorted(Comparator.comparing(field -> field.getAnnotation(UIParameter.class).order()))
                .map(this::transformUiParameter)
                .forEach(node -> leftColumn.getChildren().add(node));

        if (object3D instanceof Renderable) {
            //rightColumn = createMaterialParametersPane();
        }
        if (object3D instanceof LightSource) {
            //rightColumn  = createLightParametersPane();
        }
        return new HBox(leftColumn, rightColumn);
    }

    private Node transformUiParameter(Field annotatedField) {
        UIParameter uiParameter = annotatedField.getAnnotation(UIParameter.class);

        Node control;
        try {
            ControlFactory controlFactory = uiParameter.controlFactory().getDeclaredConstructor().newInstance();
            control = controlFactory.getByClass(annotatedField.getType());
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to create JavaFX Control for class " + annotatedField.getType(), e);
        }

        if (uiParameter.showTitle()) {
            String value = uiParameter.value();
            String labelText = value.isEmpty() ? splitAndCapitalize(annotatedField.getName()) : value;
            Label label = new Label(labelText);
            return new VBox(label, control);
        }

        return control;
    }

    private String splitAndCapitalize(String s) {
        String[] elements = StringUtils.splitByCharacterTypeCamelCase(s);
        String result = String.join(" ", elements);
        return StringUtils.capitalize(result);
    }

    private Pane createObjectParametersPane() {
        return new VBox();
    }

    private Pane createMaterialParametersPane() {
        return null;
    }

    private Pane createLightParametersPane() {
        return null;
    }

}
