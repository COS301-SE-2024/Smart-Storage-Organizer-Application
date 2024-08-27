package com.example.smartstorageorganizer;
import static net.bytebuddy.matcher.ElementMatchers.any;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

import android.content.Intent;
import android.os.Build;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.example.smartstorageorganizer.model.CategoryModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class AddCategoryActivityTest {

    AddCategoryActivity addCategoryActivity;
    private RadioGroup radioGroup;
    private RadioButton selectedRadioButton;
    private TextInputLayout subcategoryInput;
    private TextInputLayout parentCategoryInput;
    private ImageView parentCategoryImage;
    private ConstraintLayout uploadButton;
    private TextView spinnerHeaderText;
    private String currentSelectedParent;
    private LinearLayout addCategoryLayout;
    private ConstraintLayout addButton;
    private TextInputEditText parentCategoryEditText;
    private TextInputEditText subCategoryEditText;
    private LottieAnimationView loadingScreen;
    private List<CategoryModel> categoryModelList = new ArrayList<>();
    private Spinner categorySpinner;


    @Before
    public void setup() {
        addCategoryActivity = Robolectric.buildActivity(AddCategoryActivity.class)
                .create()
                .resume()
                .get();
        categorySpinner = addCategoryActivity.findViewById(R.id.mySpinner);
        radioGroup = addCategoryActivity.findViewById(R.id.radioGroup);
        subcategoryInput = addCategoryActivity.findViewById(R.id.subcategoryInput);
        parentCategoryInput =addCategoryActivity. findViewById(R.id.parentcategoryInput);
        parentCategoryEditText = addCategoryActivity. findViewById(R.id.parentcategory);
        spinnerHeaderText =addCategoryActivity.findViewById(R.id.spinnerHeaderText);
        addButton = addCategoryActivity.findViewById(R.id.addButton);
        addCategoryLayout = addCategoryActivity.findViewById(R.id.addCategoryLayout);
        subCategoryEditText =addCategoryActivity.findViewById(R.id.subcategory);
        parentCategoryImage = addCategoryActivity.findViewById(R.id.parentCategoryImage);
        uploadButton = addCategoryActivity.findViewById(R.id.uploadButton);
        loadingScreen = addCategoryActivity.findViewById(R.id.loadingScreen);

    }

    @Test
    public void shouldNotBeNull() {
        assertNotNull(addCategoryActivity);
    }

    @Test
    public void ValidateUI()
    {
        assertNotNull(categorySpinner);
        assertNotNull(radioGroup);
        assertNotNull(subcategoryInput);
        assertNotNull(parentCategoryInput);
        assertNotNull(parentCategoryImage);
        assertNotNull(uploadButton);
        assertNotNull(spinnerHeaderText);
        assertNotNull(addCategoryLayout);
        assertNotNull(addButton);
        assertNotNull(parentCategoryEditText);
        assertNotNull(subCategoryEditText);
    }
    @Test
    public void testNavigateToHome() {
        addCategoryActivity.navigateToHome();

        Intent expectedIntent = new Intent(addCategoryActivity, HomeActivity.class);
        Intent actualIntent = shadowOf(addCategoryActivity).getNextStartedActivity();
        assertTrue(actualIntent.filterEquals(expectedIntent));
        assertTrue(addCategoryActivity.isFinishing());
    }
    @Test
    public void testShowSubCategoryFields() {



        // Call the method to test
        addCategoryActivity.showSubCategoryFields();

        // Verify the visibility of each view
        assertEquals(View.VISIBLE, categorySpinner.getVisibility());
        assertEquals(View.VISIBLE, spinnerHeaderText.getVisibility());
        assertEquals(View.VISIBLE, subcategoryInput.getVisibility());
        assertEquals(View.GONE, parentCategoryImage.getVisibility());
        assertEquals(View.GONE, uploadButton.getVisibility());
    }

    @Test
    public void testShowParentCategoryFields() {

        // Call the method to test
        addCategoryActivity.showParentCategoryFields();

        // Verify the visibility of each view
        assertEquals(View.GONE, categorySpinner.getVisibility());
        assertEquals(View.GONE, spinnerHeaderText.getVisibility());
        assertEquals(View.GONE, subcategoryInput.getVisibility());
        assertEquals(View.VISIBLE, parentCategoryInput.getVisibility());
        assertEquals(View.VISIBLE, parentCategoryImage.getVisibility());
        assertEquals(View.VISIBLE, uploadButton.getVisibility());
    }

    @Test
    public void testOpenGallery() {

        // Call the method to test
        addCategoryActivity.openGallery();

        // Verify the intent
        Intent expectedIntent = new Intent();
        expectedIntent.setType("image/*");
        expectedIntent.setAction(Intent.ACTION_GET_CONTENT);
        expectedIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        Intent actualIntent = shadowOf(addCategoryActivity).getNextStartedActivity();

        assertNotNull(actualIntent);
        //assertEquals(expectedIntent.getType(), actualIntent.getType());
        //assertEquals(expectedIntent.getAction(), actualIntent.getAction());
        //assertEquals(expectedIntent.getBooleanExtra(Intent.EXTRA_ALLOW_MULTIPLE, false), actualIntent.getBooleanExtra(Intent.EXTRA_ALLOW_MULTIPLE, false));

        // Verify the request code
        assertEquals(1, shadowOf(addCategoryActivity).getNextStartedActivityForResult().requestCode);
    }
    @Test
    public void testShowToast() {
        // Define the test message
        String testMessage = "Test Toast Message";

        // Call the method to test
        addCategoryActivity.showToast(testMessage);

        // Verify the toast message
        assertEquals(testMessage, ShadowToast.getTextOfLatestToast());
    }


    //    @Test
//    public void validateFormShouldReturnTrueForValidParentCategory() {
//        // Given
//        String validParentCategory = "Electronics";
//
//        // When
//        boolean result = addCategoryActivity.validateParentForm(validParentCategory);
//
//        // Then
//        assertTrue(result);
//    }
    @Test
    public void validateFormShouldReturnTrueForValidSubCategory() {
        // Given
        String validSubCategory = "Laptops";

        // When
        boolean result = addCategoryActivity.validateParentForm(validSubCategory);

        // Then
        assertTrue(result);
    }
}