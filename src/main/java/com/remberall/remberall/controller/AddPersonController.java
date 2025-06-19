package com.remberall.remberall.controller;


import com.remberall.remberall.model.GiftIdea;
import com.remberall.remberall.model.GiftIdeaDAO;
import com.remberall.remberall.model.Person;
import com.remberall.remberall.model.PersonDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
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
    @FXML private ListView<GiftIdea> giftListView;

    private final PersonDAO personDAO = new PersonDAO();
    @FXML private ComboBox<Person.RelationshipType> relationshipBox;
    private Person editingPerson;
    private ObservableList<GiftIdea> giftIdeas = FXCollections.observableArrayList();
    private final GiftIdeaDAO giftIdeaDAO = new GiftIdeaDAO();
    @FXML
    public void initialize() {
        relationshipBox.getItems().setAll(Person.RelationshipType.values());
        saveButton.setOnAction(e -> handleSave());

        giftListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                GiftIdea selectedGift = giftListView.getSelectionModel().getSelectedItem();
                if (selectedGift != null) {
                    TextInputDialog dialog = new TextInputDialog(selectedGift.getDescription());
                    dialog.setTitle("Edit Gift");
                    dialog.setHeaderText("Edit Gift Description");
                    dialog.setContentText("Description:");

                    dialog.showAndWait().ifPresent(newDesc -> {
                        selectedGift.setDescription(newDesc);
                        giftListView.refresh();
                    });
                }
            }
        });
    }

    @FXML
    private void handleSave() {
        if (nameField.getText().isEmpty() ||
                birthdayPicker.getValue() == null ||
                notesArea.getText().isEmpty() ||
                interestsField.getText().isEmpty() ||
                lastMeetingDatePicker.getValue() == null ||
                relationshipBox.getValue() == null) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setHeaderText(null);
            alert.setContentText("All fields must be filled out.");
            alert.showAndWait();
            return;
        }
        String name = nameField.getText();
        LocalDate birthday = birthdayPicker.getValue();
        String notes = notesArea.getText();
        List<String> interests = Arrays.asList(interestsField.getText().split(","));
        LocalDate lastMeetingDate = lastMeetingDatePicker.getValue();
        Person.RelationshipType relType = relationshipBox.getValue();

        if (editingPerson != null) {
            editingPerson.setName(name);
            editingPerson.setBirthday(birthday);
            editingPerson.setNotes(notes);
            editingPerson.setInterests(interests);
            editingPerson.setLastMeetingDate(lastMeetingDate);
            editingPerson.setRelationshipType(relType);

            personDAO.updatePerson(editingPerson);
        } else {
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
        giftIdeas.setAll(giftIdeaDAO.getGiftIdeasByPerson(person.getId()));
        giftListView.setItems(giftIdeas);
    }

    @FXML
    private void handleAddGift() {
        Dialog<GiftIdea> dialog = new Dialog<>();
        dialog.setTitle("Add Gift Idea");

        // Set buttons
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Fields
        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Gift Description");

        TextField occasionField = new TextField();
        occasionField.setPromptText("Occasion (e.g. Birthday)");

        VBox content = new VBox(10, new Label("Description:"), descriptionField,
                new Label("Occasion:"), occasionField);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String desc = descriptionField.getText().trim();
                String occ = occasionField.getText().trim();
                if (!desc.isEmpty() && !occ.isEmpty()) {
                    return new GiftIdea(0, editingPerson.getId(), desc, 0.0, occ, false, false);
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(gift -> {
            if (giftIdeaDAO.insertGiftIdea(gift)) {
                giftIdeas.setAll(giftIdeaDAO.getGiftIdeasByPerson(editingPerson.getId()));
            }
        });
    }

    @FXML
    private void handleDeleteGift() {
        GiftIdea selected = giftListView.getSelectionModel().getSelectedItem();
        if (selected != null && giftIdeaDAO.deleteGiftIdea(selected.getId())) {
            giftIdeas.remove(selected);
        }
    }

}