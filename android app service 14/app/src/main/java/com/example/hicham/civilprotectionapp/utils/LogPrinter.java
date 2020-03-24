package com.example.hicham.civilprotectionapp.utils;

import android.util.Log;

public class LogPrinter {
    private static final String TAG = "civilprotectionapp";
    private LogPrinter(){}

    public static void print(String text) {
        Log.i(TAG, text);
    }
}
