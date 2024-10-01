package com.example.smartstorageorganizer.model;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class ModifyItemRequestModelTest {

    private ModifyItemRequestModel modifyItemRequestModel;

    @Before
    public void setUp() {
        // Initialize the ModifyItemRequestModel with sample data
        modifyItemRequestModel = new ModifyItemRequestModel(
                "requestId123",
                "itemNameSample",
                "itemDescriptionSample",
                "locationSample",
                "parentCategorySample",
                "subcategorySample",
                "colorCodeSample",
                "userEmailSample@example.com",
                "organizationIdSample",
                "2024-09-29",
                "requestTypeSample",
                "statusSample",
                "itemIdSample"
        );
    }

    @Test
    public void testConstructor() {
        // Test if the constructor initializes the values correctly
        assertEquals("requestId123", modifyItemRequestModel.getRequestId());
        assertEquals("itemNameSample", modifyItemRequestModel.getItemName());
        assertEquals("itemDescriptionSample", modifyItemRequestModel.getItemDescription());
        assertEquals("locationSample", modifyItemRequestModel.getLocation());
        assertEquals("parentCategorySample", modifyItemRequestModel.getParentCategory());
        assertEquals("subcategorySample", modifyItemRequestModel.getSubcategory());
        assertEquals("colorCodeSample", modifyItemRequestModel.getColorCode());
        assertEquals("userEmailSample@example.com", modifyItemRequestModel.getUserEmail());
        assertEquals("organizationIdSample", modifyItemRequestModel.getOrganizationId());
        assertEquals("2024-09-29", modifyItemRequestModel.getRequestDate());
        assertEquals("requestTypeSample", modifyItemRequestModel.getRequestType());
        assertEquals("statusSample", modifyItemRequestModel.getStatus());
        assertEquals("itemIdSample", modifyItemRequestModel.getItemId());
    }

    @Test
    public void testGettersAndSetters() {
        // Test the getter and setter for requestId
        modifyItemRequestModel.setRequestId("newRequestId");
        assertEquals("newRequestId", modifyItemRequestModel.getRequestId());

        // Test the getter and setter for itemName
        modifyItemRequestModel.setItemName("newItemName");
        assertEquals("newItemName", modifyItemRequestModel.getItemName());

        // Test the getter and setter for itemDescription
        modifyItemRequestModel.setItemDescription("newItemDescription");
        assertEquals("newItemDescription", modifyItemRequestModel.getItemDescription());

        // Test the getter and setter for location
        modifyItemRequestModel.setLocation("newLocation");
        assertEquals("newLocation", modifyItemRequestModel.getLocation());

        // Test the getter and setter for parentCategory
        modifyItemRequestModel.setParentCategory("newParentCategory");
        assertEquals("newParentCategory", modifyItemRequestModel.getParentCategory());

        // Test the getter and setter for subcategory
        modifyItemRequestModel.setSubcategory("newSubcategory");
        assertEquals("newSubcategory", modifyItemRequestModel.getSubcategory());

        // Test the getter and setter for colorCode
        modifyItemRequestModel.setColorCode("newColorCode");
        assertEquals("newColorCode", modifyItemRequestModel.getColorCode());

        // Test the getter and setter for userEmail
        modifyItemRequestModel.setUserEmail("newUserEmail@example.com");
        assertEquals("newUserEmail@example.com", modifyItemRequestModel.getUserEmail());

        // Test the getter and setter for organizationId
        modifyItemRequestModel.setOrganizationId("newOrganizationId");
        assertEquals("newOrganizationId", modifyItemRequestModel.getOrganizationId());

        // Test the getter and setter for requestDate
        modifyItemRequestModel.setRequestDate("2024-10-01");
        assertEquals("2024-10-01", modifyItemRequestModel.getRequestDate());

        // Test the getter and setter for requestType
        modifyItemRequestModel.setRequestType("newRequestType");
        assertEquals("newRequestType", modifyItemRequestModel.getRequestType());

        // Test the getter and setter for status
        modifyItemRequestModel.setStatus("newStatus");
        assertEquals("newStatus", modifyItemRequestModel.getStatus());

        // Test the getter and setter for itemId
        modifyItemRequestModel.setItemId("newItemId");
        assertEquals("newItemId", modifyItemRequestModel.getItemId());

        // Test the additional fields (optional, depending on whether they need testing)
        modifyItemRequestModel.setNewItem("newItemValue");
        assertEquals("newItemValue", modifyItemRequestModel.getNewItem());

        modifyItemRequestModel.setOldItem("oldItemValue");
        assertEquals("oldItemValue", modifyItemRequestModel.getOldItem());

        modifyItemRequestModel.setNewDescription("newDescriptionValue");
        assertEquals("newDescriptionValue", modifyItemRequestModel.getNewDescription());

        modifyItemRequestModel.setOldDescription("oldDescriptionValue");
        assertEquals("oldDescriptionValue", modifyItemRequestModel.getOldDescription());

        modifyItemRequestModel.setNewLocation("newLocationValue");
        assertEquals("newLocationValue", modifyItemRequestModel.getNewLocation());

        modifyItemRequestModel.setOldLocation("oldLocationValue");
        assertEquals("oldLocationValue", modifyItemRequestModel.getOldLocation());

        // Continue for other "new" and "old" fields if necessary...
    }
}
