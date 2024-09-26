package com.example.smartstorageorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smartstorageorganizer.model.OrganizationModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.OrganizationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchOrganizationActivity extends BaseActivity {
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<String> adapter;
    private Map<String, String> organizationMap; // Map to store organizationName -> organizationId

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);  // Assuming EdgeToEdge is correctly implemented
        setContentView(R.layout.activity_search_organization);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize AutoCompleteTextView
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        organizationMap = new HashMap<>(); // Initialize the map

        // Fetch organization details
        fetchOrganizationDetails();

        // Handle the next button click
        findViewById(R.id.nextButton).setOnClickListener(v -> {
            String selectedOrganizationName = autoCompleteTextView.getText().toString();
            String organizationId = organizationMap.get(selectedOrganizationName); // Get the corresponding organizationId

            if (organizationId != null) {
                // Pass the selected organizationId to the next activity
                Intent intent = new Intent(SearchOrganizationActivity.this, RegistrationActivity.class);
                intent.putExtra("organization_id", organizationId); // Add the organizationId as extra
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            } else {
                Toast.makeText(SearchOrganizationActivity.this, "Please select a valid organization", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Fetches the organization details
    private void fetchOrganizationDetails() {
        OrganizationUtils.fetchOrganizationsDetails(this, new OperationCallback<List<OrganizationModel>>() {
            @Override
            public void onSuccess(List<OrganizationModel> result) {
                // Prepare data for the AutoCompleteTextView
                List<String> organizationNames = new ArrayList<>();
                for (OrganizationModel organization : result) {
                    organizationNames.add(organization.getOrganizationName());
                    organizationMap.put(organization.getOrganizationName(), organization.getOrganizationId());
                }

                // Create and set the adapter for the AutoCompleteTextView
                adapter = new ArrayAdapter<>(SearchOrganizationActivity.this, android.R.layout.simple_dropdown_item_1line, organizationNames);
                autoCompleteTextView.setAdapter(adapter);
                autoCompleteTextView.setThreshold(1); // Show suggestions after 1 character

                Toast.makeText(SearchOrganizationActivity.this, "Organizations fetched successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(SearchOrganizationActivity.this, "Failed to fetch organizations: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
