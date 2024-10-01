package com.example.smartstorageorganizer.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NotificationModelTest {

    private NotificationModel notification;

    @Before
    public void setUp() {
        notification = new NotificationModel("Title", "This is a message", "2024-10-01");
    }

    @Test
    public void testGetTitle() {
        assertEquals("Title", notification.getTitle());
    }

    @Test
    public void testGetMessage() {
        assertEquals("This is a message", notification.getMessage());
    }

    @Test
    public void testGetDate() {
        assertEquals("2024-10-01", notification.getDate());
    }
}
