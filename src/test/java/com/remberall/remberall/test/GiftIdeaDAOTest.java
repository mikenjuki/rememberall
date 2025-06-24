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
public class GiftIdeaDAOTest {

    private GiftIdeaDAO giftIdeaDAO;
    private PersonDAO personDAO;
    private int testPersonId;

    // Define test database details
    private static final String TEST_DB_URL = "jdbc:mysql://localhost:3306/remembrall_test";
    private static final String TEST_DB_USER = "root";
    private static final String TEST_DB_PASSWORD = "kwahola"; // Your MySQL password

    @BeforeAll // Runs once before ALL tests in this class
    static void setupTestDatabaseConnection() {
        DBConnection.setTestConnectionDetails(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD);
    }

    @AfterAll // Runs once after ALL tests in this class
    static void tearDownTestDatabaseConnection() {
        DBConnection.resetToDefaultConnection(); // Reset to default DB connection after all tests
    }

    @BeforeEach // Runs before each test method
    void setup() {
        giftIdeaDAO = new GiftIdeaDAO();
        personDAO = new PersonDAO();
        cleanAndSetupDatabase();
        insertTestPerson();
    }

    @AfterEach
    void tearDown() {
        cleanAndSetupDatabase();
    }

    // --- Helper Methods for Database Management ---
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

    private void insertTestPerson() {
        Person testPerson = new Person(0, "Test Person for Gifts", LocalDate.of(1990, 5, 15),
                "Notes for gift person", List.of("Gardening"), LocalDate.now(), Person.RelationshipType.FRIEND);
        boolean success = personDAO.insertPerson(testPerson);
        assertTrue(success, "Should be able to insert a test person.");

        Optional<Person> insertedOpt = personDAO.getAllPersons().stream()
                .filter(p -> p.getName().equals("Test Person for Gifts"))
                .findFirst();
        testPersonId = insertedOpt.orElseThrow(() -> new AssertionError("Test person not found after insertion.")).getId();
    }

    // ... (rest of your test methods remain the same) ...
    @Test
    @Order(1)
    @DisplayName("Test inserting a new gift idea")
    void testInsertGiftIdea() {
        GiftIdea gift1 = new GiftIdea(0, testPersonId, "Book: 'The Hitchhiker's Guide'", 25.50, "Birthday", false, false);
        boolean inserted = giftIdeaDAO.insertGiftIdea(gift1);
        assertTrue(inserted, "Gift idea should be inserted successfully.");

        List<GiftIdea> gifts = giftIdeaDAO.getGiftIdeasByPerson(testPersonId);
        assertFalse(gifts.isEmpty(), "Should retrieve at least one gift idea.");
        assertEquals(1, gifts.size(), "Should have exactly one gift idea.");
        assertEquals("Book: 'The Hitchhiker's Guide'", gifts.get(0).getDescription(), "Retrieved gift description should match.");
    }

    @Test
    @Order(2)
    @DisplayName("Test getting gift ideas by person ID")
    void testGetGiftIdeasByPerson() {
        // Insert multiple gifts for the test person
        giftIdeaDAO.insertGiftIdea(new GiftIdea(0, testPersonId, "Gardening Gloves", 15.00, "Christmas", true, false));
        giftIdeaDAO.insertGiftIdea(new GiftIdea(0, testPersonId, "Coffee Mug", 10.00, "Anniversary", false, true));

        List<GiftIdea> gifts = giftIdeaDAO.getGiftIdeasByPerson(testPersonId);
        assertNotNull(gifts, "List of gifts should not be null.");
        assertEquals(2, gifts.size(), "Should retrieve two gift ideas for the test person.");

        // Check content
        assertTrue(gifts.stream().anyMatch(g -> g.getDescription().equals("Gardening Gloves")), "Should contain 'Gardening Gloves'.");
        assertTrue(gifts.stream().anyMatch(g -> g.getDescription().equals("Coffee Mug")), "Should contain 'Coffee Mug'.");
    }

    @Test
    @Order(3)
    @DisplayName("Test updating an existing gift idea")
    void testUpdateGiftIdea() {
        // First, insert a gift to update
        giftIdeaDAO.insertGiftIdea(new GiftIdea(0, testPersonId, "Old Description", 5.00, "Old Occasion", false, false));
        List<GiftIdea> initialGifts = giftIdeaDAO.getGiftIdeasByPerson(testPersonId);
        assertFalse(initialGifts.isEmpty(), "Initial gift should be present.");

        GiftIdea giftToUpdate = initialGifts.get(0);
        giftToUpdate.setDescription("New Updated Description");
        giftToUpdate.setCost(30.00);
        giftToUpdate.setBought(true);
        giftToUpdate.setOccasion("New Occasion");

        boolean updated = giftIdeaDAO.updateGiftIdea(giftToUpdate);
        assertTrue(updated, "Gift idea should be updated successfully.");

        List<GiftIdea> updatedGifts = giftIdeaDAO.getGiftIdeasByPerson(testPersonId);
        assertEquals(1, updatedGifts.size(), "Should still have one gift after update.");
        GiftIdea retrievedGift = updatedGifts.get(0);
        assertEquals("New Updated Description", retrievedGift.getDescription(), "Description should be updated.");
        assertEquals(30.00, retrievedGift.getCost(), 0.01, "Cost should be updated.");
        assertTrue(retrievedGift.isBought(), "isBought should be updated.");
        assertEquals("New Occasion", retrievedGift.getOccasion(), "Occasion should be updated.");
    }

    @Test
    @Order(4)
    @DisplayName("Test deleting a single gift idea")
    void testDeleteGiftIdea() {
        // Insert two gifts, delete one
        giftIdeaDAO.insertGiftIdea(new GiftIdea(0, testPersonId, "Gift to Delete", 10.00, "Occasion1", false, false));
        giftIdeaDAO.insertGiftIdea(new GiftIdea(0, testPersonId, "Gift to Keep", 20.00, "Occasion2", false, false));

        List<GiftIdea> giftsBeforeDelete = giftIdeaDAO.getGiftIdeasByPerson(testPersonId);
        assertEquals(2, giftsBeforeDelete.size(), "Should have two gifts initially.");

        GiftIdea giftToDelete = giftsBeforeDelete.stream()
                .filter(g -> g.getDescription().equals("Gift to Delete"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Gift to delete not found."));

        boolean deleted = giftIdeaDAO.deleteGiftIdea(giftToDelete.getId());
        assertTrue(deleted, "Gift idea should be deleted successfully.");

        List<GiftIdea> giftsAfterDelete = giftIdeaDAO.getGiftIdeasByPerson(testPersonId);
        assertEquals(1, giftsAfterDelete.size(), "Should have one gift after deletion.");
        assertEquals("Gift to Keep", giftsAfterDelete.get(0).getDescription(), "The correct gift should remain.");
    }

    @Test
    @Order(5)
    @DisplayName("Test deleting all gift ideas by person ID")
    void testDeleteGiftIdeasByPerson() {
        // Insert multiple gifts for the test person
        giftIdeaDAO.insertGiftIdea(new GiftIdea(0, testPersonId, "Item A", 10.00, "Occasion A", false, false));
        giftIdeaDAO.insertGiftIdea(new GiftIdea(0, testPersonId, "Item B", 20.00, "Occasion B", false, false));
        giftIdeaDAO.insertGiftIdea(new GiftIdea(0, testPersonId, "Item C", 30.00, "Occasion C", false, false));

        List<GiftIdea> giftsBeforeDelete = giftIdeaDAO.getGiftIdeasByPerson(testPersonId);
        assertEquals(3, giftsBeforeDelete.size(), "Should have 3 gifts initially.");

        boolean deletedAll = giftIdeaDAO.deleteGiftIdeasByPerson(testPersonId);
        assertTrue(deletedAll, "All gift ideas for the person should be deleted successfully.");

        List<GiftIdea> giftsAfterDelete = giftIdeaDAO.getGiftIdeasByPerson(testPersonId);
        assertTrue(giftsAfterDelete.isEmpty(), "No gift ideas should remain for the person.");
    }
}