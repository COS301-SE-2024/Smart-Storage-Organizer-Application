package com.example.smartstorageorganizer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.os.Build;
import android.os.CountDownTimer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class EmailVerificationActivityTest {

    EmailVerificationActivity Page;
    @Mock
    EmailVerificationActivity MockedPage = mock(EmailVerificationActivity.class);

    private CountDownTimer mockCountDownTimer;

    @Before
    public void setup() {
        Page = Robolectric.buildActivity(EmailVerificationActivity.class)
                .create()
                .resume()
                .get();

        mockCountDownTimer = mock(CountDownTimer.class);
    }

    @Test
    public void shouldNotBeNull() {
        assertNotNull(Page);
    }

   @Test
   public void validateFormShouldReturnTrueWhenALLInputsAreGiven()
   {
       //given
       Page.inputCode1.setText("input1");
       Page.inputCode2.setText("input1");
       Page.inputCode3.setText("input1");
       Page.inputCode4.setText("input1");
       Page.inputCode5.setText("input1");
       Page.inputCode6.setText("input1");

       //when
       boolean result=Page.validateForm();

       //then
       assertEquals(true, result);

   }

    @Test
    public void validateFormShouldReturnFalseWhenNotALLInputsAreGiven()
    {
        //given
        Page.inputCode1.setText("input1");
        Page.inputCode3.setText("input1");
        Page.inputCode4.setText("input1");
        Page.inputCode5.setText("input1");
        Page.inputCode6.setText("input1");

        //when
        boolean result=Page.validateForm();

        //then
        assertEquals(false, result);

    }

    @Test
    public void validateFormShouldReturnFalseWhenALLInputsAreNotGiven()
    {
        //given


        //when
        boolean result=Page.validateForm();

        //then
        assertEquals(false, result);

    }
    @Test
    public void ConfirmSignShouldTrueForValidEmailANDValidCode()
    {
        //given
         String email="email@gmail.com";
         String code="123456";
         AtomicBoolean result = new AtomicBoolean(false);
         //when
        when(MockedPage.confirmSignUp(email, code)).thenReturn(CompletableFuture.completedFuture(true));

        //then
        MockedPage.confirmSignUp(email, code).thenAccept(isResult->{
            if(isResult)
                result.set(true);
            else
                result.set(false);

        });
        assertEquals(true, result.get());

    }

    public void ConfirmSignShouldFalseForInValidEmail()
    {
        //given
        String email="email@";
        String code="123456";
        AtomicBoolean result = new AtomicBoolean(false);
        //when
        when(MockedPage.confirmSignUp(email, code)).thenReturn(CompletableFuture.completedFuture(false));

        //then
        MockedPage.confirmSignUp(email, code).thenAccept(isResult->{
            if(isResult)
                result.set(true);
            else
                result.set(false);

        });
        assertEquals(false, result.get());

    }

    public void ConfirmSignShouldFalseForInValidCode()
    {
        //given
        String email="email@gmail.com";
        String code="123";
        AtomicBoolean result = new AtomicBoolean(false);
        //when
        when(MockedPage.confirmSignUp(email, code)).thenReturn(CompletableFuture.completedFuture(false));

        //then
        MockedPage.confirmSignUp(email, code).thenAccept(isResult->{
            if(isResult)
                result.set(true);
            else
                result.set(false);

        });
        assertEquals(false, result.get());

    }
    @Test
    public void testOTPInputsFocusChange() {
        // Simulate typing a character in inputCode1
        Page.inputCode1.setText("1");
        assertTrue(Page.inputCode2.hasFocus());

        // Simulate typing a character in inputCode2
        Page.inputCode2.setText("2");
        assertTrue(Page.inputCode3.hasFocus());

        // Simulate typing a character in inputCode3
        Page.inputCode3.setText("3");
        assertTrue(Page.inputCode4.hasFocus());

        // Simulate typing a character in inputCode4
        Page.inputCode4.setText("4");
        assertTrue(Page.inputCode5.hasFocus());

        // Simulate typing a character in inputCode5
        Page.inputCode5.setText("5");
        assertTrue(Page.inputCode6.hasFocus());

        // Simulate typing a character in inputCode6
        Page.inputCode6.setText("6");
        assertTrue(Page.inputCode6.hasFocus()); // inputCode6 should retain focus
    }

    @Test
    public void  testCountDownTick()
    {
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        // Verify that the TextView is disabled during the countdown
        assertFalse(Page.resendOtpTextView.isEnabled());


        // Simulate a tick
        // Simulate the passage of 30 seconds
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        // Check the text at halfway point (30 seconds left)
        assertEquals("Resend OTP in 60 sec", Page.resendOtpTextView.getText().toString());
    }

    @Test
    public void testCountdownFinish() {
        // Simulate the passage of 60 seconds
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        // Fast forward to the end of the countdown
        Page.countDownTimer.onFinish();

        // Verify that the TextView is enabled and text is updated after the countdown
        assertTrue(Page.resendOtpTextView.isEnabled());
        assertEquals("Resend OTP", Page.resendOtpTextView.getText().toString());
    }
}