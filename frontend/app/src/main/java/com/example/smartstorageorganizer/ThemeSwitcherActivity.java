package com.example.smartstorageorganizer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ThemeSwitcherActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_THEME = "selected_theme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_theme_switcher);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Get references to the radio buttons and the radio group
        RadioGroup themeRadioGroup = findViewById(R.id.themeRadioGroup);
        RadioButton radioLight = findViewById(R.id.radioLight);
        RadioButton radioDark = findViewById(R.id.radioDark);
        RadioButton radioSystemDefault = findViewById(R.id.radioSystemDefault);

        // Load saved theme preference
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedTheme = preferences.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        // Check the appropriate radio button based on saved theme
        switch (savedTheme) {
            case AppCompatDelegate.MODE_NIGHT_NO:
                radioLight.setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
                radioDark.setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
                radioSystemDefault.setChecked(true);
                break;
        }

        themeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectedTheme;
                if (checkedId == R.id.radioLight) {
                    selectedTheme = AppCompatDelegate.MODE_NIGHT_NO;
                } else if (checkedId == R.id.radioDark) {
                    selectedTheme = AppCompatDelegate.MODE_NIGHT_YES;
                } else {
                    selectedTheme = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                }

                // Save the selected theme to SharedPreferences
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(KEY_THEME, selectedTheme);
                editor.apply();

                // Apply the selected theme
                AppCompatDelegate.setDefaultNightMode(selectedTheme);
            }
        });

    }
}