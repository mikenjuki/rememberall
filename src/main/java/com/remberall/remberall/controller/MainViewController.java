package com.remberall.remberall.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class MainViewController {

    @FXML private Button addPersonButton;
    @FXML private TextField searchTextField;
    @FXML private ComboBox<String> sortOptionsBox;

    @FXML
    public void initialize() {
        searchTextField.setOnKeyReleased(e -> {
            String query = searchTextField.getText();
            PersonController controller = PersonController.getInstance();
            if (controller != null) {
                controller.filterList(query);
            }
        });

        sortOptionsBox.getItems().addAll("A-Z", "Birthday", "Last Contacted");
        sortOptionsBox.setOnAction(event -> {
            String choice = sortOptionsBox.getValue();
            PersonController controller = PersonController.getInstance();
            if (controller != null) {
                switch (choice) {
                    case "A-Z" -> controller.sortByName();
                    case "Birthday" -> controller.sortByBirthday();
                    case "Last Contacted" -> controller.sortByLastContacted();
                }
            }
        });

        addPersonButton.setOnAction(event -> openAddPersonForm());
    }


    private void openAddPersonForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/add-person.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 450, 700);
            scene.getStylesheets().add(getClass().getResource("/view/styles/global.css").toExternalForm());
            Stage stage = new Stage();
            stage.setTitle("Add New Person");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}