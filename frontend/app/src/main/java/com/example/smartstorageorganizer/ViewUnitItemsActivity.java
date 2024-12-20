package com.example.smartstorageorganizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.example.smartstorageorganizer.adapters.ItemAdapter;
import com.example.smartstorageorganizer.adapters.RecentAdapter;
import com.example.smartstorageorganizer.adapters.SkeletonAdapter;
import com.example.smartstorageorganizer.adapters.UnitsAdapter;
import com.example.smartstorageorganizer.model.ArrangementModel;
import com.example.smartstorageorganizer.model.BinItemModel;
import com.example.smartstorageorganizer.model.ColorCodeModel;
import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.model.SuggestedCategoryModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ViewUnitItemsActivity extends BaseActivity {
    private static final int PAGE_SIZE = 10;
    private int currentPage = 1;
    private TextView notFoundText;
    private ImageView backButton;
    private Spinner sortBySpinner;
    private List<ItemModel> itemModelList;
    private ItemAdapter itemAdapter;
    private LottieAnimationView loadingScreen;
    private boolean firstTime = true;
    private RecyclerView itemRecyclerView;
    private Button prevButton;
    private Button nextButton;
    private TextView pageNumber;
    private String currentSelectedOption;
    private ShimmerFrameLayout shimmerFrameLayout;
    private RecyclerView recyclerView;
    private NestedScrollView itemsLayout;
    private LinearLayout bottomNavigationView;
    List<SuggestedCategoryModel> suggestedCategoriesList;
    ProgressDialog progressDialog;
    private MyAmplifyApp app;
    private long startTime;
    private LinearLayout generateArrangementButton;
    private ConstraintLayout mainLayout, arrangementLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_unit_items);

        app = (MyAmplifyApp) getApplicationContext();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupBackButton();
        setupRecyclerView();
        loadInitialData();
        setupSortByListener();
        setupPaginationButtons();
        setupBottomNavigationBar();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        SkeletonAdapter skeletonAdapter = new SkeletonAdapter(10);
        recyclerView.setAdapter(skeletonAdapter);
    }

    private void initializeViews() {
        sortBySpinner = findViewById(R.id.sort_by_filter);
        itemRecyclerView = findViewById(R.id.view_all_rec);
        loadingScreen = findViewById(R.id.loadingScreen);
        notFoundText = findViewById(R.id.notFoundText);
        backButton = findViewById(R.id.back_home_button);
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        pageNumber = findViewById(R.id.pageNumber);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        recyclerView = findViewById(R.id.recycler_view);
        itemsLayout = findViewById(R.id.newstedScrollview);
        TextView category = findViewById(R.id.category_text);
        generateArrangementButton = findViewById(R.id.generateArrangementButton);
        arrangementLoader = findViewById(R.id.arrangementLoader);
        mainLayout = findViewById(R.id.mainLayout);
        category.setText(getIntent().getStringExtra("unit_name"));

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Suggesting Categories...");
        progressDialog.setCancelable(false);

        generateArrangementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainLayout.setVisibility(View.GONE);
                arrangementLoader.setVisibility(View.VISIBLE);
                generateProcess(getIntent().getStringExtra("unit_id"), getIntent().getStringExtra("unit_name"));
            }
        });

    }

    private void setupBackButton() {
        backButton.setOnClickListener(v -> finish());
    }

    private void setupPaginationButtons() {
        prevButton.setOnClickListener(v -> {
            if (currentPage > 1) {
                itemModelList.clear();
                itemAdapter.notifyDataSetChanged();
                currentPage--;
                firstTime = false;
                loadInitialData();
                pageNumber.setText("Page " + currentPage);
            }
        });

        nextButton.setOnClickListener(v -> {
            itemModelList.clear();
            itemAdapter.notifyDataSetChanged();
            currentPage++;
            firstTime = false;
            loadInitialData();
            pageNumber.setText("Page " + currentPage);
        });
    }

    private void updatePaginationButtons(int resultSize) {
        prevButton.setEnabled(currentPage > 1);
        nextButton.setEnabled(resultSize == PAGE_SIZE);
    }

    private void setupRecyclerView() {
        itemRecyclerView.setHasFixedSize(true);
        itemRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        itemModelList = new ArrayList<>();
        suggestedCategoriesList = new ArrayList<>();
        itemAdapter = new ItemAdapter(this, itemModelList, this);
        itemRecyclerView.setAdapter(itemAdapter);
    }

    private void loadInitialData() {
        loadUncategorizedItems();
    }

    private void loadUncategorizedItems() {
        shimmerFrameLayout.startShimmer();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        itemsLayout.setVisibility(View.GONE);
        sortBySpinner.setVisibility(View.GONE);

        Utils.fetchItemsUnderUnit(getIntent().getStringExtra("unit_name"), this, new OperationCallback<List<ItemModel>>() {
            @Override
            public void onSuccess(List<ItemModel> result) {
                itemModelList.clear();
                itemModelList.addAll(result);
                if (!Objects.equals(currentSelectedOption, "Sort by")) {
                    setupSort(currentSelectedOption);
                } else {
                    itemAdapter.notifyDataSetChanged();
                }
                notFoundText.setVisibility(result.isEmpty() ? View.VISIBLE : View.GONE);
//                Toast.makeText(ViewUnitItemsActivity.this, "Items fetched successfully", Toast.LENGTH_SHORT).show();
                updatePaginationButtons(result.size());
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                itemsLayout.setVisibility(View.VISIBLE);
                sortBySpinner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(ViewUnitItemsActivity.this, "Failed to fetch items: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSortByListener() {
        String[] dropdownItems = {"Sort by", "Newest to Oldest", "Oldest to Newest", "A to Z", "Z to A"};
        currentSelectedOption = "Sort by";
        ArrayAdapter<String> adapter = new ArrayAdapter<>(ViewUnitItemsActivity.this, android.R.layout.simple_spinner_item, dropdownItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortBySpinner.setAdapter(adapter);

        sortBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                currentSelectedOption = parentView.getItemAtPosition(position).toString();

                if (currentSelectedOption.equals("Sort by")) {
                    return;
                }

                setupSort(currentSelectedOption);
//                Toast.makeText(ViewUnitItemsActivity.this, "Selected: " + currentSelectedOption, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do something here if nothing is selected
            }
        });
    }

    private void setupSort(String sortType) {
        switch (sortType) {
            case "A to Z":
                sortItemModelsAscending(itemModelList);
                itemAdapter.notifyDataSetChanged();
                break;
            case "Z to A":
                sortItemModelsDescending(itemModelList);
                itemAdapter.notifyDataSetChanged();
                break;
            case "Newest to Oldest":
                sortItemModelsByNewest(itemModelList);
                itemAdapter.notifyDataSetChanged();
                break;
            case "Oldest to Newest":
                sortItemModelsByOldest(itemModelList);
                itemAdapter.notifyDataSetChanged();
                break;
            default:
        }
    }

    public static void sortItemModelsAscending(List<ItemModel> itemModels) {
        itemModels.sort((o1, o2) -> o1.getItemName().compareToIgnoreCase(o2.getItemName()));
    }

    public static void sortItemModelsDescending(List<ItemModel> itemModels) {
        itemModels.sort((o1, o2) -> o2.getItemName().compareToIgnoreCase(o1.getItemName()));
    }

    public static void sortItemModelsByNewest(List<ItemModel> itemModels) {
        itemModels.sort((o1, o2) -> {
            LocalDateTime date1 = LocalDateTime.parse(o1.getCreatedAt(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSXXX"));
            LocalDateTime date2 = LocalDateTime.parse(o2.getCreatedAt(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSXXX"));
            return date2.compareTo(date1);
        });
    }

    public static void sortItemModelsByOldest(List<ItemModel> itemModels) {
        itemModels.sort((o1, o2) -> {
            LocalDateTime date1 = LocalDateTime.parse(o1.getCreatedAt(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSXXX"));
            LocalDateTime date2 = LocalDateTime.parse(o2.getCreatedAt(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSXXX"));
            return date1.compareTo(date2);
        });
    }

    public void suggestCategory(String selectedIds) {
        //Show the progress bar loader
        progressDialog.show();
        Utils.RecommendMultipleCategories(selectedIds, app.getOrganizationID() , this, new OperationCallback<List<SuggestedCategoryModel>>() {
            @Override
            public void onSuccess(List<SuggestedCategoryModel> result) {
                //stop the progress bar loader
                progressDialog.dismiss();
                suggestedCategoriesList.clear();
                suggestedCategoriesList.addAll(result);
                ItemModel itemName = findItemById(result.get(0).getItemId());
                showUnverifiedDialog();
//                Toast.makeText(UncategorizedItemsActivity.this, "success: " + result.get(0).getItemId(), Toast.LENGTH_LONG).show();
                Log.i("View Suggested", itemName.getItemName());
            }

            @Override
            public void onFailure(String error) {
                progressDialog.dismiss();
                Toast.makeText(ViewUnitItemsActivity.this, "Failed to fetch items: " + error, Toast.LENGTH_LONG).show();
            }
        });

    }

    private void setupBottomNavigationBar() {
        LinearLayout deleteButton = findViewById(R.id.delete);
        LinearLayout colorButton = findViewById(R.id.color);
        LinearLayout shareButton = findViewById(R.id.share);
        LinearLayout selectAllButton = findViewById(R.id.select_all);
        LinearLayout categorizeButton = findViewById(R.id.categorize);

        deleteButton.setOnClickListener(view -> {
            // Handle delete action
        });

        colorButton.setOnClickListener(view -> {
            // Handle color assign action
            String selectedIds = itemAdapter.getSelectedItemsIds();
            progressDialog.setMessage("Fetching Color Codes...");
            progressDialog.show();
            assignItemToColor(selectedIds);
        });

        shareButton.setOnClickListener(view -> {
            // Handle share action
        });

        selectAllButton.setOnClickListener(view -> {
            // Handle select all action
            itemAdapter.selectAllItems();
        });
        categorizeButton.setOnClickListener(view -> {
            // Handle select all action
            String selectedIds = itemAdapter.getSelectedItemsIds();
            suggestCategory(selectedIds);
        });
    }

    private ItemModel findItemById(String itemId) {
        for (ItemModel item : itemModelList) {
            if (item.getItemId().equalsIgnoreCase(itemId)) {
                return item;
            }
        }
        return null;
    }

    private void showUnverifiedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.suggested_categories_popup, null);
        builder.setView(dialogView);

        LinearLayout parentLayout = dialogView.findViewById(R.id.parentLayout); // Assuming you have a LinearLayout in your XML
        Button closeButton = dialogView.findViewById(R.id.closeButton);

        for (SuggestedCategoryModel category : suggestedCategoriesList) {
            TextView suggestedCategory = new TextView(this);
            suggestedCategory.setTextSize(16);
            ItemModel itemName = findItemById(category.getItemId());
            suggestedCategory.setText(itemName.getItemName() + " (" + category.getCategoryName() + " - " + category.getSubcategoryName() + ")");
            parentLayout.addView(suggestedCategory);
        }

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle close button click
            }
        });

        builder.show();
    }

    public void updateBottomNavigationBar(boolean isVisible) {
        LinearLayout paginationLayout = findViewById(R.id.paginationLayout);
        paginationLayout.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        bottomNavigationView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public void assignItemToColor(String itemId) {
        // Fetch all available color codes
        Utils.fetchAllColour(getIntent().getStringExtra("organization_id"),this, new OperationCallback<List<ColorCodeModel>>() {
            @Override
            public void onSuccess(List<ColorCodeModel> colorCodeModelList) {
                // Create an array of color names to display to the user
                String[] colorNames = new String[colorCodeModelList.size()];
                for (int i = 0; i < colorCodeModelList.size(); i++) {
                    colorNames[i] = colorCodeModelList.get(i).getName();
                }

                // Show the color options to the user (e.g., in a dialog)
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewUnitItemsActivity.this);
                progressDialog.dismiss();
                builder.setTitle("Choose a color for the item");
                builder.setItems(colorNames, (dialog, which) -> {
                    // Get the selected color code
                    ColorCodeModel selectedColor = colorCodeModelList.get(which);
                    String colourId = selectedColor.getId();
                    Log.d("Selected Color", "Color ID: " + colourId);
                    Log.d("Selected Item", "Item ID: " + itemId);
                    progressDialog.setMessage("Assigning Item(s) to Color Code...");
                    progressDialog.show();

                    // Assign the selected color to the item
                    assignColorToItem(itemId, selectedColor);

                    //assigned to color db
                    AssignColour(colourId,itemId);
                });
                builder.show();
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle the error (e.g., show a toast or log the error)
                Toast.makeText(ViewUnitItemsActivity.this, "Failed to fetch colors: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void assignColorToItem(String itemId, ColorCodeModel colorCode) {
        // Your logic to assign the colorCode to the item with the given itemId
        // This might involve updating a database, sending a request to a server, etc.
        Log.d("AssignColor", "Item ID: " + itemId + " assigned to color: " + colorCode.getName());
    }

    public void AssignColour(String colourid, String itemid) {
        String json = "{\"colourid\":\"" + colourid + "\", \"itemid\":\"" + itemid + "\"}";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.AddItemToColourEndPoint;
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ViewUnitItemsActivity.this, "Failed to Assign Color Code to item(s)", Toast.LENGTH_SHORT).show());
                Log.e("AssignColour", "Request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    runOnUiThread(() -> Toast.makeText(ViewUnitItemsActivity.this, "Color Code Assigned Successfully.", Toast.LENGTH_SHORT).show());
                } else {
                    progressDialog.dismiss();
                    runOnUiThread(() -> Toast.makeText(ViewUnitItemsActivity.this, "Failed to Assign Color Code to item(s)", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    public void showArrangementDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.arrangement_popup, null);

        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        Button closeButton = dialogView.findViewById(R.id.finishButton);
        TextView message = dialogView.findViewById(R.id.textView3);
//        message.setText("");

        closeButton.setOnClickListener(v -> {
            alertDialog.dismiss();

        });

//        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void generateProcess(String unit_id, String unit_name) {
        Utils.generateProcess(unit_id, unit_name, this, new OperationCallback<ArrangementModel>() {
            @Override
            public void onSuccess(ArrangementModel result) {
                if (Objects.equals(result.getImageUrl(), "400")) {
                    mainLayout.setVisibility(View.VISIBLE);
                    arrangementLoader.setVisibility(View.GONE);
                    showArrangementDialog();
                } else if (Objects.equals(result.getImageUrl(), "401")) {
                    mainLayout.setVisibility(View.VISIBLE);
                    arrangementLoader.setVisibility(View.GONE);
//                    showArrangementDialog();
                    generateProcess(unit_id, unit_name);
                } else {
                    mainLayout.setVisibility(View.VISIBLE);
                    arrangementLoader.setVisibility(View.GONE);
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ViewUnitItemsActivity.this);

                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.dialog_items_list, null);
                    dialogBuilder.setView(dialogView);

                    TableLayout tableLayout = dialogView.findViewById(R.id.items_table);

                    TableRow headerRow = new TableRow(ViewUnitItemsActivity.this);
                    TextView headerName = new TextView(ViewUnitItemsActivity.this);
                    TextView headerColor = new TextView(ViewUnitItemsActivity.this);

                    headerName.setText("Name");
                    headerName.setPadding(8, 8, 8, 8);
                    headerName.setTypeface(null, Typeface.BOLD);
                    headerColor.setText("Color");
                    headerColor.setPadding(8, 8, 8, 8);
                    headerColor.setTypeface(null, Typeface.BOLD);

                    headerRow.addView(headerName);
                    headerRow.addView(headerColor);

                    tableLayout.addView(headerRow);

                    List<BinItemModel> items = result.getItems();

                    for (BinItemModel item : items) {
                        TableRow row = new TableRow(ViewUnitItemsActivity.this);

                        TextView nameView = new TextView(ViewUnitItemsActivity.this);
                        nameView.setText(item.getName());
                        nameView.setPadding(8, 8, 8, 8);

                        View colorView = new View(ViewUnitItemsActivity.this);
                        colorView.setBackgroundColor(parseColor(item.getColor()));

                        TableRow.LayoutParams params = new TableRow.LayoutParams(50, 50);
                        params.setMargins(8, 8, 8, 8);
                        colorView.setLayoutParams(params);

                        row.addView(nameView);
                        row.addView(colorView);

                        tableLayout.addView(row);
                    }

                    Button view3DButton = dialogView.findViewById(R.id.view_3d_button);
                    view3DButton.setOnClickListener(v -> {
                        Intent sceneViewerIntent = new Intent(Intent.ACTION_VIEW);
                        sceneViewerIntent.setData(Uri.parse("https://arvr.google.com/scene-viewer/1.0?file=" + result.getImageUrl()));
                        sceneViewerIntent.setPackage("com.google.android.googlequicksearchbox");
                        startActivity(sceneViewerIntent);
                    });

                    // Show the dialog
                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();
                }

            }



            @Override
            public void onFailure(String error) {
                if(Objects.equals(error.toLowerCase(), "timeout")){
                    generateProcess(unit_id, unit_name);
                }
                Toast.makeText(ViewUnitItemsActivity.this, "Failed to generate Image: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    public int parseColor(String colorString) {
        // Remove the square brackets and split the string by commas
        String[] rgbValues = colorString.replaceAll("[\\[\\]]", "").split(",");

        // Parse the RGB values and alpha channel
        int red = Integer.parseInt(rgbValues[0].trim());
        int green = Integer.parseInt(rgbValues[1].trim());
        int blue = Integer.parseInt(rgbValues[2].trim());
        int alpha = Integer.parseInt(rgbValues[3].trim());

        // Return the color as an int
        return Color.argb(alpha, red, green, blue);
    }


    private void logActivityView(String activityName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = app.getEmail();

        Map<String, Object> activityView = new HashMap<>();
        activityView.put("user_id", userId);
        activityView.put("activity_name", activityName);
        activityView.put("view_time", new Timestamp(new Date()));

        db.collection("activity_views")
                .add(activityView)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "Activity view logged."))
                .addOnFailureListener(e -> Log.w("Firestore", "Error logging activity view", e));
    }

    private void logSessionDuration(String activityName, long sessionDuration) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = app.getEmail();

        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("user_id", userId);
        sessionData.put("activity_name", activityName);
        sessionData.put("session_duration", sessionDuration); // Duration in milliseconds

        db.collection("activity_sessions")
                .add(sessionData)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "Session duration logged."))
                .addOnFailureListener(e -> Log.w("Firestore", "Error logging session duration", e));
    }
    public void logUserFlow(String toActivity) {
        long sessionDuration = System.currentTimeMillis() - startTime;
        logSessionDuration("ViewUnitItemsActivity", (sessionDuration));
        long transitionTime = System.currentTimeMillis();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = app.getEmail();

        Map<String, Object> userFlowData = new HashMap<>();
        userFlowData.put("user_id", userId);
        userFlowData.put("previous_activity", "ViewUnitItemsActivity");
        userFlowData.put("next_activity", toActivity);
        userFlowData.put("transition_time", new Timestamp(new Date(transitionTime)));

        db.collection("user_flow")
                .add(userFlowData)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "User flow logged."))
                .addOnFailureListener(e -> Log.w("Firestore", "Error logging user flow", e));
    }

//    public void logUser

    @Override
    public void onStart() {
        super.onStart();
        logActivityView("ViewUnitItemsActivity");
    }

    @Override
    public void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
