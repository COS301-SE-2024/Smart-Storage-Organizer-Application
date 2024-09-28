package com.example.smartstorageorganizer;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstorageorganizer.adapters.UnitsAdapter;
import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.model.unitModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewUnitsActivity extends BaseActivity {
    private RecyclerView recyclerViewUnits;
    private UnitsAdapter unitsAdapter;
    private List<unitModel> unitList;
    MyAmplifyApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_units);

        app = (MyAmplifyApp) getApplicationContext();

        recyclerViewUnits = findViewById(R.id.recyclerViewUnits);
        recyclerViewUnits.setLayoutManager(new LinearLayoutManager(this));

        unitList = new ArrayList<>();

        unitsAdapter = new UnitsAdapter(this, unitList);
        recyclerViewUnits.setAdapter(unitsAdapter);

        loadUnits("");
    }

    private void loadUnits(String authorizationToken) {

        Utils.FetchAllUnits(app.getOrganizationID(), this, new OperationCallback<List<unitModel>>() {
            @Override
            public void onSuccess(List<unitModel> result) {
                unitList.clear();
                unitList.addAll(result);
                unitsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(ViewUnitsActivity.this, "Failed to fetch units: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}