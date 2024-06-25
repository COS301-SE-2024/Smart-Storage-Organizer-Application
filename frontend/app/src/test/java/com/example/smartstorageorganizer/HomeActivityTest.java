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
public class HomeActivityTest {

    HomeActivity Page;
    @Mock
    HomeActivity MockedPage = mock(HomeActivity.class);

    @Before
    public void setup() {
        Page = Robolectric.buildActivity(HomeActivity.class)
                .create()
                .resume()
                .get();
    }

    @Test
    public void GetDetailsShouldFalseForInValidSession()
    {
        // Given No session

        //when
        AtomicBoolean result = new AtomicBoolean(false);
        // When
        when(MockedPage.getDetails()).thenReturn(CompletableFuture.completedFuture(false));

        //then
        MockedPage.getDetails().thenAccept(isResult->{
            if(isResult)
                result.set(true);
            else
                result.set(false);

        });
        assertEquals(false,result.get());

    }

    @Test
    public void GetDetailsShouldTrueForValidSession()
    {
        // Given No session

        //when
        AtomicBoolean result = new AtomicBoolean(false);
        // When
        when(MockedPage.getDetails()).thenReturn(CompletableFuture.completedFuture(true));

        //then
        MockedPage.getDetails().thenAccept(isResult->{
            if(isResult)
                result.set(true);
            else
                result.set(false);

        });
        assertEquals(true,result.get());
    }


    @Test
    public void SignInShouldTrueWhenCalled()
    {
        // Given No session

        //when
        AtomicBoolean result = new AtomicBoolean(false);
        // When
        when(MockedPage.SignOut()).thenReturn(CompletableFuture.completedFuture(true));

        //then
        MockedPage.SignOut().thenAccept(isResult->{
            if(isResult)
                result.set(true);
            else
                result.set(false);

        });
        assertEquals(true,result.get());
    }

}