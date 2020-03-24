package com.example.hicham.civilprotectionapp.models;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {
    public static final String PREF_ACTION = "PREF_ACTION";
    public static final String PREF_AUTO_CALL = "PREF_AUTO_CALL";
    public static final String PREF_PHONE_NUMBER = "PREF_PHONE_NUMBER";
    public static final String PREF_DEVICE_ID = "PREF_DEVICE_ID";
    public static final String PREF_REGION = "PREF_REGION";
    private SharedPreferences mSharedPreferences;

    public SharedPreferencesHelper(Context context) {
        mSharedPreferences = context.getSharedPreferences(PREF_ACTION, Context.MODE_PRIVATE);
    }

    public void clear() {
        mSharedPreferences.edit().clear().apply();
    }

    public void setNotifyTime(String buttonId, long time) {
        mSharedPreferences.edit().putLong(buttonId, time).apply();
    }

    public void setAutoCall(Boolean autoCall) {
        mSharedPreferences.edit().putBoolean(PREF_AUTO_CALL, autoCall).apply();
    }

    public long getNotifyTime(String buttonId) {
        return mSharedPreferences.getLong(buttonId, 0);
    }

    public boolean getAutoCall() {
        return mSharedPreferences.getBoolean(PREF_AUTO_CALL, false);
    }

    public void setPhoneNumber(String phoneNumber) {
        mSharedPreferences.edit().putString(PREF_PHONE_NUMBER, phoneNumber).apply();
    }

    public String getPhoneNumber() {
        return mSharedPreferences.getString(PREF_PHONE_NUMBER, null);
    }

    public void setDeviceId(String deviceId) {
        mSharedPreferences.edit().putString(PREF_DEVICE_ID, deviceId).apply();
    }

    public String getDeviceId() {
        return mSharedPreferences.getString(PREF_DEVICE_ID, null);
    }

    public void setRegion(String region) {
        mSharedPreferences.edit().putString(PREF_REGION, region).apply();
    }

    public String getRegion() {
        return mSharedPreferences.getString(PREF_REGION, "Algeria Province");
    }
}
