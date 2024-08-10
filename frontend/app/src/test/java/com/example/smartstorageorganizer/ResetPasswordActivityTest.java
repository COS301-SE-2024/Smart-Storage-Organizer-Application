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
public class ResetPasswordActivityTest {

    ResetPasswordActivity Page;
    @Mock
    ResetPasswordActivity MockedPage = mock(ResetPasswordActivity.class);
    @Before
    public void setUp()  {
        Page = Robolectric.buildActivity(ResetPasswordActivity.class)
                .create()
                .resume()
                .get();
    }

    @Test
    public void onCreate() {
        assertNotNull(Page);
    }

    @Test
    public void TestConfirmPasswordShouldReturnTrueValidVerfication()
    {
        //given
        String verification="12345";
        String NewPassword="NewPassword1234";
        AtomicBoolean result = new AtomicBoolean(false);


       // when
        when(MockedPage.resetPassword(verification, NewPassword)).thenReturn(CompletableFuture.completedFuture(true));

        //then
        MockedPage.resetPassword(verification,NewPassword).thenAccept(isResult->{
            if(isResult)
                result.set(true);
            else
                result.set(false);

        });
        assertEquals(true, result.get());


    }

    public void TestConfirmPasswordShouldReturnTrueInValidVerfication()
    {
        //give
        String verification="123";
        String NewPassword="NewPassword1234";
        AtomicBoolean result = new AtomicBoolean(false);


        // when
        when(MockedPage.resetPassword(verification, NewPassword)).thenReturn(CompletableFuture.completedFuture(false));

        //then
        MockedPage.resetPassword(verification,NewPassword).thenAccept(isResult->{
            if(isResult)
                result.set(true);
            else
                result.set(false);

        });
        assertEquals(false, result.get());


    }
}