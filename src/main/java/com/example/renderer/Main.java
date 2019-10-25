package com.example.renderer;

import com.example.renderer.controller.UIController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

public class Main extends Application {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private FXMLLoader fxmlLoader;

    @Override
    public void start(Stage primaryStage) throws IOException {
        SpringConfiguration.initContext(this);
        registerPrimaryStageBean(primaryStage);
        fxmlLoader.setLocation(getClass().getResource("/fxml/app.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/style.css");

        UIController controller = fxmlLoader.getController();
        Thread.currentThread().setUncaughtExceptionHandler(controller.getExceptionHandler());

        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.setResizable(false);
        primaryStage.setTitle("Java Ray Tracer");
        primaryStage.show();
        root.requestFocus();
    }

    private void registerPrimaryStageBean(Stage primaryStage) {
        ((AnnotationConfigApplicationContext) applicationContext)
                .registerBean("primaryStage", Stage.class, () -> primaryStage);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.exit(0);
    }

    public static void main(String... args) {
        launch(args);
    }

}