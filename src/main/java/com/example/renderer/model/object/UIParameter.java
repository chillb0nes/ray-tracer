package com.example.renderer.model.object;

import com.example.renderer.service.ControlFactory;
import com.example.renderer.service.DefaultControlFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface UIParameter {

    String value() default "";

    int order() default -1;

    boolean showTitle() default true;

    Class<? extends ControlFactory> controlFactory() default DefaultControlFactory.class;

}
