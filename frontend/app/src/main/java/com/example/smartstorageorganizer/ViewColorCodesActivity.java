package com.example.smartstorageorganizer;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.smartstorageorganizer.adapters.ColorCodeAdapter;
import com.example.smartstorageorganizer.adapters.ItemAdapter;
import com.example.smartstorageorganizer.model.ColorCodeModel;
import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewColorCodesActivity extends AppCompatActivity {
    private RecyclerView colorCodeRecyclerView;
    private List<ColorCodeModel> colorCodeModelList;
    private ColorCodeAdapter colorCodeAdapter;
    private FloatingActionButton deleteFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_color_codes);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        colorCodeRecyclerView = findViewById(R.id.color_code_rec);
        deleteFab = findViewById(R.id.delete_fab);
        deleteFab.setVisibility(View.GONE); // Initially hidden

        colorCodeRecyclerView.setHasFixedSize(true);
        colorCodeRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        colorCodeModelList = new ArrayList<>();
        colorCodeAdapter = new ColorCodeAdapter(this, colorCodeModelList, selectedItems -> {
            if (selectedItems.isEmpty()) {
                deleteFab.setVisibility(View.GONE);
            } else {
                deleteFab.setVisibility(View.VISIBLE);
            }
        });
        colorCodeRecyclerView.setAdapter(colorCodeAdapter);

        deleteFab.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Delete Color Codes")
                .setMessage("Are you sure you want to delete the selected color codes?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    colorCodeAdapter.deleteSelectedItems();
                    deleteFab.setVisibility(View.GONE);
                })
                .setNegativeButton("No", null)
                .show());

        loadAllColorCodes();
    }

    private void loadAllColorCodes() {
        Utils.fetchAllColour(this, new OperationCallback<List<ColorCodeModel>>() {
            @Override
            public void onSuccess(List<ColorCodeModel> result) {
                colorCodeModelList.clear();
                colorCodeModelList.addAll(result);
                colorCodeAdapter.notifyDataSetChanged();
                Toast.makeText(ViewColorCodesActivity.this, "Items fetched successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(ViewColorCodesActivity.this, "Failed to fetch items: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
