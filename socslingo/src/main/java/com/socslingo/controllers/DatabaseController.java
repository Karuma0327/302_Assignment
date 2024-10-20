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
                + " profile_banner_path TEXT,\n"
                + " pet_id INTEGER,\n"
                + " actual_name TEXT,\n"  // Added actual_name column, not mandatory
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

        String deck_statistics_table_sql = "CREATE TABLE IF NOT EXISTS deck_statistics (\n"
                + " stat_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " user_id INTEGER NOT NULL,\n"
                + " deck_attempted INTEGER DEFAULT 0,\n"
                + " deck_completed INTEGER DEFAULT 0,\n"
                + " deck_accuracy DECIMAL(5,2) DEFAULT 0.00,\n"
                + " last_updated TEXT DEFAULT CURRENT_TIMESTAMP,\n"
                + " FOREIGN KEY (user_id) REFERENCES user_table(user_id) ON DELETE CASCADE\n"
                + ");";

        String character_recognition_statistics_table_sql = "CREATE TABLE IF NOT EXISTS character_recognition_statistics (\n"
                + " stat_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " user_id INTEGER NOT NULL,\n"
                + " characters_correct INTEGER DEFAULT 0,\n"
                + " characters_incorrect INTEGER DEFAULT 0,\n"
                + " characters_accuracy DECIMAL(5,2) DEFAULT 0.00,\n"
                + " last_updated TEXT DEFAULT CURRENT_TIMESTAMP,\n"
                + " FOREIGN KEY (user_id) REFERENCES user_table(user_id) ON DELETE CASCADE\n"
                + ");";

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

        // Updated Character Recognition Activities Table with differentiated character types
        String character_recognition_activities_table_sql = "CREATE TABLE IF NOT EXISTS character_recognition_activities_table (\n"
                + " activity_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " character_type TEXT NOT NULL CHECK (character_type IN ('Hiragana_Main', 'Hiragana_Dakuon', 'Hiragana_Combo', 'Hiragana_Long Vowels', 'Hiragana_Small_tsu', "
                + "'Katakana_Main', 'Katakana_Dakuon', 'Katakana_Combo', 'Katakana_Long Vowels', 'Katakana_Small_tsu', 'Kanji')),\n" // Ensure all types are included
                + " character TEXT NOT NULL,\n"
                + " romaji TEXT NOT NULL,\n"
                + " created_date TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP\n"
                + ");";

        // Word Recognition Activities Table (New)
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

                // Execute updated Character Recognition Activities table creation
                statement.execute(character_recognition_activities_table_sql); // Modified

                // Execute Word Recognition Activities table creation
                statement.execute(word_recognition_activities_table_sql); // Added

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

    public static void insertAllCharacterTypes() {
        String database_path = "src/main/database/socslingo_database.db";
        String database_url = "jdbc:sqlite:" + database_path;

        // Hiragana Characters
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

        // Katakana Characters
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

        // Dakuon Characters for Hiragana
        String[] hiraganaDakuon = {
            "が","ぎ","ぐ","げ","ご",
            "ざ","じ","ず","ぜ","ぞ",
            "だ","ぢ","づ","で","ど",
            "ば","び","ぶ","べ","ぼ",
            "ぱ","ぴ","ぷ","ぺ","ぽ" // Added Pakuon
        };

        String[] hiraganaDakuonRomaji = {
            "ga","gi","gu","ge","go",
            "za","ji","zu","ze","zo",
            "da","ji","zu","de","do",
            "ba","bi","bu","be","bo",
            "pa","pi","pu","pe","po" // Romaji for Pakuon
        };

        // Dakuon Characters for Katakana
        String[] katakanaDakuon = {
            "ガ","ギ","グ","ゲ","ゴ",
            "ザ","ジ","ズ","ゼ","ゾ",
            "ダ","ヂ","ヅ","デ","ド",
            "バ","ビ","ブ","ベ","ボ",
            "パ","ピ","プ","ペ","ポ"
        };

        String[] katakanaDakuonRomaji = {
            "ga","gi","gu","ge","go",
            "za","ji","zu","ze","zo",
            "da","ji","zu","de","do",
            "ba","bi","bu","be","bo",
            "pa","pi","pu","pe","po"
        };

        // Combo Characters for Hiragana
        String[] hiraganaCombo = {
            "きゃ","きゅ","きょ",
            "しゃ","しゅ","しょ",
            "ちゃ","ちゅ","ちょ",
            "にゃ","にゅ","にょ",
            "ひゃ","ひゅ","ひょ",
            "みゃ","みゅ","みょ",
            "りゃ","りゅ","りょ",
            "ぎゃ","ぎゅ","ぎょ", // Added
            "じゃ","じゅ","じょ", // Added
            "びゃ","びゅ","びょ", // Added
            "ぴゃ","ぴゅ","ぴょ"  // Added
        };

        String[] hiraganaComboRomaji = {
            "kya","kyu","kyo",
            "sha","shu","sho",
            "cha","chu","cho",
            "nya","nyu","nyo",
            "hya","hyu","hyo",
            "mya","myu","myo",
            "rya","ryu","ryo",
            "gya","gyu","gyo", // Added
            "ja","ju","jo",   // Added
            "bya","byu","byo", // Added
            "pya","pyu","pyo"  // Added
        };

        // Combo Characters for Katakana
        String[] katakanaCombo = {
            "キャ","キュ","キョ",
            "シャ","シュ","ショ",
            "チャ","チュ","チョ",
            "ニャ","ニュ","ニョ",
            "ヒャ","ヒュ","ヒョ",
            "ミャ","ミュ","ミョ",
            "リャ","リュ","リョ",
            "ギャ","ギュ","ギョ", // Added
            "ジャ","ジュ","ジョ", // Added
            "ビャ","ビュ","ビョ", // Added
            "ピャ","ピュ","ピョ"  // Added
        };

        String[] katakanaComboRomaji = {
            "kya","kyu","kyo",
            "sha","shu","sho",
            "cha","chu","cho",
            "nya","nyu","nyo",
            "hya","hyu","hyo",
            "mya","myu","myo",
            "rya","ryu","ryo",
            "gya","gyu","gyo", // Added
            "ja","ju","jo",   // Added
            "bya","byu","byo", // Added
            "pya","pyu","pyo"  // Added
        };

        // Small っ Characters for Hiragana
        String[] hiraganaSmallTsu = {
            "っか","っき","っく","っけ","っこ",
            "っさ","っし","っす","っせ","っそ",
            "った","っち","っつ","って","っと",
            "っぱ","っぴ","っぷ","っぺ","っぽ"
        };

        String[] hiraganaSmallTsuRomaji = {
            "kka","kki","kku","kke","kko",
            "ssa","sshi","ssu","sse","sso",
            "tta","cchi","ttsu","tte","tto",
            "ppa","ppi","ppu","ppe","ppo"
        };

        // Small ッ Characters for Katakana
        String[] katakanaSmallTsu = {
            "ッカ","ッキ","ック","ッケ","ッコ",
            "ッサ","ッシ","ッス","ッセ","ッソ",
            "ッタ","ッチ","ッツ","ッテ","ット",
            "ッパ","ッピ","ップ","ッペ","ッポ"
        };

        String[] katakanaSmallTsuRomaji = {
            "kka","kki","kku","kke","kko",
            "ssa","sshi","ssu","sse","sso",
            "tta","cchi","ttsu","tte","tto",
            "ppa","ppi","ppu","ppe","ppo"
        };

        // Long Vowels for Hiragana
        String[] hiraganaLongVowels = {
            "ああ","いい","うう","ええ","おお",
            "かあ","きい","くう","けえ","こお",
            "さあ","しい","すう","せえ","そう",
            "たあ","ちい","つう","てえ","とお",
            "なあ","にい","ぬう","ねえ","のお",
            "はあ","ひい","ふう","へえ","ほお",
            "まあ","みい","むう","めえ","もお",
            "やあ","ゆう","よお",
            "らあ","りい","るう","れえ","ろお",
            "わあ","をう"
        };

        String[] hiraganaLongVowelsRomaji = {
            "aa","ii","uu","ee","oo",
            "kaa","kii","kuu","kee","koo",
            "saa","shii","suu","see","soo",
            "taa","chii","tsuu","tee","too",
            "naa","nii","nuu","nee","noo",
            "haa","hii","fuu","hee","hoo",
            "maa","mii","muu","mee","moo",
            "yaa","yuu","yoo",
            "raa","rii","ruu","ree","roo",
            "waa","woo"
        };

        // Long Vowels for Katakana
        String[] katakanaLongVowels = {
            "アー","イー","ウー","エー","オー",
            "カー","キー","クー","ケー","コー",
            "サー","シー","スー","セー","ソー",
            "ター","チー","ツー","テー","トー",
            "ナー","ニー","ヌー","ネー","ノー",
            "ハー","ヒー","フー","ヘー","ホー",
            "マー","ミー","ムー","メー","モー",
            "ヤー","ユー","ヨー",
            "ラー","リー","ルー","レー","ロー",
            "ワー","ヲー"
        };

        String[] katakanaLongVowelsRomaji = {
            "ā","ī","ū","ē","ō",
            "kā","kī","kū","kē","kō",
            "sā","shī","sū","sē","sō",
            "tā","chī","tsū","tē","tō",
            "nā","nī","nū","nē","nō",
            "hā","hī","fū","hē","hō",
            "mā","mī","mū","mē","mō",
            "yā","yū","yō",
            "rā","rī","rū","rē","rō",
            "wā","wō"
        };

        // Building the SQL insert statements
        StringBuilder insertHiragana = new StringBuilder("INSERT INTO character_recognition_activities_table (character_type, character, romaji) VALUES ");
        for (int i = 0; i < hiragana.length; i++) {
            insertHiragana.append("('Hiragana_Main', '").append(hiragana[i]).append("', '").append(hiraganaRomaji[i]).append("'),");
        }
        // Remove the last comma
        insertHiragana.setLength(insertHiragana.length() - 1);
        insertHiragana.append(";");

        StringBuilder insertKatakana = new StringBuilder("INSERT INTO character_recognition_activities_table (character_type, character, romaji) VALUES ");
        for (int i = 0; i < katakana.length; i++) {
            insertKatakana.append("('Katakana_Main', '").append(katakana[i]).append("', '").append(katakanaRomaji[i]).append("'),");
        }
        insertKatakana.setLength(insertKatakana.length() - 1);
        insertKatakana.append(";");

        StringBuilder insertHiraganaDakuon = new StringBuilder("INSERT INTO character_recognition_activities_table (character_type, character, romaji) VALUES ");
        for (int i = 0; i < hiraganaDakuon.length; i++) {
            insertHiraganaDakuon.append("('Hiragana_Dakuon', '").append(hiraganaDakuon[i]).append("', '").append(hiraganaDakuonRomaji[i]).append("'),");
        }
        insertHiraganaDakuon.setLength(insertHiraganaDakuon.length() - 1);
        insertHiraganaDakuon.append(";");

        StringBuilder insertKatakanaDakuon = new StringBuilder("INSERT INTO character_recognition_activities_table (character_type, character, romaji) VALUES ");
        for (int i = 0; i < katakanaDakuon.length; i++) {
            insertKatakanaDakuon.append("('Katakana_Dakuon', '").append(katakanaDakuon[i]).append("', '").append(katakanaDakuonRomaji[i]).append("'),");
        }
        insertKatakanaDakuon.setLength(insertKatakanaDakuon.length() - 1);
        insertKatakanaDakuon.append(";");

        StringBuilder insertHiraganaCombo = new StringBuilder("INSERT INTO character_recognition_activities_table (character_type, character, romaji) VALUES ");
        for (int i = 0; i < hiraganaCombo.length; i++) {
            insertHiraganaCombo.append("('Hiragana_Combo', '").append(hiraganaCombo[i]).append("', '").append(hiraganaComboRomaji[i]).append("'),");
        }
        insertHiraganaCombo.setLength(insertHiraganaCombo.length() - 1);
        insertHiraganaCombo.append(";");

        StringBuilder insertKatakanaCombo = new StringBuilder("INSERT INTO character_recognition_activities_table (character_type, character, romaji) VALUES ");
        for (int i = 0; i < katakanaCombo.length; i++) {
            insertKatakanaCombo.append("('Katakana_Combo', '").append(katakanaCombo[i]).append("', '").append(katakanaComboRomaji[i]).append("'),");
        }
        insertKatakanaCombo.setLength(insertKatakanaCombo.length() - 1);
        insertKatakanaCombo.append(";");

        StringBuilder insertHiraganaSmallTsu = new StringBuilder("INSERT INTO character_recognition_activities_table (character_type, character, romaji) VALUES ");
        for (int i = 0; i < hiraganaSmallTsu.length; i++) {
            insertHiraganaSmallTsu.append("('Hiragana_Small_tsu', '").append(hiraganaSmallTsu[i]).append("', '").append(hiraganaSmallTsuRomaji[i]).append("'),");
        }
        insertHiraganaSmallTsu.setLength(insertHiraganaSmallTsu.length() - 1);
        insertHiraganaSmallTsu.append(";");

        StringBuilder insertKatakanaSmallTsu = new StringBuilder("INSERT INTO character_recognition_activities_table (character_type, character, romaji) VALUES ");
        for (int i = 0; i < katakanaSmallTsu.length; i++) {
            insertKatakanaSmallTsu.append("('Katakana_Small_tsu', '").append(katakanaSmallTsu[i]).append("', '").append(katakanaSmallTsuRomaji[i]).append("'),");
        }
        insertKatakanaSmallTsu.setLength(insertKatakanaSmallTsu.length() - 1);
        insertKatakanaSmallTsu.append(";");

        // Ensure all character types are correctly defined and inserted
        StringBuilder insertHiraganaLongVowels = new StringBuilder("INSERT INTO character_recognition_activities_table (character_type, character, romaji) VALUES ");
        for (int i = 0; i < hiraganaLongVowels.length; i++) {
            insertHiraganaLongVowels.append("('Hiragana_Long Vowels', '").append(hiraganaLongVowels[i]).append("', '").append(hiraganaLongVowelsRomaji[i]).append("'),");
        }
        insertHiraganaLongVowels.setLength(insertHiraganaLongVowels.length() - 1);
        insertHiraganaLongVowels.append(";");

        StringBuilder insertKatakanaLongVowels = new StringBuilder("INSERT INTO character_recognition_activities_table (character_type, character, romaji) VALUES ");
        for (int i = 0; i < katakanaLongVowels.length; i++) {
            insertKatakanaLongVowels.append("('Katakana_Long Vowels', '").append(katakanaLongVowels[i]).append("', '").append(katakanaLongVowelsRomaji[i]).append("'),");
        }
        insertKatakanaLongVowels.setLength(insertKatakanaLongVowels.length() - 1);
        insertKatakanaLongVowels.append(";");

        try {
            Class.forName("org.sqlite.JDBC");

            try (Connection connection = DriverManager.getConnection(database_url);
                 Statement statement = connection.createStatement()) {
                 
                // Disable auto-commit for batch insertion
                connection.setAutoCommit(false);

                // Insert Hiragana characters
                statement.executeUpdate(insertHiragana.toString());
                logger.info("All Hiragana_Main characters inserted successfully.");

                // Insert Katakana characters
                statement.executeUpdate(insertKatakana.toString());
                logger.info("All Katakana_Main characters inserted successfully.");

                // Insert Hiragana Dakuon characters
                statement.executeUpdate(insertHiraganaDakuon.toString()); // Added
                logger.info("All Hiragana_Dakuon characters inserted successfully.");

                // Insert Katakana Dakuon characters
                statement.executeUpdate(insertKatakanaDakuon.toString()); // Added
                logger.info("All Katakana_Dakuon characters inserted successfully.");

                // Insert Hiragana Combo characters
                statement.executeUpdate(insertHiraganaCombo.toString()); // Added
                logger.info("All Hiragana_Combo characters inserted successfully.");

                // Insert Katakana Combo characters
                statement.executeUpdate(insertKatakanaCombo.toString()); // Added
                logger.info("All Katakana_Combo characters inserted successfully.");

                // Insert Hiragana Small っ characters
                statement.executeUpdate(insertHiraganaSmallTsu.toString()); // Added
                logger.info("All Hiragana_Small_tsu characters inserted successfully.");

                // Insert Katakana Small ッ characters
                statement.executeUpdate(insertKatakanaSmallTsu.toString()); // Added
                logger.info("All Katakana_Small_tsu characters inserted successfully.");

                // Insert Hiragana Long Vowels
                statement.executeUpdate(insertHiraganaLongVowels.toString()); // Added
                logger.info("All Hiragana_Long Vowels characters inserted successfully.");

                // Insert Katakana Long Vowels
                statement.executeUpdate(insertKatakanaLongVowels.toString()); // Added
                logger.info("All Katakana_Long Vowels characters inserted successfully.");

                // Commit the transaction
                connection.commit();
            } catch (SQLException e) {
                logger.error("SQL error during character insertion: " + e.getMessage(), e);
            }
        } catch (ClassNotFoundException e) {
            logger.error("SQLite JDBC driver not found.", e);
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

    // New: Insert all Hiragana, Katakana, Dakuon, Combo, Small っ/ッ, and Long Vowels
    public static void insertAllCharacterTypesMethod() {
        insertAllCharacterTypes();
    }

    public static void main(String[] args) {
        createDatabase();
        insertSampleConversations(); // Optional: Insert sample conversation data
        insertAllCharacterTypesMethod(); // Insert all character types including Katakana expansions
    }
}
