package com.example.smartstorageorganizer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import android.app.AlertDialog;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowDialog;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class ItemInfoActivityTest {

    ItemInfoActivity Page;


    @Before
    public void setup() {
        Page = Robolectric.buildActivity(ItemInfoActivity.class)
                .create()
                .resume()
                .get();
    }

    @Test
    public void shouldNotBeNull() {
        assertNotNull(Page);
    }
    @Test
    public void  TestPopUp()
    {
        Page.showEditItemPopup();
        AlertDialog alertDialog = (AlertDialog) ShadowDialog.getLatestDialog();
        assertNotNull(alertDialog);
    }

    @Test
    public void testUiElements() {
        TextView itemName = Page.findViewById(R.id.item_name);
        TextView itemDescription = Page.findViewById(R.id.item_description);
        TextView itemLocation = Page.findViewById(R.id.item_location);
        Button editItemButton = Page.findViewById(R.id.edit_item_button);

        assertNotNull(itemName);
        assertNotNull(itemDescription);
        assertNotNull(itemLocation);
        assertNotNull(editItemButton);


    }
    @Test
    public void testShowEditItemPopup() {
        Page.showEditItemPopup();
        AlertDialog alertDialog = (AlertDialog) ShadowDialog.getLatestDialog();
        assertNotNull(alertDialog);
        assertTrue(alertDialog.isShowing());
    }
//
//    @Test
//    public void testEditItemFlow() {
//
//        TextView itemName = Page.findViewById(R.id.item_name);
//        TextView itemDescription = Page.findViewById(R.id.item_description);
//        TextView itemLocation = Page.findViewById(R.id.item_location);
//        Button editItemButton = Page.findViewById(R.id.edit_item_button);
//
//        assertNotNull(itemName);
//        assertNotNull(itemDescription);
//        assertNotNull(itemLocation);
//        assertNotNull(editItemButton);
//
//        // Simulate button click to show edit item popup
//        editItemButton.performClick();
//
//        // Verify that the dialog is displayed
//        AlertDialog alertDialog = (AlertDialog) ShadowDialog.getLatestDialog();
//        assertNotNull(alertDialog);
//        assertTrue(alertDialog.isShowing());
//
//        // Verify dialog UI elements
//        EditText dialogItemName = alertDialog.findViewById(R.id.item_name);
//        EditText dialogItemDescription = alertDialog.findViewById(R.id.item_description);
//        EditText dialogItemLocation = alertDialog.findViewById(R.id.item_location);
//        EditText dialogItemColorCode = alertDialog.findViewById(R.id.item_color_code);
//        Button buttonNext = alertDialog.findViewById(R.id.button_edit_item);
//
//        assertNotNull(dialogItemName);
//        assertNotNull(dialogItemDescription);
//        assertNotNull(dialogItemLocation);
//        assertNotNull(dialogItemColorCode);
//        assertNotNull(buttonNext);
//
//        // Simulate user input in the dialog
//        dialogItemName.setText("New Item Name");
//        dialogItemDescription.setText("New Item Description");
//        dialogItemLocation.setText("New Item Location");
//        dialogItemColorCode.setText("New Color Code");
//
//        // Simulate button click to submit the form
//        buttonNext.performClick();
//
//        // Verify that the dialog is dismissed
//        assertTrue(!alertDialog.isShowing());
//
//        // Verify that the activity UI is updated
//        assertEquals("", itemName.getText().toString());
//        assertEquals("", itemDescription.getText().toString());
//        assertEquals("", itemLocation.getText().toString());
//    }



}