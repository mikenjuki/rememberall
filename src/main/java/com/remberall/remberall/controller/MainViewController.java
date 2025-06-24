package com.remberall.remberall.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MainViewController {

    private static final Logger LOGGER = Logger.getLogger(MainViewController.class.getName());

    @FXML private Button addPersonButton;
    @FXML private TextField searchTextField;
    @FXML private ComboBox<String> sortOptionsBox;

    // Reference to PersonController, obtained via ControllerManager
    private PersonController personController;


    @FXML
    public void initialize() {
        // Get the PersonController instance from the manager
        // This assumes person-list.fxml is loaded before main-view.fxml,
        // or that PersonController's constructor registers it.
        // A more robust approach might be to inject it or have MainApp manage it.
        personController = ControllerManager.getInstance().getController(PersonController.class);
        if (personController == null) {
            LOGGER.warning("PersonController not found in ControllerManager during MainViewController initialization.");
        }


        searchTextField.setOnKeyReleased(e -> {
            String query = searchTextField.getText();
            if (personController != null) {
                personController.filterList(query);
            }
        });

        sortOptionsBox.getItems().addAll("A-Z", "Birthday", "Last Contacted");
        sortOptionsBox.setOnAction(event -> {
            String choice = sortOptionsBox.getValue();
            if (personController != null) {
                switch (choice) {
                    case "A-Z" -> personController.sortByName();
                    case "Birthday" -> personController.sortByBirthday();
                    case "Last Contacted" -> personController.sortByLastContacted();
                }
            }
        });
        addPersonButton.setOnAction(event -> openAddPersonForm());
    }

    private void openAddPersonForm() {
        try {
            // Use ControllerManager to load FXML and get controller
            ControllerManager.FXMLLoaderResult<AddPersonController> result =
                    ControllerManager.getInstance().loadFXMLAndGetController("/view/add-person.fxml");

            Parent root = result.root;
            AddPersonController addPersonController = result.controller;

            // Set a callback for when the add/edit form closes
            addPersonController.setRefreshCallback(() -> {
                if (personController != null) {
                    personController.refreshList();
                }
            });

            Scene scene = new Scene(root, 600, 550);
            scene.getStylesheets().add(getClass().getResource("/view/styles/global.css").toExternalForm());
            Stage stage = new Stage();
            stage.setTitle("Add New Person");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL); // Make it a modal window
            stage.showAndWait(); // Show it and wait for it to close

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error opening add person form", e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not open 'Add Person' form.");
            alert.setContentText("Please check the application logs for more details.");
            alert.showAndWait();
        }
    }
}