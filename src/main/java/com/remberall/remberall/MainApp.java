package com.remberall.remberall;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 640, 500);
        scene.getStylesheets().add(getClass().getResource("/view/styles/global.css").toExternalForm());
        stage.setTitle("Rememberall");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}