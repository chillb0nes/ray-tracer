package com.example.renderer.view.control;

import com.example.renderer.model.Material;
import com.example.renderer.view.component.ValueNode;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.Getter;

import static com.example.renderer.view.util.ObservableUtils.addListener;

@Getter
public class MaterialControl extends VBox implements ValueNode<Material> {
    private ColorPicker colorPicker;
    private DoubleSpinner diffuseSpinner;
    private DoubleSpinner specularSpinner;
    private DoubleSpinner reflectivitySpinner;
    private DoubleSpinner transmittanceSpinner;
    private DoubleSpinner specularExpSpinner;
    private DoubleSpinner iorSpinner;
    private ObjectProperty<Material> value;

    public MaterialControl() {
        colorPicker = new ColorPicker();
        colorPicker.prefWidthProperty().bind(widthProperty());

        diffuseSpinner = new DoubleSpinner();
        specularSpinner = new DoubleSpinner();
        reflectivitySpinner = new DoubleSpinner();
        transmittanceSpinner = new DoubleSpinner();
        specularExpSpinner = new DoubleSpinner();
        iorSpinner = new DoubleSpinner();

        getChildren().addAll(
                new Label("Color"),
                colorPicker,
                new Label("Diffuse"),
                diffuseSpinner,
                new Label("Specular"),
                specularSpinner,
                new Label("Reflectivity"),
                reflectivitySpinner,
                new Label("Transmittance"),
                transmittanceSpinner,
                new Label("Specular exponent"),
                specularExpSpinner,
                new Label("Index of refraction"),
                iorSpinner
        );

        value = new ReadOnlyObjectWrapper<>();
        addListener(value, newMaterial -> {
            colorPicker.setValue(newMaterial.getColor());
            diffuseSpinner.setValue(newMaterial.getDiffuse());
            specularSpinner.setValue(newMaterial.getSpecular());
            reflectivitySpinner.setValue(newMaterial.getReflectivity());
            transmittanceSpinner.setValue(newMaterial.getTransmittance());
            specularExpSpinner.setValue(newMaterial.getSpecularExp());
            iorSpinner.setValue(newMaterial.getIor());
        });

        addListener(colorPicker.valueProperty(),
                newColor -> value.get().setColor(newColor));

        addListener(diffuseSpinner.valueProperty(),
                newValue -> value.get().setDiffuse(newValue));

        addListener(specularSpinner.valueProperty(),
                newValue -> value.get().setSpecular(newValue));

        addListener(reflectivitySpinner.valueProperty(),
                newValue -> value.get().setReflectivity(newValue));

        addListener(transmittanceSpinner.valueProperty(),
                newValue -> value.get().setTransmittance(newValue));

        addListener(specularExpSpinner.valueProperty(),
                newValue -> value.get().setSpecularExp(newValue));

        addListener(iorSpinner.valueProperty(),
                newValue -> value.get().setIor(newValue));

        setDefaultValue();
    }

    @Override
    public Material getDefaultValue() {
        return Material.DEFAULT;
    }

    @Override
    public ObjectProperty<Material> valueProperty() {
        return value;
    }

    @Override
    public Material getValue() {
        return value.get();
    }

    @Override
    public void setValue(Material value) {
        valueProperty().set(value);
    }
}
