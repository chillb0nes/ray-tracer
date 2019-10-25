package com.example.renderer.service.serialization;

import com.example.renderer.model.Material;
import com.example.renderer.model.object.Mesh;
import com.example.renderer.model.object.Triangle;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.annotations.Beta;
import javafx.geometry.Point3D;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Getter
@Component
public class SerializationService {

    private ObjectMapper objectMapper;
    private ObjectMapper yamlMapper;

    public SerializationService() {
        objectMapper = new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .enable(SerializationFeature.INDENT_OUTPUT)
                .registerModule(new FXModule());

        yamlMapper = new ObjectMapper(new YAMLFactory())
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .registerModule(new FXModule());
    }

    public String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to serialize value", e);
        }
    }

    public <T> T fromJson(String json, Class<T> valueType) {
        try {
            return objectMapper.readValue(json, valueType);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to deserialize value", e);
        }
    }

    public String toYaml(Object value) {
        try {
            return yamlMapper.writeValueAsString(value);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to serialize value", e);
        }
    }

    public <T> T fromYaml(String yaml, Class<T> valueType) {
        try {
            return yamlMapper.readValue(yaml, valueType);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to deserialize value", e);
        }
    }

    public <T> T fromFile(String file, Class<T> valueType) {
        try {
            return objectMapper.readValue(file, valueType);
        } catch (Exception ex) {
            try {
                return yamlMapper.readValue(file, valueType);
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to deserialize value", e);
            }
        }
    }

    public <T> T copy(T value) {
        try {
            String json = toJson(value);
            log.trace("Copying value {}\nJSON string:\n{}", value, json);
            return objectMapper.readValue(json, new TypeReference<T>() {
                @Override
                public Type getType() {
                    return value.getClass();
                }
            });
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to copy value", e);
        }
    }

    @Beta
    public Mesh fromObj(String obj) {
        try {
            Map<Boolean, List<String>> verticlesAndFaces = Arrays.stream(obj.split("\\r?\\n"))
                    .filter(line -> line.startsWith("v ") || line.startsWith("f "))
                    .collect(Collectors.partitioningBy(s -> s.startsWith("v ")));

            List<Point3D> verticles = verticlesAndFaces.get(true).stream()
                    .map(SerializationService::toPoint3D)
                    .collect(Collectors.toList());

            List<Triangle> triangles = verticlesAndFaces.get(false).stream()
                    .map(line -> {
                        String[] verts = line.split(" ");
                        return new Triangle(
                                toPoint3D(verts[1], verticles),
                                toPoint3D(verts[2], verticles),
                                toPoint3D(verts[3], verticles)
                        );
                    })
                    .collect(Collectors.toList());
            return new Mesh(Material.DEFAULT, triangles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize value", e);
        }
    }

    private static Point3D toPoint3D(String line) {
        String[] coords = line.split(" ");
        return new Point3D(
                Double.valueOf(coords[1]),
                Double.valueOf(coords[2]),
                Double.valueOf(coords[3])
        );
    }

    private static Point3D toPoint3D(String vert, List<Point3D> verticles) {
        int i = Integer.parseInt(vert.split("/")[0]) - 1;
        return verticles.get(i);
    }
}
