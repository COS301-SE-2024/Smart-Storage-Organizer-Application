package com.example.smartstorageorganizer.ui.home;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.TextView;


import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.StoragePath;
import com.amplifyframework.storage.options.StorageUploadFileOptions;
import com.example.smartstorageorganizer.AddColorCodeActivity;
import com.example.smartstorageorganizer.adapters.CategoryAdapter;
import com.example.smartstorageorganizer.AddCategoryActivity;
import com.example.smartstorageorganizer.HomeActivity;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.UnitActivity;
import com.example.smartstorageorganizer.adapters.ItemAdapter;
import com.example.smartstorageorganizer.databinding.FragmentHomeBinding;
import com.example.smartstorageorganizer.model.ItemModel;
import com.example.smartstorageorganizer.model.CategoryModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class HomeFragment extends Fragment {

    Spinner suggestionSpinner, colorSpinner;
    List<CategoryModel> suggestedCategory = new ArrayList<>();
    int PICK_IMAGE_MULTIPLE = 1;
    private static final int GALLERY_CODE = 1;
    private String currentSelectedCategory, currentSelectedSubcategory;
    Uri ImageUri;
    File file;
    List<String> imagesEncodedList;
    private Spinner parentSpinner, subcategorySpinner;

    LottieAnimationView fetchItemsLoader;
    RecyclerView.LayoutManager layoutManager;
    private TextView name;
    private FragmentHomeBinding binding;
    private List<ItemModel> itemModelList;
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> subAdapter;
    private ItemAdapter itemAdapter;
    private RecyclerView itemRecyclerView, category_RecyclerView;
    private String currentEmail, currentName;
    AlertDialog alertDialog;
    private List<CategoryModel> categoryModelList;
    private CategoryAdapter categoryAdapter;
    private String parentCategoryId, subcategoryId;
    Button buttonNext;
    EditText itemDescription, itemName;
    ImageView itemImage;
    private List<String> parentCategories = new ArrayList<>();
    private List<String> subCategories = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        getDetails().thenAccept(getDetails-> Log.i("AuthDemo", "User is signed in"));

        FloatingActionButton addItemButton = root.findViewById(R.id.addItemButton);
        itemRecyclerView = root.findViewById(R.id.item_rec);
        fetchItemsLoader = root.findViewById(R.id.fetchItemsLoader);
        category_RecyclerView = root.findViewById(R.id.category_rec);
        name = root.findViewById(R.id.name);

        category_RecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        categoryModelList = new ArrayList<>();

        categoryAdapter = new CategoryAdapter(requireActivity(), categoryModelList);
        category_RecyclerView.setAdapter(categoryAdapter);

        itemRecyclerView.setHasFixedSize(true);
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        itemRecyclerView.setLayoutManager(layoutManager);

        addItemButton.setOnClickListener(v -> showAddButtonPopup());

        itemModelList = new ArrayList<>();
        itemAdapter = new ItemAdapter(requireActivity(), itemModelList);
        itemRecyclerView.setAdapter(itemAdapter);

        return root;
    }

    private void loadRecentItems(String email) {
        Utils.fetchRecentItems(email,requireActivity(), new OperationCallback<List<ItemModel>>() {
            @Override
            public void onSuccess(List<ItemModel> result) {
                itemModelList.clear();
                itemModelList.addAll(result);
                itemAdapter.notifyDataSetChanged();
                fetchItemsLoader.setVisibility(View.GONE);
                itemRecyclerView.setVisibility(View.VISIBLE);
                Toast.makeText(requireActivity(), "Items fetched successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                fetchItemsLoader.setVisibility(View.GONE);
                itemRecyclerView.setVisibility(View.VISIBLE);
                Toast.makeText(requireActivity(), "Failed to fetch items: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private CompletableFuture<Boolean> getDetails()
    {
        CompletableFuture<Boolean> future=new CompletableFuture<>();

        Amplify.Auth.fetchUserAttributes(
                attributes -> {

                    for (AuthUserAttribute attribute : attributes) {
                        switch (attribute.getKey().getKeyString()) {
                            case "email":
                                currentEmail = attribute.getValue();
                                break;
                            case "name":
                                currentName = attribute.getValue();
                                break;
                            default:
                        }
                    }
                    Log.i("progress","User attributes fetched successfully");
                    requireActivity().runOnUiThread(() -> {
                        name.setText("Hi "+currentName);
                        loadRecentItems(currentEmail);
                        fetchParentCategories(0, currentEmail);
                    });
                    future.complete(true);
                },
                error -> Log.e("AuthDemo", "Failed to fetch user attributes.", error)

        );
        return future;
    }

    private void showAddItemPopup()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_item_popup, null);
        builder.setView(dialogView);

        itemImage  = dialogView.findViewById(R.id.item_image);
        itemName = dialogView.findViewById(R.id.item_name);
        itemDescription = dialogView.findViewById(R.id.item_description);
        buttonNext = dialogView.findViewById(R.id.button_next_item);

        alertDialog = builder.create();

        itemImage.setOnClickListener(v -> OpenGallery());

        buttonNext.setOnClickListener(v -> {
            showSuggestionPopup(itemName.getText().toString(), itemDescription.getText().toString());
            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    private void showSuggestionPopup(String itemName, String itemDescription) {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.category_suggestion_popup, null);
        builder.setView(dialogView);

        suggestionSpinner = dialogView.findViewById(R.id.suggestionSpinner);
        colorSpinner = dialogView.findViewById(R.id.colorSpinner);
        Button addButton = dialogView.findViewById(R.id.button_add_item);

        dialog = builder.create();

        getSuggestedCategory(itemName, itemDescription);

        addButton.setOnClickListener(v -> uploadItemImage(file));

        dialog.show();
    }

    private void getSuggestedCategory(String itemName, String itemDescription) {
        Utils.fetchCategorySuggestions(itemName, itemDescription, currentEmail, requireActivity(), new OperationCallback<List<CategoryModel>>() {
            @Override
            public void onSuccess(List<CategoryModel> result) {
                suggestedCategory.clear();
                suggestedCategory.addAll(result);
                parentCategoryId = suggestedCategory.get(0).getCategoryID();
                subcategoryId = suggestedCategory.get(1).getCategoryID();
                List<String> categories = new ArrayList<>();
                categories.add(suggestedCategory.get(0).getCategoryName()+ " - "+suggestedCategory.get(1).getCategoryName());
                categories.add("Add Custom Category");

                // Populate the Spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                suggestionSpinner.setAdapter(adapter);

                suggestionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        if (categories.get(position).equals("Add Custom Category"))
                        {
                            // Show dialog to input custom category
                            showCustomCategoryDialog(categories, adapter);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(requireActivity(), "Category Fetching failed... ", Toast.LENGTH_LONG).show();

            }
        });
    }
    private void showCustomCategoryDialog(List<String> categories, ArrayAdapter<String> adapter2) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_custom_category_popup, null);

        parentSpinner = dialogView.findViewById(R.id.ParentSpinner);
        subcategorySpinner = dialogView.findViewById(R.id.subcategorySpinner);

        builder.setView(dialogView);
        adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, parentCategories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        parentSpinner.setAdapter(adapter);

        parentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get selected item
                currentSelectedCategory = parentView.getItemAtPosition(position).toString();
                String parentCategory = "";
                parentCategory = findCategoryByName(currentSelectedCategory);
                parentCategoryId = parentCategory;
                if(!Objects.equals(parentCategory, "")){
                    parentCategoryId = parentCategory;
                    fetchParentCategories(Integer.parseInt(parentCategory), currentEmail);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do something here if nothing is selected
            }
        });
        subcategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                currentSelectedSubcategory = parentView.getItemAtPosition(position).toString();
                String subCategory = findCategoryByName(currentSelectedSubcategory);
                subcategoryId = subCategory;
                if(!Objects.equals(subCategory, "")) {
                    subcategoryId = subCategory;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do something here if nothing is selected
            }
        });
        builder.setPositiveButton("Add", (dialog, which) ->
        {
            categories.add(categories.size() - 1, currentSelectedCategory+" - "+currentSelectedSubcategory); // Add before "Add Custom Category"
            adapter2.notifyDataSetChanged();
            suggestionSpinner.setSelection(categories.indexOf(currentSelectedCategory+" - "+currentSelectedSubcategory));
            Toast.makeText(requireActivity(), "Selected: " + currentSelectedCategory+" - "+currentSelectedSubcategory, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showAddButtonPopup() {
        AlertDialog alertDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_button_popup, null);
        builder.setView(dialogView);

        Button addItemButton = dialogView.findViewById(R.id.addItemButton);
        Button addCategoryButton = dialogView.findViewById(R.id.addCategoryButton);
        Button addUnitButton = dialogView.findViewById(R.id.addUnitButton);
        Button addColorButton = dialogView.findViewById(R.id.addColorButton);

        alertDialog = builder.create();

        addItemButton.setOnClickListener(v -> {

            showAddItemPopup();
            alertDialog.dismiss();
        });
        addCategoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddCategoryActivity.class);
            intent.putExtra("email", currentEmail);
            startActivity(intent);
        });
        addUnitButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UnitActivity.class);
            startActivity(intent);
        });
        addColorButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddColorCodeActivity.class);
            intent.putExtra("email", currentEmail);
            startActivity(intent);
        });

        // Show the AlertDialog
        alertDialog.show();
    }

    private void addItem(String itemImage, String itemName, String description, int category, int parentCategory) {
        Utils.postAddItem(itemImage, itemName, description, category, parentCategory, currentEmail, requireActivity(), new OperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                Toast.makeText(requireActivity(), "Item Added Successfully ", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(requireActivity(), HomeActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(requireActivity(), "Category Fetching failed... ", Toast.LENGTH_LONG).show();

            }
        });
    }

    private String findCategoryByName(String categoryName) {
        for (CategoryModel category : categoryModelList) {
            if (category.getCategoryName().equalsIgnoreCase(categoryName)) {
                requireActivity().runOnUiThread(() -> {
                    Log.e("Filtering: ", category.getCategoryID());
                });
                return category.getCategoryID();
            }
        }
        return ""; // Return null if no category with the given name is found
    }

    private void fetchParentCategories(int categoryId, String email) {
        Utils.fetchParentCategories(categoryId, email, requireActivity(), new OperationCallback<List<CategoryModel>>() {
            @Override
            public void onSuccess(List<CategoryModel> result) {
                if(categoryId != 0) {
                    for (CategoryModel category : result) {
                        subCategories.add(category.getCategoryName());
                    }
                    subAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, subCategories);
                    subAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    subcategorySpinner.setAdapter(subAdapter);
                }
                else {
                    categoryModelList.clear();
                    categoryModelList.addAll(result);
                    categoryAdapter.notifyDataSetChanged();
                    for (CategoryModel category : categoryModelList) {
                        parentCategories.add(category.getCategoryName());
                    }
                    adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, parentCategories);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                }
            }

            @Override
            public void onFailure(String error) {
//                showToast("Failed to fetch categories: " + error);
                Toast.makeText(requireActivity(), "Category Fetching failed... ", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        //startActivityForResult(galleryIntent, GalleryPick);
        startActivityForResult(Intent.createChooser(galleryIntent,"Select Picture"), PICK_IMAGE_MULTIPLE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK  && data != null) {

            imagesEncodedList = new ArrayList<>();

            if (data.getData() != null) {
                ImageUri = data.getData();
                itemImage.setImageURI(ImageUri);
                BitmapDrawable drawable = (BitmapDrawable) itemImage.getDrawable();
                Bitmap bitmap = drawable.getBitmap();

                // Create a file to save the image
                file = new File(requireActivity().getCacheDir(), "image.jpeg");
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            else if (data.getClipData() != null) {
                ImageUri = data.getClipData().getItemAt(0).getUri();
                itemImage.setImageURI(ImageUri);
                BitmapDrawable drawable = (BitmapDrawable) itemImage.getDrawable();
                Bitmap bitmap = drawable.getBitmap();

                // Create a file to save the image
                file = new File(requireActivity().getCacheDir(), "image.jpeg");
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void uploadItemImage(File parentCategoryImage)
    {
        StorageUploadFileOptions options = StorageUploadFileOptions.builder()
                .contentType("image/png") // Adjust based on file type
                .build();
        long time = System.nanoTime();
        String key= String.valueOf(time);
        String path="public/ItemImages/"+key+".png";
        Amplify.Storage.uploadFile(
                StoragePath.fromString(path),
                parentCategoryImage,
                options,
                result -> Log.i("MyAmplifyApp", "Successfully uploaded: " + getObjectUrl(key)),
                storageFailure -> {Log.e("MyAmplifyApp", "Upload failed", storageFailure);}
        );
    }
    public String getObjectUrl(String key)
    {
        String url = "https://smart-storage-f0629f0176059-staging.s3.eu-north-1.amazonaws.com/public/ItemImages/"+key+".png";
        addItem(url, itemName.getText().toString().trim(), itemDescription.getText().toString().trim(), Integer.parseInt(subcategoryId), Integer.parseInt(parentCategoryId));

        return "https://smart-storage-f0629f0176059-staging.s3.eu-north-1.amazonaws.com/public/ItemImages/"+key+".png";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}