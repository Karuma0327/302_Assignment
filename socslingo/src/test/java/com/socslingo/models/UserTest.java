package com.socslingo.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserCreation() {
        User user = new User(1, "john_doe", "password123", "2023-10-01");
        assertEquals(1, user.getId());
        assertEquals("john_doe", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals("2023-10-01", user.getCreatedDate());
        assertNull(user.getProfileBannerPath());
        assertNull(user.getActualName());
        assertNull(user.getEmail());
    }

    @Test
    void testSetId() {
        User user = new User(1, "john_doe", "password123", "2023-10-01");
        user.setId(2);
        assertEquals(2, user.getId());
    }

    @Test
    void testSetUsername() {
        User user = new User(1, "john_doe", "password123", "2023-10-01");
        user.setUsername("jane_doe");
        assertEquals("jane_doe", user.getUsername());
    }

    @Test
    void testSetPassword() {
        User user = new User(1, "john_doe", "password123", "2023-10-01");
        user.setPassword("newpassword456");
        assertEquals("newpassword456", user.getPassword());
    }

    @Test
    void testSetCreatedDate() {
        User user = new User(1, "john_doe", "password123", "2023-10-01");
        user.setCreatedDate("2023-11-01");
        assertEquals("2023-11-01", user.getCreatedDate());
    }

    @Test
    void testSetProfileBannerPath() {
        User user = new User(1, "john_doe", "password123", "2023-10-01");
        user.setProfileBannerPath("/images/john.png");
        assertEquals("/images/john.png", user.getProfileBannerPath());
    }

    @Test
    void testSetActualName() {
        User user = new User(1, "john_doe", "password123", "2023-10-01");
        user.setActualName("John Doe");
        assertEquals("John Doe", user.getActualName());
    }

    @Test
    void testSetEmail() {
        User user = new User(1, "john_doe", "password123", "2023-10-01");
        user.setEmail("john.doe@example.com");
        assertEquals("john.doe@example.com", user.getEmail());
    }

    @Test
    void testGetId() {
        User user = new User(3, "alice", "alicePass", "2023-12-01");
        assertEquals(3, user.getId());
    }

    @Test
    void testGetUsername() {
        User user = new User(4, "bob", "bobPass", "2023-12-02");
        assertEquals("bob", user.getUsername());
    }

    @Test
    void testGetPassword() {
        User user = new User(5, "charlie", "charliePass", "2023-12-03");
        assertEquals("charliePass", user.getPassword());
    }

    @Test
    void testGetCreatedDate() {
        User user = new User(6, "david", "davidPass", "2023-12-04");
        assertEquals("2023-12-04", user.getCreatedDate());
    }

    @Test
    void testGetProfileBannerPath() {
        User user = new User(7, "eve", "evePass", "2023-12-05");
        user.setProfileBannerPath("/images/eve.png");
        assertEquals("/images/eve.png", user.getProfileBannerPath());
    }

    @Test
    void testGetActualName() {
        User user = new User(8, "frank", "frankPass", "2023-12-06");
        user.setActualName("Frank Miller");
        assertEquals("Frank Miller", user.getActualName());
    }

    @Test
    void testGetEmail() {
        User user = new User(9, "grace", "gracePass", "2023-12-07");
        user.setEmail("grace@example.com");
        assertEquals("grace@example.com", user.getEmail());
    }

    @Test
    void testToString() {
        User user = new User(10, "henry", "henryPass", "2023-12-08");
        user.setEmail("henry@example.com");
        user.setActualName("Henry Ford");
        user.setProfileBannerPath("/images/henry.png");
        String expected = "User{id=10, username='henry', email='henry@example.com', actual_name='Henry Ford', password='henryPass', created_date='2023-12-08', profile_banner_path='/images/henry.png'}";
        assertEquals(expected, user.toString());
    }

    @Test
    void testDefaultProfileBannerPath() {
        User user = new User(11, "irene", "irenePass", "2023-12-09");
        assertNull(user.getProfileBannerPath());
    }

    @Test
    void testDefaultActualName() {
        User user = new User(12, "jack", "jackPass", "2023-12-10");
        assertNull(user.getActualName());
    }

    @Test
    void testDefaultEmail() {
        User user = new User(13, "kate", "katePass", "2023-12-11");
        assertNull(user.getEmail());
    }
}