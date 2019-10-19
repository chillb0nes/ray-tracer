package com.example.renderer.service.serialization;

import com.example.renderer.model.Material;
import com.example.renderer.model.object.Mesh;
import com.example.renderer.model.object.Renderable;
import com.example.renderer.model.object.Sphere;
import com.example.renderer.model.object.Triangle;
import com.google.gson.*;
import javafx.geometry.Point3D;
import org.hildan.fxgson.FxGson;

import java.lang.reflect.Type;

public class RenderableSerializationHelper {
    private static Gson helperGson = FxGson.fullBuilder()
            .registerTypeAdapter(Point3D.class, Point3DSerializationHelper.getSerializer())
            .registerTypeAdapter(Point3D.class, Point3DSerializationHelper.getDeserializer())
            .create();

    public static JsonSerializer<Renderable> getSerializer() {
        return new RenderableJsonSerializer();
    }

    public static JsonDeserializer<Renderable> getDeserializer() {
        return new RenderableJsonDeserializer();
    }

    private static class RenderableJsonSerializer implements JsonSerializer<Renderable> {
        @Override
        public JsonElement serialize(Renderable renderable, Type type, JsonSerializationContext ctx) {
            JsonObject jsonObject = helperGson.toJsonTree(renderable).getAsJsonObject();
            jsonObject.addProperty("type", renderable.getClass().getSimpleName().toLowerCase());
            if (renderable instanceof Mesh) {
                JsonArray triangles = jsonObject.getAsJsonArray("triangles");
                triangles.forEach(jsonElement -> jsonElement.getAsJsonObject().remove("material"));
            }
            return jsonObject;
        }
    }

    private static class RenderableJsonDeserializer implements JsonDeserializer<Renderable> {
        @Override
        public Renderable deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String typeName = jsonObject.getAsJsonPrimitive("type").getAsString();
            return deserializeRenderable(typeName, jsonObject, ctx);
        }

        private static Renderable deserializeRenderable(String type,
                                                        JsonObject jsonObject,
                                                        JsonDeserializationContext ctx) {
            switch (type.toLowerCase()) {
                case "sphere":
                    return helperGson.fromJson(jsonObject, Sphere.class);
                case "triangle":
                    return deserializeTriangle(jsonObject, ctx);
                case "mesh":
                    return deserializeMesh(jsonObject, ctx);
                default:
                    throw new IllegalArgumentException();
            }
        }

        private static Triangle deserializeTriangle(JsonObject jsonObject, JsonDeserializationContext ctx) {
            Point3D v0 = ctx.deserialize(jsonObject.get("v0"), Point3D.class);
            Point3D v1 = ctx.deserialize(jsonObject.get("v1"), Point3D.class);
            Point3D v2 = ctx.deserialize(jsonObject.get("v2"), Point3D.class);
            Triangle triangle = new Triangle(v0, v1, v2);
            if (jsonObject.has("material")) {
                Material material = ctx.deserialize(jsonObject.get("material"), Material.class);
                triangle.setMaterial(material);
            }
            return triangle;
        }

        private static Mesh deserializeMesh(JsonObject jsonObject, JsonDeserializationContext ctx) {
            JsonArray triangles = jsonObject.getAsJsonArray("triangles");
            Mesh mesh = new Mesh();
            triangles.forEach(jsonElement -> {
                Triangle triangle = deserializeTriangle(jsonElement.getAsJsonObject(), ctx);
                mesh.getTriangles().add(triangle);
            });
            if (jsonObject.has("material")) {
                Material material = ctx.deserialize(jsonObject.get("material"), Material.class);
                mesh.setMaterial(material);
            }
            return mesh;
        }
    }
}
