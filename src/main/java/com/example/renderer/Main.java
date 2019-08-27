package com.example.renderer;

import com.example.renderer.controller.UIController;
import com.example.renderer.view.control.MeshControl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Main extends Application {
    /*private double xOffset = 0;
    private double yOffset = 0;*/

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/style.css");

        UIController controller = loader.getController();
        controller.setStage(primaryStage);

        Thread.currentThread().setUncaughtExceptionHandler(controller.getExceptionHandler());

        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(windowEvent -> {
            controller.shutdown();
            Platform.exit();
        });

        /*primaryStage.initStyle(StageStyle.UNDECORATED);
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });*/

        scene = new Scene(new MeshControl());
        scene.getStylesheets().add("/style.css");
        primaryStage.setScene(scene);

        primaryStage.show();

        log.trace("trace");
        log.info("info");
        log.warn("warn");
        log.debug("debug");
        log.error("error");

        controller.setUp();
    }

    public static void main(String... args) {
        launch(args);
    }

}