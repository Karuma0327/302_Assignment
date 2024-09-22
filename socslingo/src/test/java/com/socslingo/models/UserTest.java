package com.socslingo.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    public void testUserConstructor() {
        User user = new User(1, "testuser", "password123");
        assertEquals(1, user.getId(), "User ID should be initialized to 1");
        assertEquals("testuser", user.getUsername(), "Username should be initialized to 'testuser'");
        assertEquals("password123", user.getPassword(), "Password should be initialized to 'password123'");
        assertNull(user.getEmail(), "Email should be null by default");
    }

    @Test
    public void testSettersAndGetters() {
        User user = new User(0, null, null);

        user.setId(2);
        assertEquals(2, user.getId(), "User ID should be set to 2");

        user.setUsername("newuser");
        assertEquals("newuser", user.getUsername(), "Username should be updated to 'newuser'");

        user.setPassword("newpassword");
        assertEquals("newpassword", user.getPassword(), "Password should be updated to 'newpassword'");

        user.setEmail("user@example.com");
        assertEquals("user@example.com", user.getEmail(), "Email should be set to 'user@example.com'");
    }

    @Test
    public void testToString() {
        User user = new User(3, "stringuser", "pass");
        String expected = "User{id=3, username='stringuser', password='pass'}";
        assertEquals(expected, user.toString(), "toString() method should return the correct string representation");
    }

    @Test
    public void testEmailSetterAndGetter() {
        User user = new User(4, "emailuser", "emailpass");
        user.setEmail("emailuser@example.com");
        assertEquals("emailuser@example.com", user.getEmail(), "Email should be set and retrieved correctly");
    }

    @Test
    public void testSetEmailToNull() {
        User user = new User(5, "nullemailuser", "nullpass");
        user.setEmail("user@example.com");
        assertEquals("user@example.com", user.getEmail(), "Email should be set to 'user@example.com'");

        user.setEmail(null);
        assertNull(user.getEmail(), "Email should be set to null");
    }

    @Test
    public void testUpdateMultipleFields() {
        User user = new User(6, "multiuser", "multipass");
        user.setUsername("updateduser");
        user.setPassword("updatedpass");
        user.setEmail("updated@example.com");

        assertEquals("updateduser", user.getUsername(), "Username should be updated to 'updateduser'");
        assertEquals("updatedpass", user.getPassword(), "Password should be updated to 'updatedpass'");
        assertEquals("updated@example.com", user.getEmail(), "Email should be updated to 'updated@example.com'");
    }

    @Test
    public void testSetUsernameToEmptyString() {
        User user = new User(7, "emptyuser", "emptypass");
        user.setUsername("");
        assertEquals("", user.getUsername(), "Username should be set to an empty string");
    }

    @Test
    public void testSetPasswordToEmptyString() {
        User user = new User(8, "emptypassuser", "password");
        user.setPassword("");
        assertEquals("", user.getPassword(), "Password should be set to an empty string");
    }

    @Test
    public void testToStringWithNullFields() {
        User user = new User(9, null, null);
        String expected = "User{id=9, username='null', password='null'}";
        assertEquals(expected, user.toString(), "toString() should handle null fields correctly");
    }

    @Test
    public void testToStringWithEmptyFields() {
        User user = new User(10, "", "");
        String expected = "User{id=10, username='', password=''}";
        assertEquals(expected, user.toString(), "toString() should handle empty strings correctly");
    }
}
