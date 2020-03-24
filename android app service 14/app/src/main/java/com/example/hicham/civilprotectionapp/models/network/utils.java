package com.example.hicham.civilprotectionapp.models.network;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;

import com.example.hicham.civilprotectionapp.utils.LogPrinter;

import java.net.InetAddress;

public class utils {

    private utils() {}

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public static boolean isGpsEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
