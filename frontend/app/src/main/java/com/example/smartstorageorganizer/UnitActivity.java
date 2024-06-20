package com.example.smartstorageorganizer;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UnitActivity extends AppCompatActivity {
    String[] categories = {"Category 1", "Category 2", "Category 3", "Category 4", "Category 5", "Category 6", "Category 7", "Category 8"};
    boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_unit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {

            LinearLayout checkboxContainer = findViewById(R.id.checkbox_container);

            if(flag){
                for (String category : categories) {
                    CheckBox checkBox = new CheckBox(this);
                    checkBox.setText(category);
                    checkboxContainer.addView(checkBox);
                }
                flag = false;
            }

            // Create checkboxes dynamically and add them to the LinearLayout

            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}