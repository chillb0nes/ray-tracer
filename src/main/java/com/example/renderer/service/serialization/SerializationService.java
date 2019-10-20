package com.example.renderer.service.serialization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;

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
}
