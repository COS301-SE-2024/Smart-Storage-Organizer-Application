package com.example.smartstorageorganizer;

import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.amplifyframework.auth.AuthCategory;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.Consumer;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.shadows.ShadowToast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import yuku.ambilwarna.AmbilWarnaDialog;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class AddColorCodeActivityTest {
    private AddColorCodeActivity activity;
    private TextInputEditText titleEditText;
    private TextInputEditText descriptionEditText;
    private View colorPreview;
    private Button pickColorButton;
    private Button addColorCodeButton;

    @Before
    public void setUp() {
        activity = Robolectric.buildActivity(AddColorCodeActivity.class).create().start().resume().get();
        titleEditText = activity.findViewById(R.id.colorCodeName);
        descriptionEditText = activity.findViewById(R.id.colorCodeDescription);
        colorPreview = activity.findViewById(R.id.preview_selected_color);
        pickColorButton = activity.findViewById(R.id.pick_color_button);
        addColorCodeButton = activity.findViewById(R.id.add_colorcode_button);
    }

    @Test
    public void testActivityCreation() {
        assertNotNull(activity);
        assertNotNull(titleEditText);
        assertNotNull(descriptionEditText);
        assertNotNull(colorPreview);
        assertNotNull(pickColorButton);
        assertNotNull(addColorCodeButton);
    }

    @Test
    public void testInitialViewVisibility() {
        assertEquals(View.VISIBLE, colorPreview.getVisibility());
    }

    @Test
    public void testValidateForm() {
        // Test empty title
        titleEditText.setText("");
        descriptionEditText.setText("Description");
        assertFalse(activity.validateForm(titleEditText.getText().toString(), descriptionEditText.getText().toString()));
        assertEquals("Title is required.", titleEditText.getError());

        // Test empty description
        titleEditText.setText("Title");
        descriptionEditText.setText("");
        assertFalse(activity.validateForm(titleEditText.getText().toString(), descriptionEditText.getText().toString()));
        assertEquals("Description is required.", descriptionEditText.getError());

        // Test valid input
        titleEditText.setText("Title");
        descriptionEditText.setText("Description");
        assertTrue(activity.validateForm(titleEditText.getText().toString(), descriptionEditText.getText().toString()));
    }

    @Test
    public void testConvertIntToHexColor() {
        AddColorCodeActivity activity = new AddColorCodeActivity();

        // Test with a known color value
        assertEquals("#FFFFFF", activity.convertIntToHexColor(0xFFFFFF));
        assertEquals("#000000", activity.convertIntToHexColor(0x000000));
    }
    @Test
    public void testShowToast() {

        String testMessage = "Test Toast Message";


        activity.showToast(testMessage);


        assertEquals(testMessage, ShadowToast.getTextOfLatestToast());
    }




//    @Test
//    public void testGetDetails() throws ExecutionException, InterruptedException {
//        // Mock Amplify.Auth
//        AuthCategory auth = Mockito.mock(AuthCategory.class);
//
//        // Mock attributes
//        List<AuthUserAttribute> attributes = new ArrayList<>();
//        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.email(), "test@example.com"));
//
//        // Mock successful fetch
//        doAnswer(invocation -> {
//            Consumer<List<AuthUserAttribute>> onResult = invocation.getArgument(0);
//            onResult.accept(attributes);
//            return null;
//        }).when(auth).fetchUserAttributes(any(), any());
//
//        // Create instance of class containing getDetails
////        MyClass myClass = new MyClass(auth);
//        CompletableFuture<Boolean> future = activity.getDetails();
//
//        // Assert that the future completes successfully
//        assertTrue(future.get());
////        assertEquals("test@example.com", myClass.getCurrentEmail());
//    }

//    @Test
//    public void testFetchUserDetails() {
//        // Mock the Amplify Auth call
//        List<AuthUserAttribute> mockAttributes = new ArrayList<>();
//        mockAttributes.add(new AuthUserAttribute(AuthUserAttributeKey.email(), "test@example.com"));
//
//        Amplify.Auth = mock(Amplify.class);
//        doAnswer(invocation -> {
//            Consumer<List<AuthUserAttribute>> onSuccess = invocation.getArgument(0);
//            onSuccess.accept(mockAttributes);
//            return null;
//        }).when(Amplify.Auth).fetchUserAttributes(any(), any());
//
//        CompletableFuture<Boolean> future = activity.getDetails();
//        assertTrue(future.join());
//        assertEquals("test@example.com", activity.getCurrentEmail());
//    }

//    @Test
//    public void testOpenColorPickerDialogue() {
//        // Mock AmbilWarnaDialog
//        AmbilWarnaDialog colorPickerDialog = mock(AmbilWarnaDialog.class);
//        whenNew(AmbilWarnaDialog.class).withAnyArguments().thenReturn(colorPickerDialog);
//
//        pickColorButton.performClick();
//        verify(colorPickerDialog).show();
//    }

//    @Test
//    public void testAddNewColorCodeSuccess() {
//        titleEditText.setText("Title");
//        descriptionEditText.setText("Description");
//        activity.runOnUiThread(() -> {
//            addColorCodeButton.performClick();
//            shadowOf(Looper.getMainLooper()).idle(); // Ensure all pending tasks are executed
//        });
//
//        // Mock Utils.addColourGroup
//        doAnswer(invocation -> {
//            OperationCallback<Boolean> callback = invocation.getArgument(5);
//            callback.onSuccess(true);
//            return null;
//        }).when(Utils.class, "addColourGroup", anyString(), anyString(), anyString(), anyString(), any(Activity.class), any(OperationCallback.class));
//
//        assertEquals("Color Code added successfully", ShadowToast.getTextOfLatestToast());
//        Intent expectedIntent = new Intent(activity, HomeActivity.class);
//        Intent actualIntent = shadowOf(activity).getNextStartedActivity();
//        assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());
//    }

//    @Test
//    public void testAddNewColorCodeFailure() {
//        titleEditText.setText("Title");
//        descriptionEditText.setText("Description");
//        activity.runOnUiThread(() -> {
//            addColorCodeButton.performClick();
//            shadowOf(Looper.getMainLooper()).idle(); // Ensure all pending tasks are executed
//        });
//
//        // Mock Utils.addColourGroup
//        doAnswer(invocation -> {
//            OperationCallback<Boolean> callback = invocation.getArgument(5);
//            callback.onFailure("Error message");
//            return null;
//        }).when(Utils.class, "addColourGroup", anyString(), anyString(), anyString(), anyString(), any(Activity.class), any(OperationCallback.class));
//
//        assertEquals("Failed to add category: Error message", ShadowToast.getTextOfLatestToast());
//    }
}