package com.example.hicham.civilprotectionapp;

import android.app.Application;

import com.example.hicham.civilprotectionapp.models.DataManager;
import com.example.hicham.civilprotectionapp.models.SharedPreferencesHelper;

public class BaseApp extends Application {
    private DataManager mDataManager;
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(getApplicationContext());
        mDataManager = new DataManager(sharedPreferencesHelper);
    }

    public DataManager getDataManager() {
        return mDataManager;
    }
}
