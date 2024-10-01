package com.example.smartstorageorganizer.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserModelTest {

    private UserModel user;

    @Before
    public void setUp() {
        user = new UserModel("2024-10-01", "John Doe", "Active");
        user.setSurname("Doe");
        user.setEmail("john.doe@example.com");
    }

    @Test
    public void testGetDate() {
        assertEquals("2024-10-01", user.getDate());
    }

    @Test
    public void testSetDate() {
        user.setDate("2024-11-01");
        assertEquals("2024-11-01", user.getDate());
    }

    @Test
    public void testGetName() {
        assertEquals("John Doe", user.getName());
    }

    @Test
    public void testSetName() {
        user.setName("Jane Doe");
        assertEquals("Jane Doe", user.getName());
    }

    @Test
    public void testGetSurname() {
        assertEquals("Doe", user.getSurname());
    }

    @Test
    public void testSetSurname() {
        user.setSurname("Smith");
        assertEquals("Smith", user.getSurname());
    }

    @Test
    public void testGetEmail() {
        assertEquals("john.doe@example.com", user.getEmail());
    }

    @Test
    public void testSetEmail() {
        user.setEmail("jane.doe@example.com");
        assertEquals("jane.doe@example.com", user.getEmail());
    }

    @Test
    public void testGetStatus() {
        assertEquals("Active", user.getStatus());
    }

    @Test
    public void testSetStatus() {
        user.setStatus("Inactive");
        assertEquals("Inactive", user.getStatus());
    }
}
