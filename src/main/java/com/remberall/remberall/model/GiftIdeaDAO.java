package com.remberall.remberall.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GiftIdeaDAO {
    private static final Logger LOGGER = Logger.getLogger(GiftIdeaDAO.class.getName());

    // fetch all gifts for a specific person
    public List<GiftIdea> getGiftIdeasByPerson(int personId) {
        List<GiftIdea> gifts = new ArrayList<>();
        String query = "SELECT * FROM gift_idea WHERE person_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, personId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String description = rs.getString("description");
                double cost = rs.getDouble("cost");
                String occasion = rs.getString("occasion");
                boolean isBought = rs.getBoolean("is_bought");
                boolean isDelivered = rs.getBoolean("is_delivered");

                GiftIdea gift = new GiftIdea(id, personId, description, cost, occasion, isBought, isDelivered);
                gifts.add(gift);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Error fetching gift ideas for person ID: " + personId, e);
        }

        return gifts;
    }

    // insert a new gift idea
    public boolean insertGiftIdea(GiftIdea gift) {
        String query = "INSERT INTO gift_idea (person_id, description, cost, occasion, is_bought, is_delivered) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, gift.getPersonId());
            stmt.setString(2, gift.getDescription());
            stmt.setDouble(3, gift.getCost());
            stmt.setString(4, gift.getOccasion());
            stmt.setBoolean(5, gift.isBought());
            stmt.setBoolean(6, gift.isDelivered());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update an existing gift idea
    public boolean updateGiftIdea(GiftIdea gift) {
        String query = "UPDATE gift_idea SET description = ?, cost = ?, occasion = ?, is_bought = ?, is_delivered = ? " +
                "WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, gift.getDescription());
            stmt.setDouble(2, gift.getCost());
            stmt.setString(3, gift.getOccasion());
            stmt.setBoolean(4, gift.isBought());
            stmt.setBoolean(5, gift.isDelivered());
            stmt.setInt(6, gift.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete a gift idea by ID
    public boolean deleteGiftIdea(int giftId) {
        String query = "DELETE FROM gift_idea WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, giftId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes all gift ideas associated with a specific person.
     * @param personId The ID of the person whose gift ideas are to be deleted.
     * @return true if the deletion was successful, false otherwise.
     */
    public boolean deleteGiftIdeasByPerson(int personId) {
        String query = "DELETE FROM gift_idea WHERE person_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, personId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}