package com.example.renderer.service;

import com.example.renderer.model.object.Mesh;
import com.example.renderer.model.object.Renderable;
import com.example.renderer.model.object.Sphere;
import com.example.renderer.model.object.Triangle;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import javafx.geometry.Point3D;
import lombok.Getter;
import org.hildan.fxgson.FxGson;

import java.lang.reflect.Type;

public class SerializationService {

    @Getter
    private Gson gson;

    public SerializationService() {
        gson = FxGson.fullBuilder()
                .registerTypeAdapter(Renderable.class, (JsonSerializer<Renderable>) (renderable, type, ctx) -> {
                    JsonObject jsonObject = ctx.serialize(renderable).getAsJsonObject();
                    jsonObject.addProperty("type", renderable.getClass().getSimpleName().toLowerCase());
                    return jsonObject;
                })
                .registerTypeAdapter(Renderable.class, (JsonDeserializer<Renderable>) (jsonElement, type, ctx) -> {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    String typeName = jsonObject.getAsJsonPrimitive("type").getAsString();
                    return ctx.deserialize(jsonElement, forName(typeName));
                })
                .registerTypeAdapter(Point3D.class, (JsonSerializer<Point3D>) (point3D, type, ctx) -> {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("x", point3D.getX());
                    jsonObject.addProperty("y", point3D.getY());
                    jsonObject.addProperty("z", point3D.getZ());
                    return jsonObject;
                })
                .registerTypeAdapter(Point3D.class, (JsonDeserializer<Point3D>) (jsonElement, type, ctx) -> {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    double x = jsonObject.getAsJsonPrimitive("x").getAsDouble();
                    double y = jsonObject.getAsJsonPrimitive("y").getAsDouble();
                    double z = jsonObject.getAsJsonPrimitive("z").getAsDouble();
                    return new Point3D(x, y, z);
                })
                .setPrettyPrinting()
                .create();
    }

    private static Type forName(String name) {
        switch (name.toLowerCase()) {
            case "sphere":
                return Sphere.class;
            case "triangle":
                return Triangle.class;
            case "mesh":
                return Mesh.class;
            default:
                throw new IllegalArgumentException();
        }
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
