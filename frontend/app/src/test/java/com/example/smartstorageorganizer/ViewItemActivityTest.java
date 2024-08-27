package com.example.smartstorageorganizer;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30)
public class ViewItemActivityTest {

    private ViewItemActivity activity;

    @Before
    public void setUp() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.putExtra("category", "All");
        intent.putExtra("category_id", "1");
        activity = Robolectric.buildActivity(ViewItemActivity.class, intent)
                .create()
                .start()
                .resume()
                .get();
    }

    @After
    public void tearDown() {
        activity = null;
    }

    @Test
    public void testActivityNotNull() {
        assertNotNull(activity);
    }

    @Test
    public void testInitialViews() {
        TextView categoryTextView = activity.findViewById(R.id.category_text);
        RecyclerView recyclerView = activity.findViewById(R.id.view_all_rec);
        Spinner sortBySpinner = activity.findViewById(R.id.sort_by_filter);
        Button prevButton = activity.findViewById(R.id.prevButton);
        Button nextButton = activity.findViewById(R.id.nextButton);

        assertNotNull(categoryTextView);
        assertNotNull(recyclerView);
        assertNotNull(sortBySpinner);
        assertNotNull(prevButton);
        assertNotNull(nextButton);
    }

    @Test
    public void testBackButtonFunctionality() {
        activity.findViewById(R.id.back_home_button).performClick();
        assertTrue(activity.isFinishing());
    }

//    @Test
//    public void testCategoryTextIsSetCorrectly() {
//        TextView categoryTextView = activity.findViewById(R.id.category_text);
//        assertEquals("All", categoryTextView.getText().toString());
//    }


//    @Test
//    public void testPaginationButtonsFunctionality() {
//        Button nextButton = activity.findViewById(R.id.nextButton);
//        Button prevButton = activity.findViewById(R.id.prevButton);
//        TextView pageNumber = activity.findViewById(R.id.pageNumber);
//
//        nextButton.performClick();
//        assertEquals("Page 2", pageNumber.getText().toString());
//
//        prevButton.performClick();
//        assertEquals("Page 1", pageNumber.getText().toString());
//    }

//    @Test
//    public void testSpinnerSelection() {
//        Spinner categorySpinner = activity.findViewById(R.id.category_filter);
//        categorySpinner.setSelection(1);  // Simulate selecting a category
//        Shadows.shadowOf(activity.getMainLooper()).idle();
//
//        // Verify the selection was updated
//        assertEquals(1, categorySpinner.getSelectedItemPosition());
//    }



    @Test
    public void testLoadingItemsByCategory() {
        // Test logic for verifying items are loaded by category
        // This might require mocking the network or data layer
        RecyclerView recyclerView = activity.findViewById(R.id.view_all_rec);
        assertNotNull(recyclerView.getAdapter());
        assertEquals(0, Objects.requireNonNull(recyclerView.getAdapter()).getItemCount());
    }
}