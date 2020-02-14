package com.example.myapplication.ui.Bookmarks;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BookmarksViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public BookmarksViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}