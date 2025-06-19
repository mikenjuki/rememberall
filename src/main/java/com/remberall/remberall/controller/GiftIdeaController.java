package com.remberall.remberall.controller;

import com.remberall.remberall.model.GiftIdea;
import com.remberall.remberall.model.GiftIdeaDAO;
import com.remberall.remberall.model.Person;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.List;

public class GiftIdeaController {

    @FXML private ListView<GiftIdea> giftListView;
    private final GiftIdeaDAO giftDAO = new GiftIdeaDAO();
    private final ObservableList<GiftIdea> gifts = FXCollections.observableArrayList();

    // singleton instance
    private static GiftIdeaController instance;

    public GiftIdeaController() {
        instance = this;
    }

    public static GiftIdeaController getInstance() {
        return instance;
    }

    public void initialize() {
        giftListView.setItems(gifts);
    }

    public void loadGiftsFor(Person person) {
        if (person == null) return;
        List<GiftIdea> gifts = giftDAO.getGiftIdeasByPerson(person.getId());
        giftListView.getItems().setAll(gifts);
    }
}