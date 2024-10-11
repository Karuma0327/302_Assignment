package com.socslingo.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    
    public static String getCurrentDate() {
        LocalDate current_date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return current_date.format(formatter);
    }
}
