package com.example.smartstorageorganizer.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OrganizationModelTest {

    private OrganizationModel organization;

    @Before
    public void setUp() {
        organization = new OrganizationModel("Tech Corp", "org123", "2024-10-01");
    }

    @Test
    public void testGetOrganizationName() {
        assertEquals("Tech Corp", organization.getOrganizationName());
    }

    @Test
    public void testSetOrganizationName() {
        organization.setOrganizationName("Innovate Inc.");
        assertEquals("Innovate Inc.", organization.getOrganizationName());
    }

    @Test
    public void testGetOrganizationId() {
        assertEquals("org123", organization.getOrganizationId());
    }

    @Test
    public void testSetOrganizationId() {
        organization.setOrganizationId("org456");
        assertEquals("org456", organization.getOrganizationId());
    }

    @Test
    public void testGetCreatedAt() {
        assertEquals("2024-10-01", organization.getCreatedAt());
    }

    @Test
    public void testSetCreatedAt() {
        organization.setCreatedAt("2024-11-01");
        assertEquals("2024-11-01", organization.getCreatedAt());
    }
}
