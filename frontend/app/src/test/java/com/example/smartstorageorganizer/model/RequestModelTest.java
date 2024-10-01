package com.example.smartstorageorganizer.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RequestModelTest {

    private RequestModel request;

    @Before
    public void setUp() {
        request = new RequestModel("2024-10-01", "John Doe", "Request description", "Pending");
    }

    @Test
    public void testGetDate() {
        assertEquals("2024-10-01", request.getDate());
    }

    @Test
    public void testGetName() {
        assertEquals("John Doe", request.getName());
    }

    @Test
    public void testGetDescription() {
        assertEquals("Request description", request.getDescription());
    }

    @Test
    public void testGetStatus() {
        assertEquals("Pending", request.getStatus());
    }
}
