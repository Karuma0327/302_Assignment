package com.socslingo.services;

import com.socslingo.data.SelectedCategory;
import com.socslingo.models.CharacterModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CharacterService {

    private static final String DATABASE_PATH = "src/main/database/socslingo_database.db";
    private static final String DATABASE_URL = "jdbc:sqlite:" + DATABASE_PATH;

    // Existing method
    public static List<CharacterModel> getCharactersByType(String characterType) {
        List<CharacterModel> characterList = new ArrayList<>();

        String query = "SELECT character_type, character, romaji FROM character_recognition_activities_table WHERE character_type = ?";

        try {
            Class.forName("org.sqlite.JDBC");

            try (Connection connection = DriverManager.getConnection(DATABASE_URL);
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, characterType);

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String type = resultSet.getString("character_type");
                    String character = resultSet.getString("character");
                    String romaji = resultSet.getString("romaji");

                    characterList.add(new CharacterModel(type, character, romaji));
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("SQL error during character retrieval: " + e.getMessage());
            e.printStackTrace();
        }

        return characterList;
    }

    // **New Method:** Retrieve romaji for a specific character
    public static String getRomajiForCharacter(String character) {
        String romaji = null;
        String query = "SELECT romaji FROM character_recognition_activities_table WHERE character = ?";

        try {
            Class.forName("org.sqlite.JDBC");

            try (Connection connection = DriverManager.getConnection(DATABASE_URL);
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, character);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    romaji = resultSet.getString("romaji");
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("SQL error during romaji retrieval: " + e.getMessage());
            e.printStackTrace();
        }

        return romaji;
    }

    // **New Method:** Retrieve random romaji options excluding the correct one
    public static List<String> getRandomRomaji(int count, String excludeRomaji) {
        List<String> romajiList = new ArrayList<>();
        String query = "SELECT romaji FROM character_recognition_activities_table WHERE romaji != ? ORDER BY RANDOM() LIMIT ?";

        try {
            Class.forName("org.sqlite.JDBC");

            try (Connection connection = DriverManager.getConnection(DATABASE_URL);
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, excludeRomaji);
                preparedStatement.setInt(2, count);

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    romajiList.add(resultSet.getString("romaji"));
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("SQL error during random romaji retrieval: " + e.getMessage());
            e.printStackTrace();
        }

        return romajiList;
    }

    
    // **New Method:** Retrieve a random character from the database
    public static CharacterModel getRandomCharacter() {
        CharacterModel character = null;
        String query = "SELECT character_type, character, romaji FROM character_recognition_activities_table ORDER BY RANDOM() LIMIT 1";

        try {
            Class.forName("org.sqlite.JDBC");

            try (Connection connection = DriverManager.getConnection(DATABASE_URL);
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                if (resultSet.next()) {
                    String type = resultSet.getString("character_type");
                    String characterChar = resultSet.getString("character");
                    String romaji = resultSet.getString("romaji");
                    character = new CharacterModel(type, characterChar, romaji);
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("SQL error during random character retrieval: " + e.getMessage());
            e.printStackTrace();
        }

        return character;
    }

        public static CharacterModel getRandomCharacterByCategory(SelectedCategory.Category category) {
        String query = "SELECT character_type, character, romaji FROM character_recognition_activities_table WHERE character_type LIKE ? ORDER BY RANDOM() LIMIT 1";

        String categoryPattern = "";
        switch (category) {
            case HIRAGANA:
                categoryPattern = "Hiragana%";
                break;
            case KATAKANA:
                categoryPattern = "Katakana%";
                break;
            case USER:
                categoryPattern = "User%"; // Assuming user-defined categories start with 'User'
                break;
            default:
                categoryPattern = "%"; // Default to any category
                break;
        }

        try {
            Class.forName("org.sqlite.JDBC");

            try (Connection connection = DriverManager.getConnection(DATABASE_URL);
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, categoryPattern);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    String type = resultSet.getString("character_type");
                    String character = resultSet.getString("character");
                    String romaji = resultSet.getString("romaji");

                    return new CharacterModel(type, character, romaji);
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("SQL error during random character retrieval by category: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Retrieves the romaji for a given character based on the selected category.
     *
     * @param character The Japanese character.
     * @param category The selected category.
     * @return The corresponding romaji, or null if not found.
     */
    public static String getRomajiForCharacter(String character, SelectedCategory.Category category) {
        String query = "SELECT romaji FROM character_recognition_activities_table WHERE character = ? AND character_type LIKE ?";

        String categoryPattern = "";
        switch (category) {
            case HIRAGANA:
                categoryPattern = "Hiragana%";
                break;
            case KATAKANA:
                categoryPattern = "Katakana%";
                break;
            case USER:
                categoryPattern = "User%";
                break;
            default:
                categoryPattern = "%";
                break;
        }

        try {
            Class.forName("org.sqlite.JDBC");

            try (Connection connection = DriverManager.getConnection(DATABASE_URL);
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, character);
                preparedStatement.setString(2, categoryPattern);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    return resultSet.getString("romaji");
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("SQL error during romaji retrieval by character and category: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Retrieves a list of random romaji options excluding the correct one, based on the selected category.
     *
     * @param count The number of random options to retrieve.
     * @param excludeRomaji The romaji to exclude from the options.
     * @param category The selected category.
     * @return A list of random romaji options.
     */
    public static List<String> getRandomRomaji(int count, String excludeRomaji, SelectedCategory.Category category) {
        List<String> romajiList = new ArrayList<>();
        String query = "SELECT romaji FROM character_recognition_activities_table WHERE romaji != ? AND character_type LIKE ? ORDER BY RANDOM() LIMIT ?";

        String categoryPattern = "";
        switch (category) {
            case HIRAGANA:
                categoryPattern = "Hiragana%";
                break;
            case KATAKANA:
                categoryPattern = "Katakana%";
                break;
            case USER:
                categoryPattern = "User%"; // Assuming user-defined categories start with 'User'
                break;
            default:
                categoryPattern = "%";
                break;
        }

        try {
            Class.forName("org.sqlite.JDBC");

            try (Connection connection = DriverManager.getConnection(DATABASE_URL);
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, excludeRomaji);
                preparedStatement.setString(2, categoryPattern);
                preparedStatement.setInt(3, count);

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    romajiList.add(resultSet.getString("romaji"));
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("SQL error during random romaji retrieval: " + e.getMessage());
            e.printStackTrace();
        }

        return romajiList;
    }

}