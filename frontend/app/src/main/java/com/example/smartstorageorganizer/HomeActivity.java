package com.example.smartstorageorganizer;

import static androidx.media.session.MediaButtonReceiver.handleIntent;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HomeActivity extends BaseActivity  {
    public TextView fullName, organizationName;
    public ShapeableImageView profileImage;
    public AppBarConfiguration mAppBarConfiguration;
    public ActivityHomeBinding binding;
    public String currentEmail, currentName, currentSurname, currentPicture, organizationId;
    NavigationView navigationView;
    ImageButton searchButton;
    MyAmplifyApp app;
    private long startTime;
    private static final String CHANNEL_ID = "AppExitServiceChannel";

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (MyAmplifyApp) getApplicationContext();

        // Update user's last active time when the app opens
//        updateActiveUser();

        if (!isServiceRunning(AppTerminationService.class)) {
            startAppExitService();
        }

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
                FragmentManager fragmentManager = getSupportFragmentManager();
                NavHostFragment navHostFragment = (NavHostFragment) fragmentManager.findFragmentById(R.id.nav_host_fragment_content_home);
                if (navHostFragment != null) {
                    Fragment currentFragment = navHostFragment.getChildFragmentManager().getPrimaryNavigationFragment();
                    if (currentFragment != null) {
                        String fragmentName = currentFragment.getClass().getSimpleName();
                        Log.d("CurrentFragment", "Active Fragment: " + fragmentName);
                        logUserFlow(fragmentName, "SearchActivity");
                    }
                }


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
                            case "email":
                                currentEmail = attribute.getValue();
                                break;
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
                        app.setName(currentName);
                        app.setEmail(currentEmail);
                        app.setSurname(currentSurname);
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
                    Date currentDate = new Date();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = dateFormat.format(currentDate);
                    loginActivities(currentEmail, currentName, currentSurname, "sign_out", organizationId, formattedDate);
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                });
            } else if (signOutResult instanceof AWSCognitoAuthSignOutResult.PartialSignOut) {
                future.complete(true);
                runOnUiThread(() -> {
                    Date currentDate = new Date();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = dateFormat.format(currentDate);
                    loginActivities(currentEmail, currentName, currentSurname, "sign_out", organizationId, formattedDate);
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

    public void loginActivities(String email, String name, String surname, String type, String organization_id, String time) {
        UserUtils.loginActivities(email, name, surname, type, organization_id, time, this, new OperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {

//                Toast.makeText(HomeActivity.this, "Login Activities Failed to Save"+ result, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(String error) {
//                hideLoading();
//                loginActivities(email, name, surname, "sign_out", organization_id, time);
                Toast.makeText(HomeActivity.this, "Login Activities Failed to Save", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startAppExitService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the notification channel
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "App Exit Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }

        // Create a notification for the foreground service
        Intent notificationIntent = new Intent(this, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("App is running")
                .setContentText("Tracking app exit...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();

        Intent serviceIntent = new Intent(this, AppTerminationService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        }
    }

    private void logActivityView(String activityName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = getIntent().getStringExtra("email");

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
        String userId = getIntent().getStringExtra("email");

        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("user_id", userId);
        sessionData.put("activity_name", activityName);
        sessionData.put("session_duration", sessionDuration); // Duration in milliseconds

        db.collection("activity_sessions")
                .add(sessionData)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "Session duration logged."))
                .addOnFailureListener(e -> Log.w("Firestore", "Error logging session duration", e));
    }

    public void logUserFlow(String fromActivity, String toActivity) {
        long sessionDuration = System.currentTimeMillis() - startTime;
        logSessionDuration(fromActivity, (sessionDuration));
        long transitionTime = System.currentTimeMillis();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = getIntent().getStringExtra("email");

        Map<String, Object> userFlowData = new HashMap<>();
        userFlowData.put("user_id", userId);
        userFlowData.put("previous_activity", fromActivity);
        userFlowData.put("next_activity", toActivity);
        userFlowData.put("transition_time", new Timestamp(new Date(transitionTime)));

        db.collection("user_flow")
                .add(userFlowData)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "User flow logged."))
                .addOnFailureListener(e -> Log.w("Firestore", "Error logging user flow", e));
    }

    @Override
    protected void onStart() {
        super.onStart();
//        logActivityView("HomeActivity");
    }

    @Override
    protected void onResume() {
        super.onResume();
//        startTime = System.currentTimeMillis();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        long sessionDuration = System.currentTimeMillis() - startTime;
//        logSessionDuration("HomeActivity", (sessionDuration));
//        long transitionTime = System.currentTimeMillis();
//        logUserFlow("HomeActivity", "ReportsActivity", transitionTime);
    }

}

