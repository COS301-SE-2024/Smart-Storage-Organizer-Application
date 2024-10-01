package com.example.smartstorageorganizer.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SearchResultTest {

    private SearchResult searchResult;

    @Before
    public void setUp() {
        searchResult = new SearchResult("Initial Title", "Initial Description");
    }

    @Test
    public void testGetTitle() {
        assertEquals("Initial Title", searchResult.getTitle());
    }

    @Test
    public void testSetTitle() {
        searchResult.setTitle("Updated Title");
        assertEquals("Updated Title", searchResult.getTitle());
    }

    @Test
    public void testGetDescription() {
        assertEquals("Initial Description", searchResult.getDescription());
    }

    @Test
    public void testSetDescription() {
        searchResult.setDescription("Updated Description");
        assertEquals("Updated Description", searchResult.getDescription());
    }
}
