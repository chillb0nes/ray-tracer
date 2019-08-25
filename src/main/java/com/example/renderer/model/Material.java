package com.example.renderer.model;

import javafx.scene.paint.Color;
import lombok.Builder;
import lombok.Data;

import java.util.Random;

@Data
@Builder
public class Material {

    private Color color;                //цвет
    private double diffuse;             //сколько света возвращается
    private double specular;            //насколько яркий блик
    private double reflectivity;        //отражательная способность
    private double transmittance;       //прозрачность
    private double specularExp;         //размер блика
    private double ior;                 //показатель преломления

    public static Material random2() {
        Random r = new Random();
        return builder()
                .color(Color.color(r.nextDouble(), r.nextDouble(), r.nextDouble()))
                .diffuse(r.nextDouble())
                .specular(r.nextDouble())
                .reflectivity(r.nextDouble() / 2)
                .transmittance(0)
                .ior(1)
                .specularExp(r.nextDouble() * 100)
                .build();
    }

    public static Material random() {
        Random random = new Random();
        switch (random.nextInt(4)) {
            case 0:
                return IVORY;
            case 1:
                return GLASS;
            case 2:
                return RUBBER;
            case 3:
                return MIRROR;
            default:
                return random2();
        }
    }

    public final static Material IVORY = builder()
            .color(Color.color(0.4, 0.4, 0.3))
            .diffuse(0.6)
            .specular(0.3)
            .reflectivity(0.1)
            .transmittance(0)
            .specularExp(50)
            .ior(1)
            .build();

    public final static Material GLASS = builder()
            .color(Color.color(0.6, 0.7, 0.8))
            .diffuse(0.2)
            .specular(0.5)
            .reflectivity(0.1)
            .transmittance(0.8)
            .specularExp(125)
            .ior(1.5)
            .build();

    public final static Material RUBBER = builder()
            .color(Color.color(0.5, 0.1, 0.1))
            .diffuse(0.9)
            .specular(0.1)
            .reflectivity(0)
            .transmittance(0)
            .specularExp(10)
            .ior(1)
            .build();

    public final static Material MIRROR = builder()
            .color(Color.color(1, 1, 1))
            .diffuse(0.1)
            .specular(10)
            .reflectivity(0.8)
            .transmittance(0)
            .specularExp(1425)
            .ior(1)
            .build();

    public boolean isReflective() {
        return reflectivity > 0;
    }

    public boolean isTranslucent() {
        return transmittance > 0;
    }
}
