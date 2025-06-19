package com.remberall.remberall.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class MainViewController {

    @FXML private Button addPersonButton;
    @FXML
    private TextField searchTextField;

    @FXML
    public void initialize() {
        searchTextField.setOnKeyReleased(e -> {
            String query = searchTextField.getText();
            PersonController personController = PersonController.getInstance();
            if (personController != null) {
                personController.filterList(query);
            }
        });
        addPersonButton.setOnAction(event -> openAddPersonForm());
    }

    private void openAddPersonForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/add-person.fxml"));


            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Add New Person");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}