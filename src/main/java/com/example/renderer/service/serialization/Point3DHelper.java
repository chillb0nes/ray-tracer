package com.example.renderer.service.serialization;

import com.google.gson.*;
import javafx.geometry.Point3D;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Type;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Point3DHelper {

    public static JsonSerializer<Point3D> getSerializer() {
        return new Point3DJsonSerializer();
    }

    public static JsonDeserializer<Point3D> getDeserializer() {
        return new Point3DJsonDeserializer();
    }

    private static class Point3DJsonSerializer implements JsonSerializer<Point3D> {
        @Override
        public JsonElement serialize(Point3D point3D, Type type, JsonSerializationContext ctx) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("x", point3D.getX());
            jsonObject.addProperty("y", point3D.getY());
            jsonObject.addProperty("z", point3D.getZ());
            return jsonObject;
        }
    }

    private static class Point3DJsonDeserializer implements JsonDeserializer<Point3D> {
        @Override
        public Point3D deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            double x = jsonObject.getAsJsonPrimitive("x").getAsDouble();
            double y = jsonObject.getAsJsonPrimitive("y").getAsDouble();
            double z = jsonObject.getAsJsonPrimitive("z").getAsDouble();
            return new Point3D(x, y, z);
        }
    }
}
