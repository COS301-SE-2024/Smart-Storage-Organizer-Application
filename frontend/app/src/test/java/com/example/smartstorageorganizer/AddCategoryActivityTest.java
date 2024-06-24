package com.example.smartstorageorganizer;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.os.Build;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class AddCategoryActivityTest {

    AddCategoryActivity addCategoryActivity;

    @Before
    public void setup() {
        addCategoryActivity = Robolectric.buildActivity(AddCategoryActivity.class)
                .create()
                .resume()
                .get();
    }

    @Test
    public void shouldNotBeNull() {
        assertNotNull(addCategoryActivity);
    }

    @Test
    public void validateFormShouldReturnTrueForValidParentCategory() {
        // Given
        String validParentCategory = "Electronics";

        // When
        boolean result = addCategoryActivity.validateParentForm(validParentCategory);

        // Then
        assertTrue(result);
    }
    @Test
    public void validateFormShouldReturnTrueForValidSubCategory() {
        // Given
        String validSubCategory = "Laptops";

        // When
        boolean result = addCategoryActivity.validateParentForm(validSubCategory);

        // Then
        assertTrue(result);
    }

    //    @Test
//    public void testValidateForm_EmptyParentCategory() {
//        // Given
//        String validParentCategory = "";
//
//        // When
//        boolean result = addCategoryActivity.validateParentForm(validParentCategory);
//
//        // Then
//        assertEquals(false, result);
//    }
//
//    @Test
//    public void testValidateForm_EmptySubCategory() {
//        // Given
//        String validSubCategory = "";
//
//        // When
//        boolean result = addCategoryActivity.validateParentForm(validSubCategory);
//
//        // Then
//        assertEquals(false, result);
//    }

}