package com.example.hicham.civilprotectionapp.models;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.hicham.civilprotectionapp.models.network.utils;
import com.example.hicham.civilprotectionapp.utils.Functions;
import com.example.hicham.civilprotectionapp.utils.Incident;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DataManager {
    private SharedPreferencesHelper mSharedPreferencesHelper;
    private DatabaseReference mIncidents;
    private GeoFire mGeoFire;
    public DataManager(SharedPreferencesHelper sharedPreferencesHelper) {
        mSharedPreferencesHelper = sharedPreferencesHelper;
        mIncidents = FirebaseDatabase.getInstance().getReference("incidents");
        DatabaseReference geoFireReference = FirebaseDatabase.getInstance().getReference("geofire");
        mGeoFire = new GeoFire(geoFireReference);
    }

    public void clear() {
        mSharedPreferencesHelper.clear();
    }

    public void setNotifyTime(String buttonId, long time) {
        mSharedPreferencesHelper.setNotifyTime(buttonId, time);
    }

    public void setAutoCall(Boolean autoCall) {
        mSharedPreferencesHelper.setAutoCall(autoCall);
    }

    public long getNotifyTime(String buttonId) {
        return mSharedPreferencesHelper.getNotifyTime(buttonId);
    }

    public Boolean getAutoCall() {
        return mSharedPreferencesHelper.getAutoCall();
    }

    public void loadNotificationToDatabase(final Incident incident, final Context context) {
        String pushKey = mIncidents.push().getKey();
        if (incident.getIncidentType() == Functions.INCIDENT_TYPE_ACCIDENT)
            mGeoFire.setLocation(pushKey, new GeoLocation(incident.getLatitude(), incident.getLongitude()), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {}
            });
        mIncidents.child(pushKey).setValue(incident).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mSharedPreferencesHelper.setNotifyTime(String.valueOf(incident.getIncidentType()), System.currentTimeMillis());
                Toast.makeText(context, "Notification has been sent", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failure while sending notification", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void incidentsListener(ChildEventListener childEventListener) {
        mIncidents.addChildEventListener(childEventListener);
    }

    public boolean isInternetAvailable(Context context) {
        return utils.isInternetAvailable(context);
    }

    public boolean isGpsEnabled(Context context) {
        return utils.isGpsEnabled(context);
    }

    public void setPhoneNumber(String phoneNumber) {
        mSharedPreferencesHelper.setPhoneNumber(phoneNumber);
    }

    public String getPhoneNumber() {
        return mSharedPreferencesHelper.getPhoneNumber();
    }

    public void setDeviceId(String deviceId) {
        mSharedPreferencesHelper.setDeviceId(deviceId);
    }

    public String getDeviceId() {
        return mSharedPreferencesHelper.getDeviceId();
    }

    public void setRegion(String region) {
        mSharedPreferencesHelper.setRegion(region);
    }

    public String getRegion() {
        return mSharedPreferencesHelper.getRegion();
    }
}
