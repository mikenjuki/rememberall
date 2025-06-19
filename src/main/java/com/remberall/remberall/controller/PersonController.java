package com.remberall.remberall.controller;

import com.remberall.remberall.model.Person;
import com.remberall.remberall.model.PersonDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class PersonController {

    @FXML
    private ListView<Person> personListView;

    private final PersonDAO personDAO = new PersonDAO();
    private final ObservableList<Person> personList = FXCollections.observableArrayList();

    // Singleton instance
    private static PersonController instance;

    public PersonController() {
        instance = this;
    }

    public static PersonController getInstance() {
        return instance;
    }

    @FXML
    public void initialize() {
        List<Person> persons = personDAO.getAllPersons();
        personList.setAll(persons);
        personListView.setItems(personList);

        // 🟡 ADD THIS BLOCK
        personListView.setCellFactory(param -> new ListCell<Person>() {
            @Override
            protected void updateItem(Person person, boolean empty) {
                super.updateItem(person, empty);
                if (empty || person == null) {
                    setText(null);
                } else {
                    setText(person.getName() + " - Last met: " + person.getLastMeetingDate());
                }
            }
        });

        personListView.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, selected) -> {
                    GiftIdeaController giftCtrl = GiftIdeaController.getInstance();
                    if (giftCtrl != null) {
                        giftCtrl.loadGiftsFor(selected);
                    }
                });

        // 🟡 ADD THIS DOUBLE CLICK HANDLER
        personListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Person selectedPerson = personListView.getSelectionModel().getSelectedItem();
                if (selectedPerson != null) {
                    openEditPersonForm(selectedPerson);
                }
            }
        });
    }


    public void filterList(String query) {
        List<Person> filtered = personList.stream()
                .filter(p -> p.getName().toLowerCase().contains(query.toLowerCase())
                        || p.getNotes().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());

        personListView.setItems(FXCollections.observableArrayList(filtered));
    }

    public void refreshList() {
        List<Person> updatedPersons = personDAO.getAllPersons();
        personList.setAll(updatedPersons);
        personListView.setItems(personList);
    }

    private void openEditPersonForm(Person selectedPerson) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/add-person.fxml"));

            Parent root = loader.load();

            AddPersonController controller = loader.getController();
            controller.setPerson(selectedPerson); // set person to edit

            Stage stage = new Stage();

            stage.setScene(new Scene(root, 450, 500)); // Set preferred width and height
            stage.setResizable(false);
            stage.setTitle("Edit Person");

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}