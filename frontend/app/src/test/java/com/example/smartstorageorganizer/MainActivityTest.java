package com.example.smartstorageorganizer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class MainActivityTest {

    MainActivity Page;
    @Mock
    MainActivity MockedPage = mock(MainActivity.class);

    @Before
    public void setup() {
        Page = Robolectric.buildActivity(MainActivity.class)
                .create()
                .resume()
                .get();
    }

    @Test
    public void shouldNotBeNull() {
        assertNotNull(Page);
    }


    @Test
    public void isSignedInShouldReturnFalseNoSession()
    {
        //give
        AtomicBoolean result = new AtomicBoolean(false);

        //given
        when(MockedPage.isSignedIn()).thenReturn(CompletableFuture.completedFuture(false));


        //then
        MockedPage.isSignedIn().thenAccept(isResult->{
            if(isResult)
                result.set(true);
            else
                result.set(false);

        });
        assertEquals(false, result.get());
    }


    @Test
    public void isSignedInShouldReturnFalseValidSession()
    {
        //give
        AtomicBoolean result = new AtomicBoolean(false);

        //given
        when(MockedPage.isSignedIn()).thenReturn(CompletableFuture.completedFuture(true));


        //then
        MockedPage.isSignedIn().thenAccept(isResult->{
            if(isResult)
                result.set(true);
            else
                result.set(false);

        });
        assertEquals(true, result.get());
    }

}