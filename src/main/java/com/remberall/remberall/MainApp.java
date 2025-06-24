package com.remberall.remberall;

import com.remberall.remberall.controller.ControllerManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException; //
import java.util.logging.Level;
import java.util.logging.Logger;


public class MainApp extends Application {

    private static final Logger LOGGER = Logger.getLogger(MainApp.class.getName());

    @Override
    public void start(Stage stage) throws IOException {
        // Initialize the ControllerManager early
        ControllerManager controllerManager = ControllerManager.getInstance();

        // Load the main-view.fxml using the ControllerManager's helper
        // This will automatically instantiate MainViewController and PersonController (via include)
        // and register them with the ControllerManager.
        ControllerManager.FXMLLoaderResult<?> mainViewResult =
                controllerManager.loadFXMLAndGetController("/main-view.fxml"); //

        Scene scene = new Scene(mainViewResult.root, 640, 500); //
        scene.getStylesheets().add(getClass().getResource("/view/styles/global.css").toExternalForm()); //
        stage.setTitle("Rememberall"); //
        stage.setScene(scene); //
        stage.show(); //

        // After showing, if you need to ensure GiftIdeaController is ready (e.g., if it's not
        // part of a loaded FXML initially), you might explicitly load its FXML if it has one
        // or ensure its constructor is called somehow. In your setup, GiftIdeaController
        // also uses a singleton for now, so PersonController's listener gets it.
    }

    public static void main(String[] args) { //
        // Configure logging
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-7s] %3$s - %5$s%n");
        LOGGER.setLevel(Level.ALL);

        launch(); //
    }
}