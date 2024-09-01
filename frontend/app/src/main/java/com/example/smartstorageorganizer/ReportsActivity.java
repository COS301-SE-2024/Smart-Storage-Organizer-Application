package com.example.smartstorageorganizer;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.smartstorageorganizer.adapters.ItemAdapter;
import com.example.smartstorageorganizer.adapters.ReportAdapter;
import com.example.smartstorageorganizer.model.ReportModel;

import java.util.ArrayList;
import java.util.List;

public class ReportsActivity extends AppCompatActivity {
    private RecyclerView reportRecyclerView;
    private List<ReportModel> reportModelList;
    private ReportAdapter reportAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reports);

        setupRecyclerView();

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
    }

    private void setupRecyclerView() {
        reportRecyclerView = findViewById(R.id.recyclerViewReports);
        reportRecyclerView.setHasFixedSize(true);
        reportRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        reportModelList = new ArrayList<>();
        reportAdapter = new ReportAdapter(this, reportModelList);
        reportRecyclerView.setAdapter(reportAdapter);

        ReportModel reportOne = new ReportModel("Items", "Items report");
        ReportModel reportTwo = new ReportModel("Organizational", "Organizational report");
        ReportModel reportThree = new ReportModel("App", "App report");
        ReportModel reportFour = new ReportModel("Security", "Security report");
        ReportModel reportFive = new ReportModel("Custom", "Custom report");

        reportModelList.add(reportOne);
        reportModelList.add(reportTwo);
        reportModelList.add(reportThree);
        reportModelList.add(reportFour);
        reportModelList.add(reportFive);

        reportAdapter.notifyDataSetChanged();
    }
}