package com.socslingo.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

class CharacterRecognitionStatisticsTest {

    @Test
    void testDefaultConstructor() {
        CharacterRecognitionStatistics stats = new CharacterRecognitionStatistics();
        // Assuming default fields are set to default values
        assertEquals(0, stats.getStatId());
        assertEquals(0, stats.getUserId());
        assertEquals(0, stats.getCharactersCorrect());
        assertEquals(0, stats.getCharactersIncorrect());
        assertEquals(0.0, stats.getCharactersAccuracy());
        assertNull(stats.getLastUpdated());
    }

    @Test
    void testParameterizedConstructorUserId() {
        CharacterRecognitionStatistics stats = new CharacterRecognitionStatistics(1);
        assertEquals(1, stats.getUserId());
        assertEquals(0, stats.getCharactersCorrect());
        assertEquals(0, stats.getCharactersIncorrect());
        assertEquals(0.0, stats.getCharactersAccuracy());
        assertNotNull(stats.getLastUpdated());
    }

    @Test
    void testFullParameterizedConstructor() {
        LocalDateTime now = LocalDateTime.now();
        CharacterRecognitionStatistics stats = new CharacterRecognitionStatistics(2, 3, 10, 5, 66.67, now);
        assertEquals(2, stats.getStatId());
        assertEquals(3, stats.getUserId());
        assertEquals(10, stats.getCharactersCorrect());
        assertEquals(5, stats.getCharactersIncorrect());
        assertEquals(66.67, stats.getCharactersAccuracy());
        assertEquals(now, stats.getLastUpdated());
    }

    @Test
    void testSetStatId() {
        CharacterRecognitionStatistics stats = new CharacterRecognitionStatistics();
        stats.setStatId(4);
        assertEquals(4, stats.getStatId());
    }

    @Test
    void testSetUserId() {
        CharacterRecognitionStatistics stats = new CharacterRecognitionStatistics();
        stats.setUserId(5);
        assertEquals(5, stats.getUserId());
    }

    @Test
    void testSetCharactersCorrect() {
        CharacterRecognitionStatistics stats = new CharacterRecognitionStatistics();
        stats.setCharactersCorrect(15);
        assertEquals(15, stats.getCharactersCorrect());
    }

    @Test
    void testSetCharactersIncorrect() {
        CharacterRecognitionStatistics stats = new CharacterRecognitionStatistics();
        stats.setCharactersIncorrect(3);
        assertEquals(3, stats.getCharactersIncorrect());
    }

    @Test
    void testSetCharactersAccuracy() {
        CharacterRecognitionStatistics stats = new CharacterRecognitionStatistics();
        stats.setCharactersAccuracy(75.5);
        assertEquals(75.5, stats.getCharactersAccuracy());
    }

    @Test
    void testSetLastUpdated() {
        CharacterRecognitionStatistics stats = new CharacterRecognitionStatistics();
        LocalDateTime time = LocalDateTime.of(2023, 10, 1, 12, 0);
        stats.setLastUpdated(time);
        assertEquals(time, stats.getLastUpdated());
    }

    @Test
    void testUpdateAccuracy() {
        CharacterRecognitionStatistics stats = new CharacterRecognitionStatistics();
        stats.setCharactersCorrect(20);
        stats.setCharactersIncorrect(10);
        stats.updateAccuracy();
        assertEquals(66.66666666666666, stats.getCharactersAccuracy());
    }

    @Test
    void testUpdateAccuracyZeroTotal() {
        CharacterRecognitionStatistics stats = new CharacterRecognitionStatistics();
        stats.setCharactersCorrect(0);
        stats.setCharactersIncorrect(0);
        stats.updateAccuracy();
        assertEquals(0.0, stats.getCharactersAccuracy());
    }

    @Test
    void testUpdateAccuracyAfterIncrement() {
        CharacterRecognitionStatistics stats = new CharacterRecognitionStatistics();
        stats.setCharactersCorrect(5);
        stats.setCharactersIncorrect(5);
        stats.updateAccuracy();
        assertEquals(50.0, stats.getCharactersAccuracy());

        stats.setCharactersCorrect(10);
        stats.updateAccuracy();
        assertEquals(66.66666666666666, stats.getCharactersAccuracy());
    }

    @Test
    void testMultipleStatistics() {
        CharacterRecognitionStatistics stats1 = new CharacterRecognitionStatistics(1);
        CharacterRecognitionStatistics stats2 = new CharacterRecognitionStatistics(2);

        stats1.setCharactersCorrect(10);
        stats1.setCharactersIncorrect(2);
        stats1.updateAccuracy();

        stats2.setCharactersCorrect(8);
        stats2.setCharactersIncorrect(4);
        stats2.updateAccuracy();

        assertEquals(83.33333333333334, stats1.getCharactersAccuracy());
        assertEquals(66.66666666666666, stats2.getCharactersAccuracy());
    }

    @Test
    void testNegativeCharactersCorrect() {
        CharacterRecognitionStatistics stats = new CharacterRecognitionStatistics();
        stats.setCharactersCorrect(-5);
        assertEquals(-5, stats.getCharactersCorrect());
    }

    @Test
    void testNegativeCharactersIncorrect() {
        CharacterRecognitionStatistics stats = new CharacterRecognitionStatistics();
        stats.setCharactersIncorrect(-3);
        assertEquals(-3, stats.getCharactersIncorrect());
    }

    @Test
    void testResetStatistics() {
        CharacterRecognitionStatistics stats = new CharacterRecognitionStatistics(3);
        stats.setCharactersCorrect(12);
        stats.setCharactersIncorrect(6);
        stats.updateAccuracy();

        stats.setCharactersCorrect(0);
        stats.setCharactersIncorrect(0);
        stats.updateAccuracy();
        assertEquals(0.0, stats.getCharactersAccuracy());
    }

    @Test
    void testStatIdDefaultsToZero() {
        CharacterRecognitionStatistics stats = new CharacterRecognitionStatistics();
        assertEquals(0, stats.getStatId());
    }

    @Test
    void testLastUpdatedNullByDefault() {
        CharacterRecognitionStatistics stats = new CharacterRecognitionStatistics();
        assertNull(stats.getLastUpdated());
    }

    @Test
    void testLastUpdatedAfterSetting() {
        CharacterRecognitionStatistics stats = new CharacterRecognitionStatistics();
        LocalDateTime now = LocalDateTime.now();
        stats.setLastUpdated(now);
        assertEquals(now, stats.getLastUpdated());
    }
}