package com.example.renderer.view.component.dialog;

import com.example.renderer.model.Material;
import com.example.renderer.model.light.LightSource;
import com.example.renderer.model.object.*;
import com.example.renderer.view.component.InputGroup;
import com.example.renderer.view.component.ValueNode;
import com.example.renderer.view.control.*;
import com.google.common.collect.ImmutableMap;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class CustomDialog<T extends Object3D> extends Dialog<T> {

    private static final Map<Class, Supplier<ValueNode>> CONTROLS = ImmutableMap.of(
            Sphere.class, SphereControl::new,
            Triangle.class, TriangleControl::new,
            Mesh.class, () -> new MeshControl(TriangleControl::new),
            LightSource.class, LightSourceControl::new,
            Material.class, MaterialControl::new);

    private Class<? extends Object3D> clazz;
    private Function<Class, ValueNode> controlFactory;

    protected T item;
    protected ValueNode<T> objectControl;
    protected ValueNode<Material> materialControl;

    protected CustomDialog(Class<? extends Object3D> clazz) {
        this.clazz = clazz;
        controlFactory = valueClass -> CONTROLS.get(valueClass).get();
        init();
    }

    protected CustomDialog(T item) {
        this.item = item;
        this.clazz = item.getClass();
        controlFactory = valueClass -> CONTROLS.get(valueClass).get();
        init();
    }

    @SuppressWarnings("unchecked")
    private void init() {
        objectControl = controlFactory.apply(clazz);
        if (isRenderable()) {
            materialControl = controlFactory.apply(Material.class);
        }
        getDialogPane().setContent(createContentPane());
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        setResultConverter(getCustomResultConverter());
        Platform.runLater(() -> getDialogPane().lookupButton(ButtonType.CANCEL).requestFocus());
        initStyle(StageStyle.UTILITY);
    }

    protected abstract Callback<ButtonType, T> getCustomResultConverter();

    private HBox createContentPane() {
        HBox content = new HBox();
        String title = String.format("%s parameters", getPrettyClassName());
        content.getChildren().add(new InputGroup(title, (Node) objectControl));
        if (isRenderable()) {
            content.getChildren().add(new InputGroup("Material parameters", (Node) materialControl));
        }
        content.setSpacing(10);
        removeBottomPadding(content);
        return content;
    }

    private void removeBottomPadding(Region region) {
        region.needsLayoutProperty().addListener(((observable, layoutDone, needsLayout) -> {
            if (layoutDone) {
                Insets padding = region.getPadding();
                region.setPadding(new Insets(padding.getTop(), padding.getRight(), 0, padding.getLeft()));
            }
        }));
    }

    protected String getPrettyClassName() {
        String name = clazz.getSimpleName();
        String[] parts = StringUtils.splitByCharacterTypeCamelCase(name);
        if (parts.length > 1) {
            name = StringUtils.join(parts, " ");
        }
        return name;
    }

    protected boolean isRenderable() {
        return Renderable.class.isAssignableFrom(clazz);
    }

}