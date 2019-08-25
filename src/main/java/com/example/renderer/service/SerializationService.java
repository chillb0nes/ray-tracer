package com.example.renderer.service;

import com.example.renderer.model.Scene;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SerializationService {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String serialize(Scene scene, boolean prettyPrinting) throws JsonProcessingException {
        return prettyPrinting
                ? objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(scene)
                : objectMapper.writeValueAsString(scene);
    }
}
