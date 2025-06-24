package com.remberall.remberall.controller;

import com.remberall.remberall.model.GiftIdea;
import com.remberall.remberall.model.GiftIdeaDAO;
import com.remberall.remberall.model.Person;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;


public class GiftIdeaController {

    private static final Logger LOGGER = Logger.getLogger(GiftIdeaController.class.getName());

    @FXML private ListView<GiftIdea> giftListView;
    private final GiftIdeaDAO giftDAO = new GiftIdeaDAO();
    private final ObservableList<GiftIdea> gifts = FXCollections.observableArrayList();

    // Constructor - register this instance with the ControllerManager
    public GiftIdeaController() {
        ControllerManager.getInstance().registerController(this);
    }

    @FXML
    public void initialize() {
        giftListView.setItems(gifts);
    }

    public void loadGiftsFor(Person person) {
        if (person == null) {
            gifts.clear(); // Clear the list if no person is selected
            return; //
        }
        try {
            List<GiftIdea> personGifts = giftDAO.getGiftIdeasByPerson(person.getId());
            gifts.setAll(personGifts); //
            LOGGER.info("Loaded " + personGifts.size() + " gifts for person: " + person.getName());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading gifts for person: " + person.getName(), e);
            // Optionally show an alert to the user
        }
    }
}