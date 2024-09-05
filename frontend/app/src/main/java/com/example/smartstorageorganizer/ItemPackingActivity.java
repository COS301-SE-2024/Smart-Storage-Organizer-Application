package com.example.smartstorageorganizer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class ItemPackingActivity extends AppCompatActivity {

    private EditText widthInput, heightInput, depthInput, weightInput;
    private TextView packedItemsOutput;
    private List<Item3D> items;
    private UnitPacking3D unitPacking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packed_item);

        // Retrieve unit dimensions from the Intent
        double unitWidth = getIntent().getDoubleExtra("unitWidth", 20);  // Default value
        double unitHeight = getIntent().getDoubleExtra("unitHeight", 20);
        double unitDepth = getIntent().getDoubleExtra("unitDepth", 20);

        widthInput = findViewById(R.id.itemWidthInput);
        heightInput = findViewById(R.id.itemHeightInput);
        depthInput = findViewById(R.id.itemDepthInput);
        weightInput = findViewById(R.id.itemWeightInput);
        packedItemsOutput = findViewById(R.id.packedItemsOutput);

        items = new ArrayList<>();
        unitPacking = new UnitPacking3D(unitWidth, unitHeight, unitDepth);  // Unit or plane size

        Button packItemsButton = findViewById(R.id.packItemsButton);
        packItemsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemAndPack();
            }
        });
    }

    private void addItemAndPack() {
        double width = Double.parseDouble(widthInput.getText().toString());
        double height = Double.parseDouble(heightInput.getText().toString());
        double depth = Double.parseDouble(depthInput.getText().toString());
        double weight = Double.parseDouble(weightInput.getText().toString());

        items.add(new Item3D(width, height, depth, weight));
        unitPacking.packItems(items);

        StringBuilder result = new StringBuilder();
        for (Item3D item : unitPacking.getBox().getItems()) {
            result.append("Item Volume: ").append(item.getWidth()).append("x")
                    .append(item.getHeight()).append("x")
                    .append(item.getDepth()).append(" Weight: ")
                    .append(item.getWeight()).append("\n");
        }
        packedItemsOutput.setText(result.toString());
    }
}
