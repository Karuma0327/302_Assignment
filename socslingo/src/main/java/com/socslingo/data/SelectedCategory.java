package com.socslingo.data;

/**
 * A singleton class to store the selected category for character practice.
 */
public class SelectedCategory {
    public enum Category {
        HIRAGANA,
        KATAKANA,
        KANJI,
        USER
    }

    private static Category selectedCategory;

    public static Category getSelectedCategory() {
        return selectedCategory;
    }

    public static void setSelectedCategory(Category category) {
        selectedCategory = category;
    }

    
}