package com.example.renderer.model.object;

import com.example.renderer.model.light.LightSource;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import javafx.geometry.Point3D;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
@JsonSubTypes({
        @Type(value = Sphere.class, name = "sphere"),
        @Type(value = Triangle.class, name = "triangle"),
        @Type(value = Mesh.class, name = "mesh"),
        @Type(value = LightSource.class, name = "lightSource"),
})
public interface Object3D {

    Point3D getCenter();

    void setCenter(Point3D center);

}
