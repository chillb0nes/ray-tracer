package com.example.renderer;

import com.gluonhq.ignite.spring.SpringContext;
import javafx.scene.paint.Color;
import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Component;

import java.beans.PropertyEditorSupport;
import java.util.Collections;

@Configuration
@PropertySource("classpath:application.properties")
public class SpringConfiguration {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public static CustomEditorConfigurer customEditorConfigurer() {
        CustomEditorConfigurer configurer = new CustomEditorConfigurer();
        configurer.setCustomEditors(Collections.singletonMap(Color.class, ColorPropertyEditor.class));
        return configurer;
    }

    public static void initContext(Object contextRoot) {
        String basePackage = contextRoot.getClass().getPackage().getName();
        new SpringContext(contextRoot, () -> Collections.singleton(basePackage)).init();
    }

    @Component
    public static class ColorPropertyEditor extends PropertyEditorSupport {
        @Override
        public String getAsText() {
            return "#" + getValue().hashCode();
        }

        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            setValue(Color.web(text));
        }
    }

}
