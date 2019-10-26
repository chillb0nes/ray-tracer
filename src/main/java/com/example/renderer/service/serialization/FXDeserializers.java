package com.example.renderer.service.serialization;

import com.example.renderer.service.serialization.color.ColorDeserializer;
import com.example.renderer.service.serialization.observable.ObservableListDeserializer;
import com.example.renderer.service.serialization.observable.ObservableMapDeserializer;
import com.example.renderer.service.serialization.observable.ObservableSetDeserializer;
import com.example.renderer.service.serialization.point3d.Point3DDeserializer;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

public class FXDeserializers extends Deserializers.Base {

    @Override
    public JsonDeserializer<?> findBeanDeserializer(JavaType type,
                                                    DeserializationConfig config,
                                                    BeanDescription beanDesc) {
        Class<?> raw = type.getRawClass();
        if (Color.class.isAssignableFrom(raw)) {
            return new ColorDeserializer();
        }
        if (Point3D.class.isAssignableFrom(raw)) {
            return new Point3DDeserializer();
        }
        return null;
    }

    @Override
    public JsonDeserializer<?> findCollectionDeserializer(CollectionType type,
                                                          DeserializationConfig config,
                                                          BeanDescription beanDesc,
                                                          TypeDeserializer elementTypeDeserializer,
                                                          JsonDeserializer<?> elementDeserializer) {
        Class<?> raw = type.getRawClass();
        if (ObservableList.class.isAssignableFrom(raw)) {
            return new ObservableListDeserializer(type.getContentType().getRawClass());
        }
        if (ObservableSet.class.isAssignableFrom(raw)) {
            return new ObservableSetDeserializer(type.getContentType().getRawClass());
        }
        return null;
    }

    @Override
    public JsonDeserializer<?> findMapDeserializer(MapType type,
                                                   DeserializationConfig config,
                                                   BeanDescription beanDesc,
                                                   KeyDeserializer keyDeserializer,
                                                   TypeDeserializer elementTypeDeserializer,
                                                   JsonDeserializer<?> elementDeserializer) {
        Class<?> raw = type.getRawClass();
        if (ObservableMap.class.isAssignableFrom(raw)) {
            return new ObservableMapDeserializer(type.getKeyType().getRawClass(), type.getContentType().getRawClass());
        }
        return null;
    }
}
