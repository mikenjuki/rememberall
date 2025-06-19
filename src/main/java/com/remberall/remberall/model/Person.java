package com.remberall.remberall.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Person implements Comparable<Person> {
    private int id;
    private String name;
    private LocalDate birthday;
    private String notes;
    private List<String> interests;
    private LocalDate lastMeetingDate;
    private RelationshipType relationshipType;

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

    public String getName() {
        return name;
    }

    public String getNotes() {
        return notes;
    }

    public LocalDate getLastMeetingDate() {
        return lastMeetingDate;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public List<String> getInterests() {
        return interests;
    }

    public Person.RelationshipType getRelationshipType() {
        return relationshipType;
    }

    public int getId() {
        return id;
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


    @Override
    public int compareTo(Person other) {
        return this.name.compareToIgnoreCase(other.name); //default
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return name.equalsIgnoreCase(person.name) && birthday.equals(person.birthday);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase(), birthday);
    }

    @Override
    public String toString() {
        return name + " (" + relationshipType + ")";
    }
}