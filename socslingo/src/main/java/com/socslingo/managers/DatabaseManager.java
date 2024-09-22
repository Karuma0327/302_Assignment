package com.socslingo.managers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton class responsible for managing database connections.
 * <p>
 * This class provides a centralized way to obtain database connections
 * to the Socslingo SQLite database. It ensures that only one instance
 * of the {@code DatabaseManager} exists throughout the application lifecycle.
 * </p>
 * 
 * @author TEAM-SOCSLINGO 
 * @version 1.0
 * @since 2024-09-18
 */
public class DatabaseManager {
    private static DatabaseManager instance;
    private String url;

    /**
     * Private constructor to enforce singleton pattern.
     * <p>
     * Initializes the database URL for the Socslingo SQLite database.
     * </p>
     * 
     */
    private DatabaseManager() {
        String dbPath = "src/main/database/socslingo_database.db";
        url = "jdbc:sqlite:" + dbPath;
    }

    /**
     * Retrieves the singleton instance of {@code DatabaseManager}.
     * <p>
     * If the instance does not exist, it is created. Otherwise, the existing instance is returned.
     * </p>
     * 
     * @return the singleton instance of {@code DatabaseManager}
     */
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Establishes and returns a new database connection.
     * <p>
     * This method connects to the Socslingo SQLite database using the JDBC URL.
     * 
     * @return a {@code Connection} object representing the database connection
     * @throws SQLException if a database access error occurs or the URL is {@code null}
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }
}
