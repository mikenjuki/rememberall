package com.remberall.remberall.model;

import java.time.LocalDate;
import java.util.List;

public class Person implements Comparable<Person> {
    private int id;
    private String name;
    private LocalDate birthday;
    private String notes;
    private List<String> interests;
    private LocalDate lastMeetingDate;
    private RelationshipType relationshipType;

    // enum for relationship type
    public enum RelationshipType {
        FAMILY, FRIEND, COLLEAGUE, OTHER, UNKNOWN
    }

    public Person(int id, String name, LocalDate birthday, String notes, List<String> interests,
                  LocalDate lastMeetingDate, RelationshipType relationshipType) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
        this.notes = notes;
        this.interests = interests;
        this.lastMeetingDate = lastMeetingDate;
        this.relationshipType = relationshipType;
    }

    // getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public String getNotes() {
        return notes;
    }

    public List<String> getInterests() {
        return interests;
    }

    public LocalDate getLastMeetingDate() {
        return lastMeetingDate;
    }

    public RelationshipType getRelationshipType() {
        return relationshipType;
    }

    // setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public void setLastMeetingDate(LocalDate lastMeetingDate) {
        this.lastMeetingDate = lastMeetingDate;
    }

    public void setRelationshipType(RelationshipType relationshipType) {
        this.relationshipType = relationshipType;
    }

    // comparable to sort by birthday
    @Override
    public int compareTo(Person other) {
        return this.birthday.compareTo(other.birthday);
    }
@Override
    public String toString() {
        return name + " (" + relationshipType + ")";
    }
}

//List<Person> persons = personDAO.getAllPersons();
//
//// Sort by name:
//persons.sort(Comparator.comparing(Person::getName));
//
//// Sort by recent contact:
//        persons.sort(Comparator.comparing(Person::getLastMeetingDate).reversed());
//
//// Sort by old contact:
//        persons.sort(Comparator.comparing(Person::getLastMeetingDate));
//
//// Sort by birthday (default Comparable):
//        Collections.sort(persons);  // uses compareTo