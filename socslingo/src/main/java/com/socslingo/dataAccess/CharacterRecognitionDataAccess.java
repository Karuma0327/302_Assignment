package com.socslingo.dataAccess;

import com.socslingo.models.CharacterRecognition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CharacterRecognitionDataAccess {

    private static final Logger logger = LoggerFactory.getLogger(CharacterRecognitionDataAccess.class);
    private static final String DATABASE_URL = "jdbc:sqlite:src/main/database/socslingo_database.db";

    public CharacterRecognitionDataAccess() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            logger.error("SQLite JDBC driver not found.", e);
        }
    }

    /**
     * Retrieves all character recognition activities from the database.
     *
     * @return a list of CharacterRecognition objects
     */
    public List<CharacterRecognition> getAllCharacterRecognitions() {
        List<CharacterRecognition> characterRecognitions = new ArrayList<>();

        String sql = "SELECT * FROM character_recognition_activities_table";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int activityId = rs.getInt("activity_id");
                String characterType = rs.getString("character_type");
                String character = rs.getString("character");
                String romaji = rs.getString("romaji");

                CharacterRecognition cr = new CharacterRecognition(activityId, characterType, character, romaji);
                characterRecognitions.add(cr);
            }
        } catch (SQLException e) {
            logger.error("Error fetching character recognitions", e);
        }

        return characterRecognitions;
    }

    /**
     * Retrieves character recognition activities by character type.
     *
     * @param characterType the type of character ("Hiragana", "Katakana", or "Kanji")
     * @return a list of CharacterRecognition objects matching the character type
     */
    public List<CharacterRecognition> getCharacterRecognitionsByType(String characterType) {
        List<CharacterRecognition> characterRecognitions = new ArrayList<>();

        String sql = "SELECT * FROM character_recognition_activities_table WHERE character_type = ?";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, characterType);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int activityId = rs.getInt("activity_id");
                String character = rs.getString("character");
                String romaji = rs.getString("romaji");

                CharacterRecognition cr = new CharacterRecognition(activityId, characterType, character, romaji);
                characterRecognitions.add(cr);
            }
        } catch (SQLException e) {
            logger.error("Error fetching character recognitions by type", e);
        }

        return characterRecognitions;
    }

    /**
     * Retrieves a single character recognition activity by its ID.
     *
     * @param activityId the ID of the activity
     * @return a CharacterRecognition object or null if not found
     */
    public CharacterRecognition getCharacterRecognitionById(int activityId) {
        CharacterRecognition cr = null;

        String sql = "SELECT * FROM character_recognition_activities_table WHERE activity_id = ?";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, activityId);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String characterType = rs.getString("character_type");
                String character = rs.getString("character");
                String romaji = rs.getString("romaji");

                cr = new CharacterRecognition(activityId, characterType, character, romaji);
            }
        } catch (SQLException e) {
            logger.error("Error fetching character recognition by id", e);
        }

        return cr;
    }

    /**
     * Inserts a new character recognition activity into the database.
     *
     * @param characterRecognition the CharacterRecognition object to insert
     * @return true if insertion was successful, false otherwise
     */
    public boolean insertCharacterRecognition(CharacterRecognition characterRecognition) {
        String sql = "INSERT INTO character_recognition_activities_table (character_type, character, romaji) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, characterRecognition.getCharacterType());
            pstmt.setString(2, characterRecognition.getCharacter());
            pstmt.setString(3, characterRecognition.getRomaji());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error inserting character recognition", e);
            return false;
        }
    }

    /**
     * Updates an existing character recognition activity in the database.
     *
     * @param characterRecognition the CharacterRecognition object with updated data
     * @return true if update was successful, false otherwise
     */
    public boolean updateCharacterRecognition(CharacterRecognition characterRecognition) {
        String sql = "UPDATE character_recognition_activities_table SET character_type = ?, character = ?, romaji = ? WHERE activity_id = ?";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, characterRecognition.getCharacterType());
            pstmt.setString(2, characterRecognition.getCharacter());
            pstmt.setString(3, characterRecognition.getRomaji());
            pstmt.setInt(4, characterRecognition.getActivityId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error updating character recognition", e);
            return false;
        }
    }

    /**
     * Deletes a character recognition activity from the database.
     *
     * @param activityId the ID of the activity to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteCharacterRecognition(int activityId) {
        String sql = "DELETE FROM character_recognition_activities_table WHERE activity_id = ?";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, activityId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error deleting character recognition", e);
            return false;
        }
    }
}
