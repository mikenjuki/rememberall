package com.remberall.remberall.model;

public class GiftIdea {
    private int id;
    private int personId;//FK to person
    private String description;
    private double cost;
    private String occasion;
    private boolean	isBought;
    private boolean	isDelivered;

    public GiftIdea(int id, int personId, String description, double cost, String occasion, boolean isBought, boolean isDelivered) {
        this.id = id;
        this.personId = personId;
        this.description = description;
        this.cost = cost;
        this.occasion = occasion;
        this.isBought = isBought;
        this.isDelivered = isDelivered;
    }

    //getters & setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getOccasion() {
        return occasion;
    }

    public void setOccasion(String occasion) {
        this.occasion = occasion;
    }

    public boolean isBought() {
        return isBought;
    }

    public void setBought(boolean bought) {
        isBought = bought;
    }

    public boolean isDelivered() {
        return isDelivered;
    }

    public void setDelivered(boolean delivered) {
        isDelivered = delivered;
    }

    @Override
    public String toString() {
        return description + " for " + occasion;
    }

}