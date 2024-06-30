package com.example.smartstorageorganizer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import android.app.AlertDialog;
import android.os.Build;

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
    @Mock
    HomeActivity MockedPage = mock(HomeActivity.class);

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




}