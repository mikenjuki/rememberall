package com.remberall.remberall.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A centralized manager for JavaFX controllers.
 * This acts as a controlled singleton provider for controllers that
 * need to be accessed from multiple places.
 */
public class ControllerManager {

    private static final Logger LOGGER = Logger.getLogger(ControllerManager.class.getName());
    private static ControllerManager instance;
    private final Map<Class<?>, Object> controllers = new HashMap<>();

    private ControllerManager() {
        // Private constructor to enforce singleton pattern
    }

    public static ControllerManager getInstance() {
        if (instance == null) {
            instance = new ControllerManager();
        }
        return instance;
    }

    /**
     * Registers a controller instance. This should typically be called
     * immediately after an FXMLLoader has loaded an FXML and its controller.
     *
     * @param controller The controller instance to register.
     */
    public void registerController(Object controller) {
        if (controller != null) {
            controllers.put(controller.getClass(), controller);
            LOGGER.fine("Registered controller: " + controller.getClass().getName());
        }
    }

    /**
     * Retrieves a registered controller instance.
     *
     * @param controllerClass The Class object of the controller to retrieve.
     * @param <T> The type of the controller.
     * @return The controller instance, or null if not found.
     */
    @SuppressWarnings("unchecked")
    public <T> T getController(Class<T> controllerClass) {
        T controller = (T) controllers.get(controllerClass);
        if (controller == null) {
            LOGGER.warning("Controller not found in manager: " + controllerClass.getName());
        }
        return controller;
    }

    /**
     * Loads an FXML file and registers its controller.
     *
     * @param fxmlPath The path to the FXML file.
     * @param <T> The type of the controller.
     * @return A Pair containing the loaded Parent and the controller instance.
     * @throws IOException If the FXML file cannot be loaded.
     */
    public <T> FXMLLoaderResult<T> loadFXMLAndGetController(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        T controller = loader.getController();
        if (controller != null) {
            registerController(controller);
        }
        return new FXMLLoaderResult<>(root, controller);
    }

    // Helper class to return both Parent and Controller from loadFXMLAndGetController
    public static class FXMLLoaderResult<T> {
        public final Parent root;
        public final T controller;

        public FXMLLoaderResult(Parent root, T controller) {
            this.root = root;
            this.controller = controller;
        }
    }
}