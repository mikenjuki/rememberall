package com.remberall.remberall.test;

import com.remberall.remberall.model.DBConnection;
import com.remberall.remberall.model.GiftIdea;
import com.remberall.remberall.model.GiftIdeaDAO;
import com.remberall.remberall.model.Person;
import com.remberall.remberall.model.PersonDAO;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonDAOTest {

    private PersonDAO personDAO;
    private GiftIdeaDAO giftIdeaDAO;

    // Define test database details
    private static final String TEST_DB_URL = "jdbc:mysql://localhost:3306/remembrall_test";
    private static final String TEST_DB_USER = "root";
    private static final String TEST_DB_PASSWORD = "kwahola";

    @BeforeAll // Runs once before ALL tests in this class
    static void setupTestDatabaseConnection() {
        DBConnection.setTestConnectionDetails(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD);
    }

    @AfterAll
    static void tearDownTestDatabaseConnection() {
        DBConnection.resetToDefaultConnection();
    }

    @BeforeEach // This method runs before EACH test method
    void setup() {
        personDAO = new PersonDAO();
        giftIdeaDAO = new GiftIdeaDAO();
        cleanAndSetupDatabase();
    }

    @AfterEach // This method runs after EACH test method
    void tearDown() {

        cleanAndSetupDatabase();
    }

    // clean and recreate tables, ensuring isolated tests
    private void cleanAndSetupDatabase() {
        try (Connection conn = DBConnection.getConnection(); // Gets connection for the currently set DB
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DROP TABLE IF EXISTS gift_idea");
            stmt.executeUpdate("DROP TABLE IF EXISTS person");

            stmt.executeUpdate("CREATE TABLE person (\n" +
                    "    id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                    "    name VARCHAR(255) NOT NULL,\n" +
                    "    birthday DATE,\n" +
                    "    notes TEXT,\n" +
                    "    interests TEXT,\n" +
                    "    last_meeting_date DATE,\n" +
                    "    relationship_type VARCHAR(50)\n" +
                    ");");
            stmt.executeUpdate("CREATE TABLE gift_idea (\n" +
                    "    id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                    "    person_id INT NOT NULL,\n" +
                    "    description VARCHAR(255) NOT NULL,\n" +
                    "    cost DECIMAL(10, 2),\n" +
                    "    occasion VARCHAR(100),\n" +
                    "    is_bought BOOLEAN,\n" +
                    "    is_delivered BOOLEAN,\n" +
                    "    FOREIGN KEY (person_id) REFERENCES person(id) ON DELETE CASCADE\n" +
                    ");");
        } catch (SQLException e) {
            fail("Failed to clean and setup test database: " + e.getMessage());
        }
    }

    // ... (rest of your test methods remain the same) ...
    @Test
    @Order(1)
    @DisplayName("Test inserting a new person")
    void testInsertPerson() {
        Person p = new Person(0, "Alice Smith", LocalDate.of(1990, 1, 15), "Loves cats",
                List.of("Photography", "Hiking"), LocalDate.of(2023, 10, 20), Person.RelationshipType.FRIEND);

        boolean success = personDAO.insertPerson(p);
        assertTrue(success, "Person should be inserted successfully.");

        // Verify insertion by fetching all persons
        List<Person> allPersons = personDAO.getAllPersons();
        assertFalse(allPersons.isEmpty(), "Persons list should not be empty after insertion.");
        assertEquals(1, allPersons.size(), "Should contain exactly one person.");

        Person insertedPerson = allPersons.get(0);
        assertEquals("Alice Smith", insertedPerson.getName());
        assertEquals(LocalDate.of(1990, 1, 15), insertedPerson.getBirthday());
    }

    @Test
    @Order(2)
    @DisplayName("Test retrieving all persons")
    void testGetAllPersons() {
        // Insert a few persons
        personDAO.insertPerson(new Person(0, "Bob Johnson", LocalDate.of(1985, 3, 10), "Likes tech",
                List.of("Gaming"), LocalDate.of(2024, 1, 1), Person.RelationshipType.COLLEAGUE));
        personDAO.insertPerson(new Person(0, "Charlie Brown", LocalDate.of(1992, 7, 22), "Plays piano",
                List.of("Music"), LocalDate.of(2023, 12, 5), Person.RelationshipType.FAMILY));

        List<Person> persons = personDAO.getAllPersons();
        assertNotNull(persons, "List of persons should not be null.");
        assertEquals(2, persons.size(), "Should retrieve two persons.");

        assertTrue(persons.stream().anyMatch(p -> p.getName().equals("Bob Johnson")), "Should contain Bob Johnson.");
        assertTrue(persons.stream().anyMatch(p -> p.getName().equals("Charlie Brown")), "Should contain Charlie Brown.");
    }

    @Test
    @Order(3)
    @DisplayName("Test updating an existing person")
    void testUpdatePerson() {
        // Insert a person first
        Person originalPerson = new Person(0, "David Lee", LocalDate.of(1975, 11, 30), "Original notes",
                List.of("Reading"), LocalDate.of(2024, 2, 10), Person.RelationshipType.FRIEND);
        personDAO.insertPerson(originalPerson);

        // Retrieve the ID of the inserted person
        Optional<Person> insertedOpt = personDAO.getAllPersons().stream()
                .filter(p -> p.getName().equals("David Lee"))
                .findFirst();
        assertTrue(insertedOpt.isPresent(), "Inserted person should be found for update.");
        Person personToUpdate = insertedOpt.get();

        // Update some fields
        personToUpdate.setName("David Lee Updated");
        personToUpdate.setNotes("Updated notes for David");
        personToUpdate.setRelationshipType(Person.RelationshipType.COLLEAGUE);

        boolean updated = personDAO.updatePerson(personToUpdate);
        assertTrue(updated, "Person should be updated successfully.");

        // Verify the update
        List<Person> personsAfterUpdate = personDAO.getAllPersons();
        assertEquals(1, personsAfterUpdate.size(), "Should still have one person after update.");

        Person retrievedUpdatedPerson = personsAfterUpdate.get(0);
        assertEquals("David Lee Updated", retrievedUpdatedPerson.getName(), "Name should be updated.");
        assertEquals("Updated notes for David", retrievedUpdatedPerson.getNotes(), "Notes should be updated.");
        assertEquals(Person.RelationshipType.COLLEAGUE, retrievedUpdatedPerson.getRelationshipType(), "Relationship type should be updated.");
    }

    @Test
    @Order(4)
    @DisplayName("Test deleting a person and associated gift ideas")
    void testDeletePersonAndAssociatedGifts() {
        // 1. Insert a person
        Person p = new Person(0, "Eve Adams", LocalDate.of(1995, 8, 25), "Loves travel",
                List.of("Travel"), LocalDate.of(2024, 3, 1), Person.RelationshipType.FRIEND);
        personDAO.insertPerson(p);

        // Get the ID of the inserted person
        Optional<Person> insertedOpt = personDAO.getAllPersons().stream()
                .filter(person -> person.getName().equals("Eve Adams"))
                .findFirst();
        assertTrue(insertedOpt.isPresent(), "Inserted person should be found.");
        int personId = insertedOpt.get().getId();

        // 2. Insert gift ideas for this person
        giftIdeaDAO.insertGiftIdea(new GiftIdea(0, personId, "Travel Guidebook", 20.00, "Birthday", false, false));
        giftIdeaDAO.insertGiftIdea(new GiftIdea(0, personId, "Luggage Tag", 5.00, "Christmas", true, false));

        List<GiftIdea> giftsBeforeDelete = giftIdeaDAO.getGiftIdeasByPerson(personId);
        assertEquals(2, giftsBeforeDelete.size(), "Should have 2 gift ideas for Eve.");

        // 3. Delete the person
        boolean deleted = personDAO.deletePerson(personId);
        assertTrue(deleted, "Person should be deleted successfully.");

        // 4. Verify the person is deleted
        List<Person> personsAfterDelete = personDAO.getAllPersons();
        assertTrue(personsAfterDelete.isEmpty(), "No persons should remain after deletion.");

        // 5. Verify associated gift ideas are also deleted
        List<GiftIdea> giftsAfterPersonDelete = giftIdeaDAO.getGiftIdeasByPerson(personId);
        assertTrue(giftsAfterPersonDelete.isEmpty(), "Associated gift ideas should also be deleted.");
    }
}