package com.example.smartstorageorganizer;

import static androidx.media.session.MediaButtonReceiver.handleIntent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
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
import com.example.smartstorageorganizer.model.ColorCodeModel;
import com.example.smartstorageorganizer.model.OrganizationModel;
import com.example.smartstorageorganizer.utils.OperationCallback;
import com.example.smartstorageorganizer.utils.OrganizationUtils;
import com.example.smartstorageorganizer.utils.UserUtils;
import com.example.smartstorageorganizer.utils.Utils;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HomeActivity extends AppCompatActivity {
    public TextView fullName, organizationName;
    public ShapeableImageView profileImage;
    public AppBarConfiguration mAppBarConfiguration;
    public ActivityHomeBinding binding;
    public String currentName, currentSurname, currentPicture, organizationId;
    NavigationView navigationView;
    ImageButton searchButton;
    MyAmplifyApp app;
//    MyAmplifyApp app = (MyAmplifyApp) getApplicationContext();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (MyAmplifyApp) getApplicationContext();

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
        organizationName = header.findViewById(R.id.organizationName);
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
        
        findViewById(R.id.search_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(intent);
            }
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

        if(Objects.equals(app.getUserRole(), "")){
            getUserRole(getIntent().getStringExtra("email"), "");
        }
        else {
            if (Objects.equals(app.getUserRole(), "Manager")) {
                showAdminMenuItems(navigationView.getMenu());
            }
            else {
                hideAdminMenuItems(navigationView.getMenu());
            }
        }
//        FirebaseApp.initializeApp(this);
//
//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(task -> {
//                    if (!task.isSuccessful()) {
//                        Log.w("HomeActivity", "Fetching FCM registration token failed", task.getException());
//                        return;
//                    }
//
//                    String token = task.getResult();
//                    Log.d("HomeActivity", "FCM Registration Token: " + token);
//                });

//        if (!isAdmin()) {
//            hideAdminMenuItems(navigationView.getMenu());
//        }
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null && "nav_notifications".equals(intent.getStringExtra("navigateTo"))) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
            navController.navigate(R.id.nav_notifications);
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.home, menu);
//
//
//        MenuItem searchItem = menu.findItem(R.id.searchButton);
//        searchItem.setOnMenuItemClickListener(item -> {
//            // Start the SearchActivity
//            Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
//            startActivity(intent);
//            return true;
//        });
//        return true;
//    }

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
                            case "address":
                                organizationId = attribute.getValue();
                                break;
//                            case "custom:myCustomAttribute":
//                                customAttribute = attribute.getValue();
//                                break;
                        }




                    }
                    Log.i("progress","User attributes fetched successfully");
                    runOnUiThread(() -> {
                        app.setOrganizationID(organizationId);
                        String id = app.getOrganizationID();

                        fetchOrganizationDetails(id);
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
                future.complete(true);
                runOnUiThread(() -> {
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                });
            } else if (signOutResult instanceof AWSCognitoAuthSignOutResult.PartialSignOut) {
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
                app.setUserRole(result);
                if (Objects.equals(result, "Manager")) {
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

    private void fetchOrganizationDetails(String organizationId) {
        OrganizationUtils.fetchOrganizationDetails(organizationId, this, new OperationCallback<OrganizationModel>() {
            @Override
            public void onSuccess(OrganizationModel result) {
                organizationName.setText(result.getOrganizationName().toUpperCase());
                Toast.makeText(HomeActivity.this, "organization fetched successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
//                loadingScreen.setVisibility(View.GONE);
                Toast.makeText(HomeActivity.this, "Failed to fetch items: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

