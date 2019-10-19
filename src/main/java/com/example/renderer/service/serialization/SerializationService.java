package com.example.renderer.service.serialization;

import com.example.renderer.model.object.Mesh;
import com.example.renderer.model.object.Renderable;
import com.example.renderer.model.object.Sphere;
import com.example.renderer.model.object.Triangle;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
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
        JsonSerializer<Renderable> renderableSerializer = RenderableSerializationHelper.getSerializer();
        JsonDeserializer<Renderable> renderableDeserializer = RenderableSerializationHelper.getDeserializer();
        gson = FxGson.fullBuilder()
                .registerTypeAdapter(Renderable.class, renderableSerializer)
                .registerTypeAdapter(Renderable.class, renderableDeserializer)
                .registerTypeAdapter(Sphere.class, renderableSerializer)
                .registerTypeAdapter(Sphere.class, renderableDeserializer)
                .registerTypeAdapter(Triangle.class, renderableSerializer)
                .registerTypeAdapter(Triangle.class, renderableDeserializer)
                .registerTypeAdapter(Mesh.class, renderableSerializer)
                .registerTypeAdapter(Mesh.class, renderableDeserializer)
                .registerTypeAdapter(Point3D.class, Point3DSerializationHelper.getSerializer())
                .registerTypeAdapter(Point3D.class, Point3DSerializationHelper.getDeserializer())
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
