package com.example.renderer;

import com.example.renderer.controller.UIController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    /*private double xOffset = 0;
    private double yOffset = 0;*/

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/app.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/style.css");

        UIController controller = loader.getController();
        controller.setStage(primaryStage);

        Thread.currentThread().setUncaughtExceptionHandler(controller.getExceptionHandler());

        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.setResizable(false);

        /*primaryStage.initStyle(StageStyle.UNDECORATED);
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });*/

        primaryStage.show();
        controller.setUp();
    }

    public static void main(String... args) {
        launch(args);
    }

}