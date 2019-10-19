package com.example.renderer.service.serialization;

import com.example.renderer.model.object.Mesh;
import com.example.renderer.model.object.Renderable;
import com.example.renderer.model.object.Sphere;
import com.example.renderer.model.object.Triangle;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;
import javafx.geometry.Point3D;
import lombok.Getter;
import org.hildan.fxgson.FxGson;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Component
public class SerializationService {

    @Getter
    private Gson gson;

    public SerializationService() {
        gson = FxGson.fullBuilder()
                .registerTypeAdapter(Renderable.class, RenderableHelper.getSerializer())
                .registerTypeAdapter(Renderable.class, RenderableHelper.getDeserializer())
                .registerTypeAdapter(Sphere.class, RenderableHelper.getSerializer())
                .registerTypeAdapter(Sphere.class, RenderableHelper.getDeserializer())
                .registerTypeAdapter(Triangle.class, RenderableHelper.getSerializer())
                .registerTypeAdapter(Triangle.class, RenderableHelper.getDeserializer())
                .registerTypeAdapter(Mesh.class, RenderableHelper.getSerializer())
                .registerTypeAdapter(Mesh.class, RenderableHelper.getDeserializer())
                .registerTypeAdapter(Point3D.class, Point3DHelper.getSerializer())
                .registerTypeAdapter(Point3D.class, Point3DHelper.getDeserializer())
                .setPrettyPrinting()
                .create();
    }

    public String toJson(Object value) {
        return gson.toJson(value);
    }

    public <T> T fromJson(String json, Class<T> valueType) {
        return gson.fromJson(json, valueType);
    }

    public <T> T copy(T value) {
        return gson.fromJson(toJson(value), new TypeReference<T>() {
            @Override
            public Type getType() {
                return value.getClass();
            }
        }.getType());
    }
}
