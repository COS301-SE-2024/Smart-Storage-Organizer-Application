package com.example.smartstorageorganizer;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstorageorganizer.adapters.UnitsAdapter;
import com.example.smartstorageorganizer.model.unitModel;

import java.util.ArrayList;
import java.util.List;

public class ViewUnitsActivity extends AppCompatActivity {
    private RecyclerView recyclerViewUnits;
    private UnitsAdapter unitsAdapter;
    private List<unitModel> unitList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_units);

        recyclerViewUnits = findViewById(R.id.recyclerViewUnits);
        recyclerViewUnits.setLayoutManager(new LinearLayoutManager(this));
//
        unitList = new ArrayList<>();
//        // Add units to the list
        unitList.add(new unitModel("Unit 1", "1", 100, 50));
        unitList.add(new unitModel("Unit 2", "2", 120, 80));
//        // Add more units as needed
//
        unitsAdapter = new UnitsAdapter(this, unitList);
        recyclerViewUnits.setAdapter(unitsAdapter);
    }
}