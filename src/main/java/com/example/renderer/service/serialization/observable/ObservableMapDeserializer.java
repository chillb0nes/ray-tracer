package com.example.renderer.service.serialization.observable;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.type.MapType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.io.IOException;
import java.util.Map;

public class ObservableMapDeserializer extends StdDeserializer<ObservableMap> {
    private Class<?> keyType;
    private Class<?> valueType;

    public ObservableMapDeserializer(Class<?> keyType, Class<?> valueType) {
        super(ObservableMap.class);
        this.keyType = keyType;
        this.valueType = valueType;
    }

    @Override
    public ObservableMap<?, ?> deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
        MapType mapType = ctx.getTypeFactory().constructMapType(Map.class, keyType, valueType);
        Map<?, ?> value = jp.getCodec().readValue(jp, mapType);
        return FXCollections.observableMap(value);
    }
}
