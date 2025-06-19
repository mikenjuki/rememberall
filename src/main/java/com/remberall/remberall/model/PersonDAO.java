package com.remberall.remberall.model;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PersonDAO {

    // fetch all persons from DB
    public List<Person> getAllPersons() {
        List<Person> persons = new ArrayList<>();
        String query = "SELECT * FROM person";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                LocalDate birthday = rs.getDate("birthday") != null ? rs.getDate("birthday").toLocalDate() : null;
                String notes = rs.getString("notes");
                String interestsString = rs.getString("interests");
                List<String> interests = interestsString != null ? Arrays.asList(interestsString.split(",")) : new ArrayList<>();
                LocalDate lastMeetingDate = rs.getDate("last_meeting_date") != null ? rs.getDate("last_meeting_date").toLocalDate() : null;
                String relTypeStr = rs.getString("relationship_type");
                Person.RelationshipType relationshipType;
                try {
                    relationshipType = relTypeStr != null
                            ? Person.RelationshipType.valueOf(relTypeStr.toUpperCase())
                            : Person.RelationshipType.UNKNOWN;
                } catch (IllegalArgumentException e) {
                    relationshipType = Person.RelationshipType.UNKNOWN;
                }

                Person person = new Person(id, name, birthday, notes, interests, lastMeetingDate, relationshipType);
                persons.add(person);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return persons;
    }

    // insert new person into DB
    public boolean insertPerson(Person person) {
        String query = "INSERT INTO person (name, birthday, notes, interests, last_meeting_date, relationship_type) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, person.getName());
            pstmt.setDate(2, person.getBirthday() != null ? Date.valueOf(person.getBirthday()) : null);
            pstmt.setString(3, person.getNotes());
            pstmt.setString(4, String.join(",", person.getInterests()));
            pstmt.setDate(5, person.getLastMeetingDate() != null ? Date.valueOf(person.getLastMeetingDate()) : null);
            pstmt.setString(6, person.getRelationshipType().name());

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // update existing person
    public boolean updatePerson(Person person) {
        String query = "UPDATE person SET name = ?, birthday = ?, notes = ?, interests = ?, last_meeting_date = ?, relationship_type = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, person.getName());
            pstmt.setDate(2, person.getBirthday() != null ? Date.valueOf(person.getBirthday()) : null);
            pstmt.setString(3, person.getNotes());
            pstmt.setString(4, String.join(",", person.getInterests()));
            pstmt.setDate(5, person.getLastMeetingDate() != null ? Date.valueOf(person.getLastMeetingDate()) : null);
            pstmt.setString(6, person.getRelationshipType().name());
            pstmt.setInt(7, person.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // delete person from db
    public boolean deletePerson(int personId) {
        String query = "DELETE FROM person WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, personId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}