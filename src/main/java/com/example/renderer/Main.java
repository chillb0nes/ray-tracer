package com.example.renderer;

import com.example.renderer.controller.UIController;
import com.example.renderer.model.Material;
import com.example.renderer.model.object.Sphere;
import com.example.renderer.service.ModalService;
import com.example.renderer.view.control.MeshControl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point3D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

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

        controller.setUp();
    }

    public static void main(String... args) {
        launch(args);
    }

}