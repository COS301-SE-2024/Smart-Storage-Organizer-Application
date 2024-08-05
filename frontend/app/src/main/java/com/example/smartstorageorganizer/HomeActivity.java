package com.example.smartstorageorganizer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.airbnb.lottie.LottieAnimationView;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.cognito.result.AWSCognitoAuthSignOutResult;
import com.amplifyframework.core.Amplify;
import com.bumptech.glide.Glide;
import com.example.smartstorageorganizer.databinding.ActivityHomeBinding;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.UserUtils;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HomeActivity extends AppCompatActivity {
    public TextView fullName;
    public ShapeableImageView profileImage;
    public AppBarConfiguration mAppBarConfiguration;
    public ActivityHomeBinding binding;
    public String currentName, currentSurname, currentPicture;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarHome.toolbar);

        getDetails().thenAccept(getDetails->{
            Log.i("AuthDemo", "User is signed in");
        });

        DrawerLayout drawer = binding.drawerLayout;
        navigationView = binding.navView;
        ConstraintLayout logout = binding.logoutButton;
        LottieAnimationView buttonLoader = binding.buttonLoader;
        TextView logoutButtonText = binding.logOutButtonText;
        ImageView logoutButtonIcon = binding.logoutButtonIcon;

        View header = navigationView.getHeaderView(0);
        fullName = header.findViewById(R.id.fullName);
        profileImage = header.findViewById(R.id.profileImage);
//        fullName.setText("Ezekiel Makau");

        logout.setOnClickListener(v -> {
            buttonLoader.setVisibility(View.VISIBLE);
            buttonLoader.playAnimation();
            logoutButtonText.setVisibility(View.GONE);
            logoutButtonIcon.setVisibility(View.GONE);
            SignOut();
//                Snackbar.make(v, "Logging out", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null)
//                        .setAnchorView(R.id.fab).show();
        });

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile_management, R.id.nav_notifications, R.id.nav_help)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        getUserRole("ezemakau@gmail.com", "");

//        if (!isAdmin()) {
//            hideAdminMenuItems(navigationView.getMenu());
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private boolean isAdmin() {
        // Implement your logic to check if the user is an admin
        // For example, check shared preferences, a database, or an API
        return true; // Replace with actual logic
    }

    private void hideAdminMenuItems(Menu menu) {
        menu.findItem(R.id.nav_requests).setVisible(false);
        menu.findItem(R.id.nav_users).setVisible(false);
    }
    private void showAdminMenuItems(Menu menu) {
        menu.findItem(R.id.nav_requests).setVisible(true);
        menu.findItem(R.id.nav_users).setVisible(true);
    }

    public CompletableFuture<Boolean> getDetails() {
        CompletableFuture<Boolean> future=new CompletableFuture<>();

        Amplify.Auth.fetchUserAttributes(
                attributes -> {

                    for (AuthUserAttribute attribute : attributes) {
                        switch (attribute.getKey().getKeyString()) {
                            case "name":
                                currentName = attribute.getValue();
                                break;
                            case "family_name":
                                currentSurname = attribute.getValue();
                                break;
                            case "picture":
                                currentPicture = attribute.getValue();
                                break;
//                            case "address":
//                                currentAddress = attribute.getValue();
//                                break;
//                            case "custom:myCustomAttribute":
//                                customAttribute = attribute.getValue();
//                                break;
                        }




                    }
                    Log.i("progress","User attributes fetched successfully");
                    runOnUiThread(() -> {
                        Glide.with(this).load(currentPicture).placeholder(R.drawable.no_profile_image).error(R.drawable.no_profile_image).into(profileImage);
                        String username = currentName+" "+currentSurname;
                        fullName.setText(username);
                    });
                    future.complete(true);
                },
                error -> {Log.e("AuthDemo", "Failed to fetch user attributes.", error);  future.complete(false); }

        );
        return future;
    }

    public CompletableFuture<Boolean> SignOut()
    {
        CompletableFuture<Boolean> future=new CompletableFuture<>();
        Amplify.Auth.signOut(signOutResult -> {
            if (signOutResult instanceof AWSCognitoAuthSignOutResult.CompleteSignOut) {
                // Sign Out completed fully and without errors.
                Log.i("AuthQuickStart", "Signed out successfully");
                //move to a different page
                future.complete(true);
                runOnUiThread(() -> {
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                });
            } else if (signOutResult instanceof AWSCognitoAuthSignOutResult.PartialSignOut) {
                // Sign Out completed with some errors. User is signed out of the device.
                //move to the different page
                future.complete(true);
                runOnUiThread(() -> {
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                });

            } else if (signOutResult instanceof AWSCognitoAuthSignOutResult.FailedSignOut) {
                AWSCognitoAuthSignOutResult.FailedSignOut failedSignOutResult =
                        (AWSCognitoAuthSignOutResult.FailedSignOut) signOutResult;
                // Sign Out failed with an exception, leaving the user signed in.
                Log.e("AuthQuickStart", "Sign out Failed", failedSignOutResult.getException());
                //dont move to different page
                future.complete(false);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Sign Out Failed.", Toast.LENGTH_LONG).show();

                });
            }
        });
        return future;
    }

    private void getUserRole(String username, String authorization) {
        hideAdminMenuItems(navigationView.getMenu());
        UserUtils.getUserRole(username, authorization, this, new OperationCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (Objects.equals(result, "Manager") || Objects.equals(result, "Admin")) {
                    showAdminMenuItems(navigationView.getMenu());
                }
                else {
                    hideAdminMenuItems(navigationView.getMenu());
                }
            }

            @Override
            public void onFailure(String error) {
//                progressDialog.dismiss();
                hideAdminMenuItems(navigationView.getMenu());
                Toast.makeText(HomeActivity.this, "Getting user role failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void DeleteCategory(int id, String email)
    {
        String json = "{\"useremail\":\""+email+"\", \"id\":\""+ id +"\" }";
        Log.d("1Delete Item Payload", "JSON Payload: " + json);  // Log the JSON payload


        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.DeleteCategoryEndPoint;
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();
    }
    public void CategoryToUncategorized(int id)
    {
        String json = "{\"parentcategoryid\":\""+Integer.toString(id)+"\" }";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.CategoryToUncategorized;
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();
    }
    public void SubcategoryToUncategorized(int id)
    {
        String json = "{\"subcategoryid\":\""+Integer.toString(id)+"\" }";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.SubcategoryToUncategorized;
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();
    }
    public void ModifyCategoryName(int id, String NewCategoryName)
    {
        String json = "{\"id\":\""+Integer.toString(id)+"\", \"categoryname\":\""+NewCategoryName+"\" }";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.ModifyCategoryName;
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();
    }

    public void FetchAllItems(int HowMany, int PageNumber)
    {
        String json = "{\"limit\":\""+Integer.toString(HowMany)+"\", \"offset\":\""+Integer.toString(PageNumber)+"\" }";


        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.FetchAllEndPoint;
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();
    }

    public void AddColourGroup(String colourcode, String title, String description)
    {
        String json = "{\"colourcode\":\""+colourcode+"\", \"description\":\""+description+"\", \"title\":\""+title+"\" }";


        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.AddColourEndPoint;
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();
    }

    public void AddItemToColour(int colourid, int itemid)
    {
        String json = "{\"colourid\":\""+Integer.toString(colourid)+"\", \"itemid\":\""+Integer.toString(itemid)+"\" }";


        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.AddItemToColourEndPoint;
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();
    }

    public void FetchByColour(int colourid)
    {
        String json = "{\"colourid\":\""+Integer.toString(colourid)+"\"}";


        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.FetchByColourEndPoint;
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();
    }

    public void FetchAllColour()
    {
        String json = "{}";


        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.FetchByColourEndPoint;
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(API_URL)
                .get()
                .build();
    }

    public void DeleteColour(int colourid)
    {
        String json = "{\"colourid\":\""+Integer.toString(colourid)+"\"}";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.DeleteColour;
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();
    }
    public void FetchByID(int id)
    {
        String json = "{\"item_id\":\""+Integer.toString(id)+"\"}";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.FetchByIDEndPoint;
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();
    }

    public void RecommendMultiple(String id)
    {
        String json = "{\"id\":\""+id+"\"}";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.RecommendMultipleEndPoint;
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();
    }
    public void CreateCategoryAI(String parentcategory, String item_name, String description)
    {
        String json = "{\"parentcategory\":\""+parentcategory+"\", \"description\":\""+description+"\", \"item_name\":\""+item_name+"\" }";


        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.CreateCategoryAIEndPoint;
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();
    }

    public void FetchUncategorized(int HowMany, int PageNumber)
    {
        String json = "{\"limit\":\""+Integer.toString(HowMany)+"\", \"offset\":\""+Integer.toString(PageNumber)+"\" }";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String API_URL = BuildConfig.FetchUncategorizedEndPoint;
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();
    }

//    public void GenerateQrcode(int id)
//    {
//        String json = "{\"data\":\""+Integer.toString(id)+"\"}";
//
//        MediaType JSON = MediaType.get("application/json; charset=utf-8");
//        OkHttpClient client = new OkHttpClient();
//        String API_URL = BuildConfig.GenerateQrcode;
//        RequestBody body = RequestBody.create(json, JSON);
//
//        Request request = new Request.Builder()
//                .url(API_URL)
//                .post(body)
//                .build();
//    }
}

