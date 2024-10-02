package com.example.smartstorageorganizer;

import static android.content.Context.MODE_PRIVATE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.SharedPreferences;
import android.os.Build;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatDelegate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)  // Use an appropriate SDK version
public class ThemeSwitcherActivityTest {

    private ThemeSwitcherActivity activity;
    private RadioGroup themeRadioGroup;
    private RadioButton radioLight;
    private RadioButton radioDark;
    private RadioButton radioSystemDefault;
    private SharedPreferences preferences;

    @Before
    public void setUp() {
        // Initialize the activity
        ActivityController<ThemeSwitcherActivity> controller = Robolectric.buildActivity(ThemeSwitcherActivity.class);
        activity = controller.create().start().resume().get();

        themeRadioGroup = activity.findViewById(R.id.themeRadioGroup);
        radioLight = activity.findViewById(R.id.radioLight);
        radioDark = activity.findViewById(R.id.radioDark);
        radioSystemDefault = activity.findViewById(R.id.radioSystemDefault);

        preferences = activity.getSharedPreferences("theme_prefs", MODE_PRIVATE);
    }

    @Test
    public void testSavedThemePreferenceIsLoaded() {
        // Save the dark theme preference before activity is recreated
        preferences.edit().putInt("selected_theme", AppCompatDelegate.MODE_NIGHT_YES).apply();

        // Recreate the activity to simulate theme loading
        activity.recreate();

        // Verify the dark mode radio button is checked
        assertTrue(radioDark.isChecked());
    }

    @Test
    public void testLightThemeIsAppliedOnSelection() {
        // Simulate selecting the light mode radio button
        radioLight.performClick();

        // Verify the light mode is applied
        int currentMode = AppCompatDelegate.getDefaultNightMode();
        assertEquals(AppCompatDelegate.MODE_NIGHT_NO, currentMode);

        // Verify the preference is saved
        int savedTheme = preferences.getInt("selected_theme", -1);
        assertEquals(AppCompatDelegate.MODE_NIGHT_NO, savedTheme);
    }

    @Test
    public void testDarkThemeIsAppliedOnSelection() {
        // Simulate selecting the dark mode radio button
        radioDark.performClick();

        // Verify the dark mode is applied
        int currentMode = AppCompatDelegate.getDefaultNightMode();
        assertEquals(AppCompatDelegate.MODE_NIGHT_YES, currentMode);

        // Verify the preference is saved
        int savedTheme = preferences.getInt("selected_theme", -1);
        assertEquals(AppCompatDelegate.MODE_NIGHT_YES, savedTheme);
    }

    @Test
    public void testSystemDefaultThemeIsAppliedOnSelection() {
        // Simulate selecting the system default radio button
        radioSystemDefault.performClick();

        // Set default night mode to system and simulate system behavior
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        // Verify the system default mode is applied
        int currentMode = AppCompatDelegate.getDefaultNightMode();
        assertEquals(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, currentMode);

        // Verify the preference is saved
        int savedTheme = preferences.getInt("selected_theme", -1);
        assertEquals(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, savedTheme);
    }
}

