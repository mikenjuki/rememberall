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
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.Runnable;

public class AddPersonController {

    private static final Logger LOGGER = Logger.getLogger(AddPersonController.class.getName());

    @FXML private TextField nameField;
    @FXML private DatePicker birthdayPicker;
    @FXML private TextArea notesArea;
    @FXML private TextField interestsField;
    @FXML private DatePicker lastMeetingDatePicker;
    @FXML private ComboBox<Person.RelationshipType> relationshipBox;
    @FXML private Button saveButton;
    @FXML private ListView<GiftIdea> giftListView;

    private final PersonDAO personDAO = new PersonDAO();
    private final GiftIdeaDAO giftIdeaDAO = new GiftIdeaDAO();
    private Person editingPerson; 
    private ObservableList<GiftIdea> giftIdeas = FXCollections.observableArrayList();

     //callback to notify the calling controller to refresh its list
    private Runnable refreshCallback;

    @FXML
    public void initialize() {
        relationshipBox.getItems().setAll(Person.RelationshipType.values());
        saveButton.setOnAction(e -> handleSave());

        giftListView.setItems(giftIdeas);  //Set the items for the ListView

        giftListView.setCellFactory(param -> new ListCell<GiftIdea>() {
            @Override
            protected void updateItem(GiftIdea item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());  //Uses GiftIdea's toString method
                }
            }
        });

        giftListView.setOnMouseClicked(event -> { 
            if (event.getClickCount() == 2) {
                GiftIdea selectedGift = giftListView.getSelectionModel().getSelectedItem(); 
                if (selectedGift != null) {
                    editGiftIdea(selectedGift);
                }
            }
        });
    }

    public void setRefreshCallback(Runnable callback) { 
        this.refreshCallback = callback; 
    }

    private void editGiftIdea(GiftIdea selectedGift) {
        TextInputDialog dialog = new TextInputDialog(selectedGift.getDescription()); 
        dialog.setTitle("Edit Gift"); 
        dialog.setHeaderText("Edit Gift Description"); 
        dialog.setContentText("Description:"); 

        dialog.showAndWait().ifPresent(newDesc -> { 
            if (!newDesc.trim().isEmpty()) {
                selectedGift.setDescription(newDesc.trim());
                if (giftIdeaDAO.updateGiftIdea(selectedGift)) {  //Update in DB
                    giftListView.refresh();  //Refresh the ListView to show changes
                    LOGGER.info("Gift idea updated: " + selectedGift.getDescription());
                } else {
                    showAlert(Alert.AlertType.ERROR, "Update Failed", "Could not update gift idea in database.");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Invalid Input", "Gift description cannot be empty.");
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

            showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields must be filled out."); 
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

            if (personDAO.updatePerson(editingPerson)) { 
                LOGGER.info("Person updated: " + editingPerson.getName());
            } else {
                showAlert(Alert.AlertType.ERROR, "Save Failed", "Could not update person in database.");
            }
        } else {
            Person newPerson = new Person(0, name, birthday, notes, interests, lastMeetingDate, relType); 
            if (personDAO.insertPerson(newPerson)) { 
                LOGGER.info("New person added: " + newPerson.getName());
            } else {
                showAlert(Alert.AlertType.ERROR, "Save Failed", "Could not insert new person into database.");
            }
        }

        if (refreshCallback != null) {
            refreshCallback.run();
        }
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
        if (person != null) {
            nameField.setText(person.getName()); 
            birthdayPicker.setValue(person.getBirthday()); 
            notesArea.setText(person.getNotes()); 
            interestsField.setText(String.join(",", person.getInterests())); 
            lastMeetingDatePicker.setValue(person.getLastMeetingDate()); 
            relationshipBox.setValue(person.getRelationshipType()); 
             //Load gift ideas for the editing person
            giftIdeas.setAll(giftIdeaDAO.getGiftIdeasByPerson(person.getId())); 
        }
    }

    @FXML
    private void handleAddGift() { 
        if (editingPerson == null) {
            showAlert(Alert.AlertType.WARNING, "No Person Selected", "Please select or save a person before adding gift ideas.");
            return;
        }

        Dialog<GiftIdea> dialog = new Dialog<>(); 
        dialog.setTitle("Add Gift Idea"); 

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE); 
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL); 

        TextField descriptionField = new TextField(); 
        descriptionField.setPromptText("Gift Description"); 
        TextField occasionField = new TextField(); 
        occasionField.setPromptText("Occasion (e.g. Birthday)"); 

        VBox content = new VBox(10, new Label("Description:"), descriptionField,
                new Label("Occasion:"), occasionField); 
        dialog.getDialogPane().setContent(content); 

         //Enable/Disable Add button depending on whether a description is entered.
        Button addButton = (Button) dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);
        descriptionField.textProperty().addListener((obs, oldV, newV) -> {
            addButton.setDisable(newV.trim().isEmpty() || occasionField.getText().trim().isEmpty());
        });
        occasionField.textProperty().addListener((obs, oldV, newV) -> {
            addButton.setDisable(newV.trim().isEmpty() || descriptionField.getText().trim().isEmpty());
        });


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
                giftIdeas.setAll(giftIdeaDAO.getGiftIdeasByPerson(editingPerson.getId()));  //Refresh list view with
                // updated gifts
                LOGGER.info("Gift idea added: " + gift.getDescription());
            } else {
                showAlert(Alert.AlertType.ERROR, "Add Failed", "Could not add gift idea to database.");
            }
        });
    }

    @FXML
    private void handleDeleteGift() { 
        GiftIdea selected = giftListView.getSelectionModel().getSelectedItem(); 
        if (selected != null) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Deletion");
            confirmationAlert.setHeaderText("Delete Gift Idea: " + selected.getDescription() + "?");
            confirmationAlert.setContentText("Are you sure you want to delete this gift idea?");

            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (giftIdeaDAO.deleteGiftIdea(selected.getId())) { 
                    giftIdeas.remove(selected); 
                    LOGGER.info("Gift idea deleted: " + selected.getDescription());
                } else {
                    showAlert(Alert.AlertType.ERROR, "Delete Failed", "Could not delete gift idea from database.");
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a gift idea to delete.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}