package com.remberall.remberall.controller;


import com.remberall.remberall.model.Person;
import com.remberall.remberall.model.PersonDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class AddPersonController {

    @FXML private TextField nameField;
    @FXML private DatePicker birthdayPicker;
    @FXML private TextArea notesArea;
    @FXML private TextField interestsField;
    @FXML private DatePicker lastMeetingDatePicker;
    @FXML private TextField relationshipTypeField;
    @FXML private Button saveButton;

    private final PersonDAO personDAO = new PersonDAO();
    @FXML private ComboBox<Person.RelationshipType> relationshipBox;
    private Person editingPerson;

    @FXML
    public void initialize() {
        relationshipBox.getItems().setAll(Person.RelationshipType.values());
        saveButton.setOnAction(e -> handleSave());
    }


    @FXML
    private void handleSave() {
        String name = nameField.getText();
        LocalDate birthday = birthdayPicker.getValue();
        String notes = notesArea.getText();
        List<String> interests = Arrays.asList(interestsField.getText().split(","));
        LocalDate lastMeetingDate = lastMeetingDatePicker.getValue();
        Person.RelationshipType relType = relationshipBox.getValue();

        if (editingPerson != null) {
            // 🔁 Update existing person
            editingPerson.setName(name);
            editingPerson.setBirthday(birthday);
            editingPerson.setNotes(notes);
            editingPerson.setInterests(interests);
            editingPerson.setLastMeetingDate(lastMeetingDate);
            editingPerson.setRelationshipType(relType);

            personDAO.updatePerson(editingPerson);
        } else {
            // ➕ Insert new person
            Person newPerson = new Person(0, name, birthday, notes, interests, lastMeetingDate, relType);
            personDAO.insertPerson(newPerson);
        }

        PersonController.getInstance().refreshList();
        closeWindow();
    }



    @FXML
    public void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
    public void setPerson(Person person) {
        this.editingPerson = person;
        nameField.setText(person.getName());
        birthdayPicker.setValue(person.getBirthday());
        notesArea.setText(person.getNotes());
        interestsField.setText(String.join(",", person.getInterests()));
        lastMeetingDatePicker.setValue(person.getLastMeetingDate());
        relationshipBox.setValue(person.getRelationshipType());
    }



}