package com.socslingo.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for date-related operations.
 * 
    * @author TEAM-SOCSLINGO 
    * @version 1.0
    * @since 2024-09-18
 */

public class DateUtils {
    /**
     * Default constructor for DateUtils.
     */
    public DateUtils() {
        // Default constructor
    }

    /**
     * Gets the current date as a string.
     *
     * @return the current date
     */
    
    // Method to get the current date as a String
    public static String getCurrentDate() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return currentDate.format(formatter);
    }
}
