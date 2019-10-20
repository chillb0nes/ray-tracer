package com.example.renderer.service.serialization.observable;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.type.CollectionType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.List;

public class ObservableListDeserializer extends StdDeserializer<ObservableList> {
    private Class<?> contentType;

    public ObservableListDeserializer(Class<?> contentType) {
        super(ObservableList.class);
        this.contentType = contentType;
    }

    @Override
    public ObservableList<?> deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
        CollectionType collectionType = ctx.getTypeFactory().constructCollectionType(List.class, contentType);
        List<?> value = jp.getCodec().readValue(jp, collectionType);
        return FXCollections.observableList(value);
    }
}
