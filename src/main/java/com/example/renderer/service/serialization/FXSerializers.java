package com.example.renderer.service.serialization;

import com.example.renderer.service.serialization.color.ColorSerializer;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.Serializers;
import javafx.scene.paint.Color;

public class FXSerializers extends Serializers.Base {

    @Override
    public JsonSerializer<?> findSerializer(SerializationConfig config,
                                            JavaType type,
                                            BeanDescription beanDesc) {
        Class<?> raw = type.getRawClass();
        if (Color.class.isAssignableFrom(raw)) {
            return new ColorSerializer();
        }
        return null;
    }

}
