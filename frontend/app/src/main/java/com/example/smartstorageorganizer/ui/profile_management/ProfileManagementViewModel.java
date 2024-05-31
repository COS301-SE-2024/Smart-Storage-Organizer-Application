package com.example.smartstorageorganizer.ui.profile_management;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileManagementViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ProfileManagementViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Profile Management fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}