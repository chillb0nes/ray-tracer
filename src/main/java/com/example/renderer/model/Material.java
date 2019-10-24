package com.example.renderer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Random;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Material {
    /** Diffuse color */
    private Color color;
    /** Amount of reflected light */
    private double diffuse;
    /** Highlight brightness */
    private double specular;
    /** Reflection brightness */
    private double reflectivity;
    /** Amount of transmitted light */
    private double transmittance;
    /** Highlight size */
    private double specularExp;
    /** Index of Refraction */
    private double ior;

    public static Material random() {
        Random random = new Random();
        switch (random.nextInt(5)) {
            case 0:
                return IVORY;
            case 1:
                return GLASS;
            case 2:
                return RUBBER;
            case 3:
                return MIRROR;
            default:
                return DEFAULT;
        }
    }

    public final static Material DEFAULT = builder()
            .color(Color.GRAY)
            .diffuse(0.5)
            .specular(0.25)
            .reflectivity(0)
            .transmittance(0)
            .specularExp(50)
            .ior(1)
            .build();

    public final static Material IVORY = builder()
            .color(Color.IVORY)
            .diffuse(0.6)
            .specular(0.3)
            .reflectivity(0.01)
            .transmittance(0)
            .specularExp(50)
            .ior(1)
            .build();

    public final static Material GLASS = builder()
            .color(Color.POWDERBLUE)
            .diffuse(0.2)
            .specular(0.5)
            .reflectivity(0.1)
            .transmittance(0.8)
            .specularExp(125)
            .ior(1.5)
            .build();

    public final static Material RUBBER = builder()
            .color(Color.FIREBRICK)
            .diffuse(0.9)
            .specular(0.1)
            .reflectivity(0)
            .transmittance(0)
            .specularExp(10)
            .ior(1)
            .build();

    public final static Material MIRROR = builder()
            .color(Color.WHITE)
            .diffuse(0.1)
            .specular(10)
            .reflectivity(0.8)
            .transmittance(0)
            .specularExp(1425)
            .ior(1)
            .build();

    @JsonIgnore
    public boolean isReflective() {
        return reflectivity > 0;
    }

    @JsonIgnore
    public boolean isTranslucent() {
        return transmittance > 0;
    }
}
