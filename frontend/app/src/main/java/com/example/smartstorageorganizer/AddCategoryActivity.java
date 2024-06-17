package com.example.smartstorageorganizer;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;

public class AddCategoryActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private TextInputLayout subcategoryInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Spinner mySpinner = findViewById(R.id.mySpinner);
            radioGroup = findViewById(R.id.radioGroup);
            subcategoryInput = findViewById(R.id.subcategoryInput);

            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.dropdown_items, android.R.layout.simple_spinner_item);

            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // Apply the adapter to the spinner
            mySpinner.setAdapter(adapter);

            // Set a listener for the spinner

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    radioButton = findViewById(checkedId);
                    if(radioButton.getText().toString().equals("Sub Category")){
                        mySpinner.setVisibility(View.VISIBLE);
                        subcategoryInput.setVisibility(View.VISIBLE);
                        Toast.makeText(AddCategoryActivity.this, "Selected: " + radioButton.getText(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    // Get selected item
                    String selectedItem = parentView.getItemAtPosition(position).toString();
                    // Do something with the selected item
                    Toast.makeText(AddCategoryActivity.this, "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // Do something here if nothing is selected
                }
            });

            return insets;
        });
    }
}