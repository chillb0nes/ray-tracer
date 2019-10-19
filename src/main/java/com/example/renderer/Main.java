package com.example.renderer;

import com.example.renderer.controller.UIController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class Main extends Application {
    /*private double xOffset = 0;
    private double yOffset = 0;*/

    @Autowired
    private FXMLLoader fxmlLoader;

    @Override
    public void start(Stage primaryStage) throws IOException {
        SpringConfiguration.initContext(this);
        fxmlLoader.setLocation(getClass().getResource("/app.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/style.css");

        UIController controller = fxmlLoader.getController();

        Thread.currentThread().setUncaughtExceptionHandler(controller.getExceptionHandler());

        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.setResizable(false);
        primaryStage.show();
        root.requestFocus();

        /*primaryStage.initStyle(StageStyle.UNDECORATED);
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });*/
    }

    public static void main(String... args) {
        launch(args);
    }

}