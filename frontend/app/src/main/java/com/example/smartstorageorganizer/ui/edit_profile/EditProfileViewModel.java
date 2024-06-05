package com.example.smartstorageorganizer.ui.edit_profile;

import androidx.lifecycle.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class EditProfileViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public EditProfileViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Edit Profile fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}