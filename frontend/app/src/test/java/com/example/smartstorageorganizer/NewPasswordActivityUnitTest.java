package com.example.smartstorageorganizer;

import static com.google.common.base.Verify.verify;

import static org.mockito.ArgumentMatchers.any;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;

import androidx.test.rule.ActivityTestRule;

import com.amplifyframework.core.Amplify;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.Robolectric;

public class NewPasswordActivityUnitTest {
    @Rule
    public ActivityTestRule<NewPasswordActivity> activityRule =
            new ActivityTestRule<>(NewPasswordActivityUnitTest.class, false, false);


    NewPasswordActivity newPasswordActivity;

    @Before
    public void setup() {
        newPasswordActivity = Robolectric.buildActivity(NewPasswordActivity.class)
                .create()
                .resume()
                .get();
    }

    @Test
    public void testResetPassword_withEmptyField() {
        Intent intent = new Intent();
        intent.putExtra("email", "test@example.com");
        intent.putExtra("verificationCode", "123456");
        activityRule.launchActivity(intent);

        NewPasswordActivity activity = activityRule.getActivity();

        EditText newPasswordField = activity.findViewById(R.id.newPassword);
        Button resetPasswordButton = activity.findViewById(R.id.buttonConfirm);

        activity.runOnUiThread(() -> newPasswordField.setText(""));

        resetPasswordButton.performClick();

    }

    @Test
    public void testResetPassword_withValidPassword() {
        Intent intent = new Intent();
        intent.putExtra("email", "test@example.com");
        intent.putExtra("verificationCode", "123456");
        activityRule.launchActivity(intent);

        NewPasswordActivity activity = activityRule.getActivity();

        EditText newPasswordField = activity.findViewById(R.id.newPassword);
        Button resetPasswordButton = activity.findViewById(R.id.buttonConfirm);

        activity.runOnUiThread(() -> newPasswordField.setText("newpassword"));

        resetPasswordButton.performClick();

    }
}
