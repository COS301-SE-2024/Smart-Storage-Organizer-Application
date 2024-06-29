package com.example.smartstorageorganizer.utils;

public interface OperationCallback<T> {
    void onSuccess(T result);
    void onFailure(String error);
}