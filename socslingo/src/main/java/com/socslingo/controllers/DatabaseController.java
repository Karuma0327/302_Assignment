package com.socslingo.controllers;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseController {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseController.class);

    public static void createDatabase() {
        String folder_path = "src/main/database";
        String database_path = folder_path + "/socslingo_database.db";

        File folder = new File(folder_path);
        if (!folder.exists()) {
            folder.mkdirs();
            logger.info("Database folder created at: " + folder_path);
        }

        File database_file = new File(database_path);
        if (database_file.exists()) {
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.print("Database already exists. Do you want to overwrite it? (Y/N): ");
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("y")) {
                    if (database_file.delete()) {
                        logger.info("Existing database deleted.");
                    } else {
                        logger.error("Failed to delete the existing database.");
                        return;
                    }
                } else {
                    logger.info("Operation cancelled.");
                    return;
                }
            }
        }

        String database_url = "jdbc:sqlite:" + database_path;

        // Existing Tables
        String user_table_sql = "CREATE TABLE IF NOT EXISTS user_table (\n"
                + " user_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " username TEXT NOT NULL UNIQUE,\n"
                + " email TEXT NOT NULL UNIQUE,\n"
                + " password TEXT NOT NULL,\n"
                + " created_date TEXT NOT NULL,\n"
                + " profile_banner_path TEXT,\n"  // Added profile_banner_path column
                + " pet_id INTEGER,\n"
                + " FOREIGN KEY (pet_id) REFERENCES pet_table(pet_id)\n"
                + ");";

        String pet_table_sql = "CREATE TABLE IF NOT EXISTS pet_table (\n"
                + " pet_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " pet_name TEXT NOT NULL,\n"
                + " hunger_level INTEGER DEFAULT 0,\n"
                + " happiness_level INTEGER DEFAULT 0,\n"
                + " health_level INTEGER DEFAULT 0,\n"
                + " pet_tier INTEGER DEFAULT 1,\n"
                + " pet_experience INTEGER DEFAULT 0\n"
                + ");";

        String flashcards_table_sql = "CREATE TABLE IF NOT EXISTS flashcards_table (\n"
                + " flashcard_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " user_id INTEGER,\n"
                + " front_text TEXT NOT NULL,\n"
                + " back_text TEXT NOT NULL,\n"
                + " created_date TEXT NOT NULL,\n"
                + " FOREIGN KEY (user_id) REFERENCES user_table(user_id)\n"
                + ");";

        String flashcard_decks_table_sql = "CREATE TABLE IF NOT EXISTS flashcard_decks_table (\n"
                + " deck_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " user_id INTEGER NOT NULL,\n"
                + " deck_name TEXT NOT NULL,\n"
                + " created_date TEXT NOT NULL,\n"
                + " FOREIGN KEY (user_id) REFERENCES user_table(user_id)\n"
                + ");";

        String deck_flashcards_table_sql = "CREATE TABLE IF NOT EXISTS deck_flashcards_table (\n"
                + " deck_id INTEGER NOT NULL,\n"
                + " flashcard_id INTEGER NOT NULL,\n"
                + " PRIMARY KEY (deck_id, flashcard_id),\n"
                + " FOREIGN KEY (deck_id) REFERENCES flashcard_decks_table(deck_id) ON DELETE CASCADE,\n"
                + " FOREIGN KEY (flashcard_id) REFERENCES flashcards_table(flashcard_id) ON DELETE CASCADE\n"
                + ");";

        // New Statistics Tables

        // 1. Conversation Statistics
        String conversation_statistics_table_sql = "CREATE TABLE IF NOT EXISTS conversation_statistics (\n"
                + " stat_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " user_id INTEGER NOT NULL,\n"
                + " conversation_attempts INTEGER DEFAULT 0,\n"
                + " conversation_correct INTEGER DEFAULT 0,\n"
                + " conversation_incorrect INTEGER DEFAULT 0,\n"
                + " conversation_accuracy DECIMAL(5,2) DEFAULT 0.00,\n"
                + " last_updated TEXT DEFAULT CURRENT_TIMESTAMP,\n"
                + " FOREIGN KEY (user_id) REFERENCES user_table(user_id) ON DELETE CASCADE\n"
                + ");";

        // 2. Flashcard Statistics
        String flashcard_statistics_table_sql = "CREATE TABLE IF NOT EXISTS flashcard_statistics (\n"
                + " stat_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " user_id INTEGER NOT NULL,\n"
                + " flashcards_attempted INTEGER DEFAULT 0,\n"
                + " flashcards_correct INTEGER DEFAULT 0,\n"
                + " flashcards_incorrect INTEGER DEFAULT 0,\n"
                + " flashcards_accuracy DECIMAL(5,2) DEFAULT 0.00,\n"
                + " last_updated TEXT DEFAULT CURRENT_TIMESTAMP,\n"
                + " FOREIGN KEY (user_id) REFERENCES user_table(user_id) ON DELETE CASCADE\n"
                + ");";

        // 3. Deck Statistics
        String deck_statistics_table_sql = "CREATE TABLE IF NOT EXISTS deck_statistics (\n"
                + " stat_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " user_id INTEGER NOT NULL,\n"
                + " deck_attempted INTEGER DEFAULT 0,\n"
                + " deck_completed INTEGER DEFAULT 0,\n"
                + " deck_accuracy DECIMAL(5,2) DEFAULT 0.00,\n"
                + " last_updated TEXT DEFAULT CURRENT_TIMESTAMP,\n"
                + " FOREIGN KEY (user_id) REFERENCES user_table(user_id) ON DELETE CASCADE\n"
                + ");";

        // 4. Character Recognition Statistics
        String character_recognition_statistics_table_sql = "CREATE TABLE IF NOT EXISTS character_recognition_statistics (\n"
                + " stat_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " user_id INTEGER NOT NULL,\n"
                + " characters_correct INTEGER DEFAULT 0,\n"
                + " characters_incorrect INTEGER DEFAULT 0,\n"
                + " characters_accuracy DECIMAL(5,2) DEFAULT 0.00,\n"
                + " last_updated TEXT DEFAULT CURRENT_TIMESTAMP,\n"
                + " FOREIGN KEY (user_id) REFERENCES user_table(user_id) ON DELETE CASCADE\n"
                + ");";

        // 5. Conversations Table (New)
        String conversations_table_sql = "CREATE TABLE IF NOT EXISTS conversations_table (\n"
                + " conversation_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " sentence TEXT NOT NULL,\n"
                + " option_one TEXT NOT NULL,\n"
                + " option_two TEXT NOT NULL,\n"
                + " option_three TEXT NOT NULL,\n"
                + " option_four TEXT NOT NULL,\n"
                + " correct_option INTEGER NOT NULL CHECK (correct_option BETWEEN 1 AND 4),\n"
                + " created_date TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP\n"
                + ");";

        // 6. Character Recognition Activities Table (New)
        String character_recognition_activities_table_sql = "CREATE TABLE IF NOT EXISTS character_recognition_activities_table (\n"
                + " activity_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " character_type TEXT NOT NULL CHECK (character_type IN ('Hiragana', 'Katakana', 'Kanji')),\n"
                + " character TEXT NOT NULL,\n"
                + " romaji TEXT NOT NULL,\n"
                + " created_date TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP\n"
                + ");";

        // 7. Word Recognition Activities Table (New)
        String word_recognition_activities_table_sql = "CREATE TABLE IF NOT EXISTS word_recognition_activities_table (\n"
                + " activity_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " word_type TEXT NOT NULL CHECK (word_type IN ('Noun', 'Verb', 'Adjective', 'Adverb', 'Phrase')),\n"
                + " word TEXT NOT NULL,\n"
                + " meaning TEXT NOT NULL,\n"
                + " romaji TEXT,\n"  // Optional: Include romaji if applicable
                + " created_date TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP\n"
                + ");";

        try {
            Class.forName("org.sqlite.JDBC");

            try (Connection connection = DriverManager.getConnection(database_url);
                 Statement statement = connection.createStatement()) {

                // Execute table creation statements
                statement.execute(user_table_sql);
                statement.execute(pet_table_sql);
                statement.execute(flashcards_table_sql);
                statement.execute(flashcard_decks_table_sql);
                statement.execute(deck_flashcards_table_sql);

                // Execute new statistics table creation statements
                statement.execute(conversation_statistics_table_sql);
                statement.execute(flashcard_statistics_table_sql);
                statement.execute(deck_statistics_table_sql);
                statement.execute(character_recognition_statistics_table_sql);

                // Execute new Conversations table creation
                statement.execute(conversations_table_sql);

                // Execute new Character Recognition Activities table creation
                statement.execute(character_recognition_activities_table_sql);

                // Execute new Word Recognition Activities table creation
                statement.execute(word_recognition_activities_table_sql);

                // Optionally, create indexes for optimization
                String index_conversation_user_id = "CREATE INDEX IF NOT EXISTS idx_conversation_user_id ON conversation_statistics(user_id);";
                String index_flashcard_user_id = "CREATE INDEX IF NOT EXISTS idx_flashcard_user_id ON flashcard_statistics(user_id);";
                String index_deck_user_id = "CREATE INDEX IF NOT EXISTS idx_deck_user_id ON deck_statistics(user_id);";
                String index_character_recognition_user_id = "CREATE INDEX IF NOT EXISTS idx_character_recognition_user_id ON character_recognition_statistics(user_id);";

                statement.execute(index_conversation_user_id);
                statement.execute(index_flashcard_user_id);
                statement.execute(index_deck_user_id);
                statement.execute(index_character_recognition_user_id);

                // Create index on character_type for faster queries
                String index_character_type = "CREATE INDEX IF NOT EXISTS idx_character_recognition_type ON character_recognition_activities_table(character_type);";
                statement.execute(index_character_type);

                // Create index on word_type for word recognition activities
                String index_word_type = "CREATE INDEX IF NOT EXISTS idx_word_recognition_type ON word_recognition_activities_table(word_type);";
                statement.execute(index_word_type);

                logger.info("Database and all tables created successfully.");
            }
        } catch (ClassNotFoundException e) {
            logger.error("SQLite JDBC driver not found.", e);
        } catch (SQLException e) {
            logger.error("SQL error: " + e.getMessage(), e);
        }
    }

    // Optional: Insert sample conversations for testing
    public static void insertSampleConversations() {
        String database_path = "src/main/database/socslingo_database.db";
        String database_url = "jdbc:sqlite:" + database_path;

        String insert_conversation_sql = "INSERT INTO conversations_table (sentence, option_one, option_two, option_three, option_four, correct_option) VALUES "
                + "('I ___ to the store yesterday.', 'go', 'went', 'gone', 'going', 2),"
                + "('She is ___ her homework.', 'doing', 'did', 'done', 'do', 1);";

        try {
            Class.forName("org.sqlite.JDBC");

            try (Connection connection = DriverManager.getConnection(database_url);
                 Statement statement = connection.createStatement()) {
                statement.executeUpdate(insert_conversation_sql);
                logger.info("Sample conversations inserted successfully.");
            }
        } catch (ClassNotFoundException e) {
            logger.error("SQLite JDBC driver not found.", e);
        } catch (SQLException e) {
            logger.error("SQL error: " + e.getMessage(), e);
        }
    }

    // Optional: Insert sample character recognition activities for testing
    public static void insertSampleCharacterRecognitions() {
        String database_path = "src/main/database/socslingo_database.db";
        String database_url = "jdbc:sqlite:" + database_path;

        String insert_character_recognition_sql = "INSERT INTO character_recognition_activities_table (character_type, character, romaji) VALUES "
                + "('Hiragana', 'あ', 'a'),"
                + "('Hiragana', 'い', 'i'),"
                + "('Katakana', 'カ', 'ka'),"
                + "('Katakana', 'キ', 'ki'),"
                + "('Kanji', '日', 'nichi'),"
                + "('Kanji', '本', 'hon');";

        try {
            Class.forName("org.sqlite.JDBC");

            try (Connection connection = DriverManager.getConnection(database_url);
                 Statement statement = connection.createStatement()) {
                statement.executeUpdate(insert_character_recognition_sql);
                logger.info("Sample character recognition activities inserted successfully.");
            }
        } catch (ClassNotFoundException e) {
            logger.error("SQLite JDBC driver not found.", e);
        } catch (SQLException e) {
            logger.error("SQL error: " + e.getMessage(), e);
        }
    }

    // New: Insert all Hiragana and Katakana characters
    public static void insertAllHiraganaAndKatakana() {
        String database_path = "src/main/database/socslingo_database.db";
        String database_url = "jdbc:sqlite:" + database_path;

        // All Hiragana characters
        String[] hiragana = {
            "あ","い","う","え","お",
            "か","き","く","け","こ",
            "さ","し","す","せ","そ",
            "た","ち","つ","て","と",
            "な","に","ぬ","ね","の",
            "は","ひ","ふ","へ","ほ",
            "ま","み","む","め","も",
            "や","ゆ","よ",
            "ら","り","る","れ","ろ",
            "わ","を","ん"
        };

        // All Katakana characters
        String[] katakana = {
            "ア","イ","ウ","エ","オ",
            "カ","キ","ク","ケ","コ",
            "サ","シ","ス","セ","ソ",
            "タ","チ","ツ","テ","ト",
            "ナ","ニ","ヌ","ネ","ノ",
            "ハ","ヒ","フ","ヘ","ホ",
            "マ","ミ","ム","メ","モ",
            "ヤ","ユ","ヨ",
            "ラ","リ","ル","レ","ロ",
            "ワ","ヲ","ン"
        };

        // Romaji mapping for Hiragana
        String[] hiraganaRomaji = {
            "a","i","u","e","o",
            "ka","ki","ku","ke","ko",
            "sa","shi","su","se","so",
            "ta","chi","tsu","te","to",
            "na","ni","nu","ne","no",
            "ha","hi","fu","he","ho",
            "ma","mi","mu","me","mo",
            "ya","yu","yo",
            "ra","ri","ru","re","ro",
            "wa","wo","n"
        };

        // Romaji mapping for Katakana
        String[] katakanaRomaji = {
            "a","i","u","e","o",
            "ka","ki","ku","ke","ko",
            "sa","shi","su","se","so",
            "ta","chi","tsu","te","to",
            "na","ni","nu","ne","no",
            "ha","hi","fu","he","ho",
            "ma","mi","mu","me","mo",
            "ya","yu","yo",
            "ra","ri","ru","re","ro",
            "wa","wo","n"
        };

        // Building the SQL insert statements
        StringBuilder insertHiragana = new StringBuilder("INSERT INTO character_recognition_activities_table (character_type, character, romaji) VALUES ");
        for (int i = 0; i < hiragana.length; i++) {
            insertHiragana.append("('Hiragana', '").append(hiragana[i]).append("', '").append(hiraganaRomaji[i]).append("'),");
        }
        // Remove the last comma
        insertHiragana.setLength(insertHiragana.length() - 1);
        insertHiragana.append(";");

        StringBuilder insertKatakana = new StringBuilder("INSERT INTO character_recognition_activities_table (character_type, character, romaji) VALUES ");
        for (int i = 0; i < katakana.length; i++) {
            insertKatakana.append("('Katakana', '").append(katakana[i]).append("', '").append(katakanaRomaji[i]).append("'),");
        }
        // Remove the last comma
        insertKatakana.setLength(insertKatakana.length() - 1);
        insertKatakana.append(";");

        try {
            Class.forName("org.sqlite.JDBC");

            try (Connection connection = DriverManager.getConnection(database_url);
                 Statement statement = connection.createStatement()) {
                 
                // Disable auto-commit for batch insertion
                connection.setAutoCommit(false);

                // Insert Hiragana characters
                statement.executeUpdate(insertHiragana.toString());
                logger.info("All Hiragana characters inserted successfully.");

                // Insert Katakana characters
                statement.executeUpdate(insertKatakana.toString());
                logger.info("All Katakana characters inserted successfully.");

                // Commit the transaction
                connection.commit();
            } catch (SQLException e) {
                logger.error("SQL error during character insertion: " + e.getMessage(), e);
            }
        } catch (ClassNotFoundException e) {
            logger.error("SQLite JDBC driver not found.", e);
        }
    }

    // Optional: Insert sample word recognition activities for testing
    public static void insertSampleWordRecognitions() {
        String database_path = "src/main/database/socslingo_database.db";
        String database_url = "jdbc:sqlite:" + database_path;

        String insert_word_recognition_sql = "INSERT INTO word_recognition_activities_table (word_type, word, meaning, romaji) VALUES "
                + "('Noun', '猫', 'Cat', 'neko'),"
                + "('Verb', '食べる', 'To eat', 'taberu'),"
                + "('Adjective', '高い', 'High/Tall', 'takai'),"
                + "('Adverb', '早く', 'Quickly', 'hayaku'),"
                + "('Phrase', 'おはようございます', 'Good morning', 'ohayou gozaimasu');";

        try {
            Class.forName("org.sqlite.JDBC");

            try (Connection connection = DriverManager.getConnection(database_url);
                 Statement statement = connection.createStatement()) {
                statement.executeUpdate(insert_word_recognition_sql);
                logger.info("Sample word recognition activities inserted successfully.");
            }
        } catch (ClassNotFoundException e) {
            logger.error("SQLite JDBC driver not found.", e);
        } catch (SQLException e) {
            logger.error("SQL error: " + e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        createDatabase();
        insertSampleConversations(); // Optional: Insert sample conversation data
        insertSampleCharacterRecognitions(); // Optional: Insert sample character recognition data
        insertAllHiraganaAndKatakana(); // Insert all Hiragana and Katakana characters
        insertSampleWordRecognitions(); // Optional: Insert sample word recognition data
    }
}
