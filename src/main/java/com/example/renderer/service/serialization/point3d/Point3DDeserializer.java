package com.example.renderer.service.serialization.point3d;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import javafx.geometry.Point3D;

import java.io.IOException;
import java.util.Map;

public class Point3DDeserializer extends StdDeserializer<Point3D> {

    public Point3DDeserializer() {
        super(Point3D.class);
    }

    @Override
    public Point3D deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
        Map<String, Double> map = jp.readValueAs(new TypeReference<Map<String, Double>>() {});
        return new Point3D(map.get("x"), map.get("y"), map.get("z"));
    }
}
