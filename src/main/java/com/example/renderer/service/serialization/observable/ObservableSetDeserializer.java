package com.example.renderer.service.serialization.observable;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.type.CollectionType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

import java.io.IOException;
import java.util.Set;

public class ObservableSetDeserializer extends StdDeserializer<ObservableSet> {
    private Class<?> contentType;

    public ObservableSetDeserializer(Class<?> contentType) {
        super(ObservableList.class);
        this.contentType = contentType;
    }

    @Override
    public ObservableSet<?> deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
        CollectionType collectionType = ctx.getTypeFactory().constructCollectionType(Set.class, contentType);
        Set<?> value = jp.getCodec().readValue(jp, collectionType);
        return FXCollections.observableSet(value);
    }
}
