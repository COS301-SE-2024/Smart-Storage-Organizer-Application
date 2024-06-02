package com.example.smartstorageorganizer.ui.profile_management;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.cognito.result.AWSCognitoAuthSignOutResult;
import com.amplifyframework.core.Amplify;
import com.example.smartstorageorganizer.EditProfileActivity;
import com.example.smartstorageorganizer.LoginActivity;
import com.example.smartstorageorganizer.R;
import com.example.smartstorageorganizer.databinding.FragmentProfileManagementBinding;

import java.util.concurrent.CompletableFuture;

public class ProfileManagementFragment extends Fragment {
    private TextView email, username;
    ConstraintLayout content;
    LottieAnimationView loadingScreen;
    private String currentEmail, currentName, currentSurname;
    private FragmentProfileManagementBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileManagementViewModel profileManagementViewModelViewModel =
                new ViewModelProvider(this).get(ProfileManagementViewModel.class);

        binding = FragmentProfileManagementBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        email = root.findViewById(R.id.email);
        username = root.findViewById(R.id.username);
        content = root.findViewById(R.id.content);
        loadingScreen = root.findViewById(R.id.loadingScreen);

        getDetails().thenAccept(getDetails->{
            Log.i("AuthDemo", "User is signed in");
            Log.i("AuthEmailFragment", currentEmail);
        });

        AppCompatButton editProfileButton = root.findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    private CompletableFuture<Boolean> getDetails() {
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
//                            case "phone_number":
//                                currentPhone = attribute.getValue();
//                                break;
//                            case "address":
//                                currentAddress = attribute.getValue();
//                                break;
//                            case "custom:myCustomAttribute":
//                                customAttribute = attribute.getValue();
//                                break;
                        }




                    }
                    Log.i("progress","User attributes fetched successfully");
                    Log.i("progressEmail",currentEmail);
                    requireActivity().runOnUiThread(() -> {
                        email.setText(currentEmail);
                        String fullName = currentName+" "+currentSurname;
                        username.setText(fullName);
                        loadingScreen.setVisibility(View.GONE);
                        loadingScreen.pauseAnimation();
                        content.setVisibility(View.VISIBLE);
                    });
                    future.complete(true);
                },
                error -> Log.e("AuthDemo", "Failed to fetch user attributes.", error)

        );
        return future;
    }

    public void SignOut()
    {
        Amplify.Auth.signOut(signOutResult -> {
            if (signOutResult instanceof AWSCognitoAuthSignOutResult.CompleteSignOut) {
                // Sign Out completed fully and without errors.
                Log.i("AuthQuickStart", "Signed out successfully");
                //move to a different page
                requireActivity().runOnUiThread(() -> {
                    Intent intent = new Intent(requireActivity(), LoginActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                });
            } else if (signOutResult instanceof AWSCognitoAuthSignOutResult.PartialSignOut) {
                // Sign Out completed with some errors. User is signed out of the device.
                AWSCognitoAuthSignOutResult.PartialSignOut partialSignOutResult =
                        (AWSCognitoAuthSignOutResult.PartialSignOut) signOutResult;
                //move to the different page
                requireActivity().runOnUiThread(() -> {
                    Intent intent = new Intent(requireActivity(), LoginActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                });

            } else if (signOutResult instanceof AWSCognitoAuthSignOutResult.FailedSignOut) {
                AWSCognitoAuthSignOutResult.FailedSignOut failedSignOutResult =
                        (AWSCognitoAuthSignOutResult.FailedSignOut) signOutResult;
                // Sign Out failed with an exception, leaving the user signed in.
                Log.e("AuthQuickStart", "Sign out Failed", failedSignOutResult.getException());
                //dont move to different page
                requireActivity().runOnUiThread(() -> {
//                    requireActivity().Toast.makeText(this, "Sign Out Failed.", Toast.LENGTH_LONG).show();

                });
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}