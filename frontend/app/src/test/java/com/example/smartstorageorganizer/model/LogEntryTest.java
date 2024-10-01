package com.example.smartstorageorganizer.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LogEntryTest {

    private LogEntry logEntry;

    @Before
    public void setUp() {
        logEntry = new LogEntry(
                "JohnDoe", "Added", "Item123", "Added a new item",
                "None", "Item details"
        );
    }

    @Test
    public void testGetUserName() {
        assertEquals("JohnDoe", logEntry.getUserName());
    }

    @Test
    public void testGetActionDone() {
        assertEquals("Added", logEntry.getActionDone());
    }

    @Test
    public void testGetItemActedUpon() {
        assertEquals("Item123", logEntry.getItemActedUpon());
    }

    @Test
    public void testGetDetails() {
        assertEquals("Added a new item", logEntry.getDetails());
    }

    @Test
    public void testGetPreviousDetails() {
        assertEquals("None", logEntry.getPreviousDetails());
    }

    @Test
    public void testGetNewDetails() {
        assertEquals("Item details", logEntry.getNewDetails());
    }

    @Test
    public void testIsExpanded() {
        assertFalse(logEntry.isExpanded());
    }

    @Test
    public void testSetExpanded() {
        logEntry.setExpanded(true);
        assertTrue(logEntry.isExpanded());
    }
}
