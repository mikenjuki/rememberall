package com.remberall.remberall.controller;

import com.remberall.remberall.model.Person;
import com.remberall.remberall.model.PersonDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PersonController {

    private static final Logger LOGGER = Logger.getLogger(PersonController.class.getName());

    @FXML
    private ListView<Person> personListView;

    private final PersonDAO personDAO = new PersonDAO();
    private final ObservableList<Person> masterPersonList = FXCollections.observableArrayList();
    private FilteredList<Person> filteredPersonList;
    private SortedList<Person> sortedPersonList;

    private ContextMenu cellContextMenu; // Single context menu instance

    // Constructor - register this instance with the ControllerManager
    public PersonController() {
        ControllerManager.getInstance().registerController(this);
    }

    @FXML
    public void initialize() {
        // Initialize the master list from the DAO
        List<Person> persons = personDAO.getAllPersons();
        masterPersonList.setAll(persons);

        // Wrap the master list in a FilteredList and then a SortedList
        filteredPersonList = new FilteredList<>(masterPersonList, p -> true); // Initially show all
        sortedPersonList = new SortedList<>(filteredPersonList);
        // The sortedList's comparator will be set directly by the sort methods.
        // The ListView will automatically reflect changes in the sortedList.

        personListView.setItems(sortedPersonList);

        setupCellFactory();
        setupSelectionListener();
        setupDoubleClickListener();
    }

    private void setupCellFactory() {
        // Create the context menu once
        cellContextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(event -> {
            Person personToDelete = personListView.getSelectionModel().getSelectedItem();
            if (personToDelete != null) {
                confirmAndDeletePerson(personToDelete);
            }
        });
        cellContextMenu.getItems().add(deleteItem);

        personListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Person person, boolean empty) {
                super.updateItem(person, empty);
                if (empty || person == null) {
                    setText(null);
                    setContextMenu(null); // Clear context menu for empty cells
                } else {
                    setText(person.getName() + " - Last met: " + person.getLastMeetingDate());
                    setContextMenu(cellContextMenu); // Assign the single context menu
                }
            }
        });
    }

    private void confirmAndDeletePerson(Person person) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Person: " + person.getName() + "?");
        alert.setContentText("Are you sure you want to delete this person? This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                personDAO.deletePerson(person.getId());
                masterPersonList.remove(person); // Remove from master list, filtered/sorted lists will update automatically
                LOGGER.info("Person " + person.getName() + " deleted successfully.");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error deleting person: " + person.getName(), e);
                showAlert("Error", "Failed to delete person.", "Please try again later.");
            }
        }
    }

    private void setupSelectionListener() {
        personListView.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, selected) -> {
                    GiftIdeaController giftCtrl = ControllerManager.getInstance().getController(GiftIdeaController.class); // Use ControllerManager
                    if (giftCtrl != null) {
                        giftCtrl.loadGiftsFor(selected);
                    }
                });
    }

    private void setupDoubleClickListener() {
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
        String lowerCaseQuery = query.toLowerCase();
        filteredPersonList.setPredicate(person -> {
            if (query == null || query.isEmpty()) {
                return true; // Show all if query is empty
            }
            return person.getName().toLowerCase().contains(lowerCaseQuery);
        });

        if (filteredPersonList.isEmpty() && !query.isEmpty()) {
            // For UI feedback, consider a temporary text label or slight animation on the ListView
            LOGGER.info("No matching persons found for query: " + query);
        }
    }

    public void refreshList() {
        // Fetch fresh data and update the master list
        List<Person> updatedPersons = personDAO.getAllPersons();
        masterPersonList.setAll(updatedPersons);
        // filteredPersonList and sortedPersonList will automatically update
        LOGGER.info("Person list refreshed.");
    }

    private void openEditPersonForm(Person selectedPerson) {
        try {
            // Use ControllerManager to load FXML and get controller
            ControllerManager.FXMLLoaderResult<AddPersonController> result =
                    ControllerManager.getInstance().loadFXMLAndGetController("/view/add-person.fxml");

            Parent root = result.root;
            AddPersonController controller = result.controller;

            controller.setPerson(selectedPerson);
            // Pass a lambda for refresh, better than passing 'this' directly
            controller.setRefreshCallback(this::refreshList);

            Scene scene = new Scene(root, 600, 550);
            scene.getStylesheets().add(getClass().getResource("/view/styles/global.css").toExternalForm());

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle("Edit Person");
            stage.initModality(Modality.APPLICATION_MODAL); // Make it a modal window
            stage.showAndWait(); // Wait for the edit window to close

            // After the edit window closes, the refresh callback in AddPersonController
            // (or here if you prefer) will have handled the refresh.

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error opening edit person form", e);
            showAlert("Error", "Could not open edit form.", "Please check the application logs.");
        }
    }

    // Sorting methods now modify the comparator of the SortedList
    public void sortByName() {
        sortedPersonList.setComparator(Comparator.comparing(Person::getName, String.CASE_INSENSITIVE_ORDER));
    }

    public void sortByBirthday() {
        // Ensure Person::getBirthday returns a Comparable type like LocalDate
        sortedPersonList.setComparator(Comparator.comparing(Person::getBirthday));
    }

    public void sortByLastContacted() {
        // Ensure Person::getLastMeetingDate returns a Comparable type like LocalDate
        sortedPersonList.setComparator(Comparator.comparing(Person::getLastMeetingDate).reversed());
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}