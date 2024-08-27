package com.example.smartstorageorganizer;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.example.smartstorageorganizer.ItemDetailsActivity;
import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28})
public class ItemDetailsIntegrationTest {

    private ItemDetailsActivity activity;

    @Before
    public void setUp() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ItemDetailsActivity.class);
        intent.putExtra("item_id", "1");
        intent.putExtra("item_name", "Test Item");
        intent.putExtra("item_image", "https://example.com/test_image.png");
        intent.putExtra("item_description", "Test Description");
        intent.putExtra("location", "Test Location");
        intent.putExtra("color_code", "Red");
        intent.putExtra("item_qrcode", "https://example.com/test_qrcode.png");

        ActivityScenario<ItemDetailsActivity> scenario = ActivityScenario.launch(intent);
        scenario.onActivity(activity -> this.activity = activity);
    }

    @Test
    public void testActivityNotNull() {
        assertNotNull(activity);
    }

//    @Test
//    public void testViewsAreInitializedCorrectly() {
//        TextView itemName = activity.findViewById(R.id.itemName);
//        assertNotNull(itemName);
//        assertEquals("Test Item", itemName.getText().toString());
//
//        TextView itemDescription = activity.findViewById(R.id.itemDescription);
//        assertNotNull(itemDescription);
//        assertEquals("Test Description", itemDescription.getText().toString());
//
//        TextView itemUnit = activity.findViewById(R.id.itemUnit);
//        assertNotNull(itemUnit);
//        assertEquals("Test Location", itemUnit.getText().toString());
//
//        TextView itemColorCode = activity.findViewById(R.id.itemColorCode);
//        assertNotNull(itemColorCode);
//        assertEquals("Red", itemColorCode.getText().toString());
//    }
//
//    @Test
//    public void testAccordionExpansionAndCollapse() {
//        View cardViewDescription = activity.findViewById(R.id.cardViewDescription);
//        View itemDescription = activity.findViewById(R.id.itemDescription);
//        View arrow = activity.findViewById(R.id.arrow);
//
//        assertEquals(View.GONE, itemDescription.getVisibility());
//
//        cardViewDescription.performClick();
//
//        assertEquals(View.VISIBLE, itemDescription.getVisibility());
//
//        cardViewDescription.performClick();
//
//
//    }
//
//    @Test
//    public void testQRCodeDialog() {
//        View qrCodeView = activity.findViewById(R.id.qrCode);
//        qrCodeView.performClick();
//
//        AlertDialog qrDialog = ShadowAlertDialog.getLatestAlertDialog();
//        assertNotNull(qrDialog);
//
//        ImageView qrCodeImage = qrDialog.findViewById(R.id.qr_code_image);
//        assertNotNull(qrCodeImage);
//
//        Button shareButton = qrDialog.findViewById(R.id.share_button);
//        Button downloadButton = qrDialog.findViewById(R.id.download_button);
//
//        assertNotNull(shareButton);
//        assertNotNull(downloadButton);
//    }
//
//    @Test
//    public void testFetchItemDetails() {
//        List<ItemModel> mockItemList = new ArrayList<>();
//        ItemModel mockItem = new ItemModel(
//                "1",                                 // itemId
//                "Mock Item",                          // itemName
//                "Mock Description",                   // description
//                "MockColorCode",                      // colourCoding
//                "MockBarcode",                        // barcode
//                "https://mockurl.com/mockqrcode.png", // qrcode
//                "100",                                // quantity
//                "Mock Location",                      // location
//                "mock@example.com",                   // email
//                "https://mockurl.com/mockimage.png",  // itemImage
//                "2024-08-10"                          // createdAt
//        );
//        mockItemList.add(mockItem);
//
//        Utils.fetchByID(1, activity, new OperationCallback<List<ItemModel>>() {
//            @Override
//            public void onSuccess(List<ItemModel> result) {
//                activity.runOnUiThread(() -> {
//                    assertEquals("Mock Item", activity.itemName.getText().toString());
//                    assertEquals("Mock Description", activity.itemDescription.getText().toString());
//                    assertEquals("Mock Location", activity.itemUnit.getText().toString());
//                    assertEquals("MockColorCode", activity.itemColorCode.getText().toString());
//                });
//            }
//
//            @Override
//            public void onFailure(String error) {
//                assertTrue(false); // Test should fail if this callback is invoked
//            }
//        });
//    }
}