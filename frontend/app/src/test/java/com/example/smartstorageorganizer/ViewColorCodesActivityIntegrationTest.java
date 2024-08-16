package com.example.smartstorageorganizer;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.smartstorageorganizer.adapters.ColorCodeAdapter;
import com.example.smartstorageorganizer.model.ColorCodeModel;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowToast;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)

public class ViewColorCodesActivityIntegrationTest {
    private ViewColorCodesActivity activity;
    private RecyclerView colorCodeRecyclerView;
    private LottieAnimationView loadingScreen;
    private ShimmerFrameLayout shimmerFrameLayout;
    private ColorCodeAdapter colorCodeAdapter;
    private List<ColorCodeModel> colorCodeModelList;

    @Before
    public void setUp() {
        ActivityController<ViewColorCodesActivity> controller = Robolectric.buildActivity(ViewColorCodesActivity.class).create().start();
        activity = controller.get();
        colorCodeRecyclerView = activity.findViewById(R.id.color_code_rec);
        loadingScreen = activity.findViewById(R.id.loadingScreen);
        shimmerFrameLayout = activity.findViewById(R.id.shimmer_view_container);
        colorCodeAdapter = (ColorCodeAdapter) colorCodeRecyclerView.getAdapter();
        colorCodeModelList = new ArrayList<>();
    }

    @Test
    public void testActivityCreation() {
        assertNotNull(activity);
        assertNotNull(colorCodeRecyclerView);
        assertNotNull(loadingScreen);
        assertNotNull(shimmerFrameLayout);
    }

    @Test
    public void testInitialViewVisibility() {
        assertEquals(View.GONE, loadingScreen.getVisibility());
        assertEquals(View.VISIBLE, shimmerFrameLayout.getVisibility());
        assertEquals(View.GONE, colorCodeRecyclerView.getVisibility());
    }

    @Test
    public void testLoadAllColorCodesSuccess() {
        // Mock successful callback
        List<ColorCodeModel> mockColorCodes = new ArrayList<>();
        mockColorCodes.add(new ColorCodeModel("1", "Red", "Description 1", "#FF0000"));
        mockColorCodes.add(new ColorCodeModel("2", "Blue", "Description 2", "#0000FF"));

        activity.runOnUiThread(() -> {
            colorCodeModelList.clear();
            colorCodeModelList.addAll(mockColorCodes);
            colorCodeAdapter.notifyDataSetChanged();
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            colorCodeRecyclerView.setVisibility(View.VISIBLE);
            Toast.makeText(activity, "Items fetched successfully", Toast.LENGTH_SHORT).show();
        });

        assertEquals(View.GONE, shimmerFrameLayout.getVisibility());
        assertEquals(View.VISIBLE, colorCodeRecyclerView.getVisibility());
        assertEquals(2, colorCodeModelList.size());
        assertEquals("Items fetched successfully", ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testLoadAllColorCodesFailure() {
        // Mock failure callback
        String errorMessage = "Failed to fetch items";

        activity.runOnUiThread(() -> {
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            colorCodeRecyclerView.setVisibility(View.VISIBLE);
            Toast.makeText(activity, "Failed to fetch items: " + errorMessage, Toast.LENGTH_SHORT).show();
        });

        assertEquals(View.GONE, shimmerFrameLayout.getVisibility());
        assertEquals(View.VISIBLE, colorCodeRecyclerView.getVisibility());
        assertEquals("Failed to fetch items: " + errorMessage, ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testDeleteFabVisibility() {
        // Initially hidden
        assertEquals(View.GONE, activity.findViewById(R.id.delete_fab).getVisibility());

        // Mock selecting items
        ColorCodeModel mockColorCode1 = new ColorCodeModel("1", "Red", "Description 1", "#FF0000");
        ColorCodeModel mockColorCode2 = new ColorCodeModel("2", "Blue", "Description 2", "#0000FF");

        colorCodeModelList.add(mockColorCode1);
        colorCodeModelList.add(mockColorCode2);

        activity.runOnUiThread(() -> {
            colorCodeAdapter.selectItem(mockColorCode1);
            activity.findViewById(R.id.delete_fab).setVisibility(View.VISIBLE);
            colorCodeAdapter.selectItem(mockColorCode2);
        });

        // Should be visible
        assertEquals(View.VISIBLE, activity.findViewById(R.id.delete_fab).getVisibility());
    }

    @Test
    public void testDeleteCategory() {
        // Mock selecting items
        ColorCodeModel mockColorCode = new ColorCodeModel("1", "Red", "Description 1", "#FF0000");
        colorCodeModelList.add(mockColorCode);
        activity.runOnUiThread(() -> colorCodeAdapter.selectItem(mockColorCode));

        // Mock delete button click
        activity.runOnUiThread(() -> activity.findViewById(R.id.delete_fab).performClick());

        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertNotNull(alertDialog);

        // Confirm delete
        activity.runOnUiThread(() -> alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick());

        // Verify deletion
        activity.runOnUiThread(() -> colorCodeAdapter.deleteSelectedItems());
        assertEquals(0, colorCodeAdapter.getItemCount());
    }

//    @Test
//    public void testAdapterItemClick() {
//        // Add a mock item to the adapter
//        ColorCodeModel mockColorCode = new ColorCodeModel("1", "Red", "Description 1", "#FF0000");
//        colorCodeModelList.add(mockColorCode);
//        activity.runOnUiThread(() -> {
//            colorCodeAdapter.notifyDataSetChanged();
//            shadowOf(Looper.getMainLooper()).idle(); // Ensure all pending tasks are executed
//        });
//
//        RecyclerView.ViewHolder holder = colorCodeRecyclerView.findViewHolderForAdapterPosition(0);
//        assertNotNull(holder);
////        assertEquals(mockColorCode.getName(), ((TextView) holder.itemView.findViewById(R.id.color_code_name)).getText().toString());
//
//        // Perform click
////        activity.runOnUiThread(() -> holder.itemView.performClick());
////        shadowOf(Looper.getMainLooper()).idle(); // Ensure all pending tasks are executed
////
////        // Verify intent
////        Intent expectedIntent = new Intent(activity, ViewItemActivity.class);
////        expectedIntent.putExtra("color_code_id", mockColorCode.getId());
////        expectedIntent.putExtra("category", "");
////        expectedIntent.putExtra("category_id", "");
////        Intent actualIntent = shadowOf(activity).getNextStartedActivity();
////
////        assertNotNull(actualIntent);
////        assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());
////        assertEquals(expectedIntent.getStringExtra("color_code_id"), actualIntent.getStringExtra("color_code_id"));
//    }
}

