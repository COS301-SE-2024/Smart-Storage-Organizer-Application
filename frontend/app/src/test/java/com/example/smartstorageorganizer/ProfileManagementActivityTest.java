package com.example.smartstorageorganizer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class ProfileManagementActivityTest {

    ProfileManagementActivity Page;
    @Mock
    ProfileManagementActivity MockedPage = mock(ProfileManagementActivity.class);

    @Before
    public void setup() {
        Page = Robolectric.buildActivity(ProfileManagementActivity.class)
                .create()
                .resume()
                .get();
    }

    @Test
    public void shouldNotBeNull() {
        assertNotNull(Page);
    }



}