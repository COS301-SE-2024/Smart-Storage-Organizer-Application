package com.example.smartstorageorganizer.ui.help;

import androidx.lifecycle.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class HelpViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public HelpViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Edit Profile fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}