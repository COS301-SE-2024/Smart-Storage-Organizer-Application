package com.example.smartstorageorganizer.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ColorCodeModelTest {

    private ColorCodeModel colorCode;

    @Before
    public void setUp() {
        colorCode = new ColorCodeModel("Red", "Bright Red", "#FF0000", "123");
    }

    @Test
    public void testGetName() {
        assertEquals("Red", colorCode.getName());
    }

    @Test
    public void testSetName() {
        colorCode.setName("Blue");
        assertEquals("Blue", colorCode.getName());
    }

    @Test
    public void testGetDescription() {
        assertEquals("Bright Red", colorCode.getDescription());
    }

    @Test
    public void testSetDescription() {
        colorCode.setDescription("Dark Blue");
        assertEquals("Dark Blue", colorCode.getDescription());
    }

    @Test
    public void testGetColor() {
        assertEquals("#FF0000", colorCode.getColor());
    }

    @Test
    public void testSetColor() {
        colorCode.setColor("#0000FF");
        assertEquals("#0000FF", colorCode.getColor());
    }

//    @Test
//    public void testGetId() {
//        assertEquals("123", colorCode.getId());
//    }

    @Test
    public void testSetId() {
        colorCode.setId("456");
        assertEquals("456", colorCode.getId());
    }

    @Test
    public void testGetQrCode() {
        colorCode.setQrCode("QR123");
        assertEquals("QR123", colorCode.getQrCode());
    }

    @Test
    public void testSetQrCode() {
        colorCode.setQrCode("QR456");
        assertEquals("QR456", colorCode.getQrCode());
    }
}
