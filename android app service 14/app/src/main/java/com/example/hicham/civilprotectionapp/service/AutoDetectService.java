package com.example.hicham.civilprotectionapp.service;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.example.hicham.civilprotectionapp.BaseApp;
import com.example.hicham.civilprotectionapp.models.DataManager;
import com.example.hicham.civilprotectionapp.utils.Functions;
import com.example.hicham.civilprotectionapp.utils.Incident;
import com.example.hicham.civilprotectionapp.utils.LogPrinter;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.PendingResult;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

import java.util.ArrayList;

public class AutoDetectService extends Service implements SensorEventListener {
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLocation;
    private DatabaseReference mIncidentsRef;
    private DatabaseReference mGeofireRef;
    private DatabaseReference mIncidentChild;
    private GeoFire mGeoFire;
    private GeoQuery mGeoQuery;
    private ArrayList<String> mKeys;
    private Context mContext;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private static final int FORCE_THRESHOLD = 2000;
    private static final long TIME_THRESHOLD = 100;
    private static final long SHAKE_DURATION = 30000;
    private float mLastSpeedX = -1.0f;
    private float mLastSpeedY = -1.0f;
    private float mLastSpeedZ = -1.0f;
    private long mLastTime;
    private long mLastShakeTime;
    private long mLastRegionUpdateTime;
    GeoApiContext mGeoApiContext;
    DataManager mDataManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mIncidentsRef = FirebaseDatabase.getInstance().getReference("incidents");
        mGeofireRef = FirebaseDatabase.getInstance().getReference("geofire");
        mGeoFire = new GeoFire(mGeofireRef);
        mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        mKeys = new ArrayList<String>();
        initGoogleApiClientProvider();
        requestLocationUpdates();
        LogPrinter.print("Started the service ...........");
        mContext = this;

        mDataManager = ((BaseApp)getApplicationContext()).getDataManager();
        mGeoApiContext = new GeoApiContext.Builder()
                .apiKey("AIzaSyDZ-BxAEsDGoVpELrC4hA4-vvy94z3umuk")
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeLocationRequest();
        LogPrinter.print("Stopped the service ...........");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void initGoogleApiClientProvider() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult != null) {
                    mLocation = locationResult.getLastLocation();

                    if (mGeoQuery == null)
                        mGeoQuery = mGeoFire.queryAtLocation(new GeoLocation(mLocation.getLatitude(), mLocation.getLongitude()), 0.6);
                    mGeoQuery.setCenter(new GeoLocation(mLocation.getLatitude(), mLocation.getLongitude()));

                    mGeoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                        @Override
                        public void onKeyEntered(String key, GeoLocation location) {
                            final String currentKey = key;
                            mIncidentChild = mIncidentsRef.child(currentKey);

                            mIncidentChild.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Incident incident = dataSnapshot.getValue(Incident.class);

                                        if (incident.getIncidentType() == Functions.INCIDENT_TYPE_ACCIDENT && incident.getActive() == 1) {
                                            if (!mKeys.contains(currentKey)) {
                                                Functions.sendNotification(mContext, true);
                                                LogPrinter.print("Your entered the search area..........");
                                                mKeys.add(currentKey);
                                            }
                                        } else if (incident.getIncidentType() == Functions.INCIDENT_TYPE_ACCIDENT && incident.getActive() == 0 && mKeys.contains(currentKey)) {
                                            Functions.sendNotification(mContext, false);
                                            LogPrinter.print("Your no longer in the search area..........");
                                            mKeys.remove(currentKey);
                                        }
                                    }
                                    mIncidentChild.removeEventListener(this);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                        }

                        @Override
                        public void onKeyExited(String key) {
                            if (mKeys.contains(key)) {
                                Functions.sendNotification(mContext, false);
                                LogPrinter.print("Your no longer in the search area..........");
                                mKeys.remove(key);
                            }
                        }

                        @Override
                        public void onKeyMoved(String key, GeoLocation location) {}

                        @Override
                        public void onGeoQueryReady() {}

                        @Override
                        public void onGeoQueryError(DatabaseError error) {}
                    });

                    if (System.currentTimeMillis() - mLastRegionUpdateTime > 30000) {
                        GeocodingApi.reverseGeocode(mGeoApiContext,
                                new LatLng(mLocation.getLatitude(), mLocation.getLongitude())).setCallback(new PendingResult.Callback<GeocodingResult[]>() {
                            @Override
                            public void onResult(GeocodingResult[] result) {
                                if (result.length > 0) {
                                    mDataManager.setRegion(result[0].addressComponents[2].longName);
                                }
                            }

                            @Override
                            public void onFailure(Throwable e) {

                            }
                        });
                        mLastRegionUpdateTime = System.currentTimeMillis();
                    }
                }
            }
        };

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    public void requestLocationUpdates() {
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    public void removeLocationRequest() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        long mTimeLastUpdate = System.currentTimeMillis() - mLastTime;

        if (mTimeLastUpdate < TIME_THRESHOLD)
            return;

        float mSpeedNow = Math.abs(sensorEvent.values[0] + sensorEvent.values[1] + sensorEvent.values[2] - (mLastSpeedX + mLastSpeedY + mLastSpeedZ) / mTimeLastUpdate * 10000);
//        long mLastShakeTimeDiff = System.currentTimeMillis() - mLastShakeTime;
        LogPrinter.print("key .......... " + mSpeedNow);
        if (mSpeedNow > FORCE_THRESHOLD) {
//            mLastShakeTime = System.currentTimeMillis();
            Intent shakeIntent = new Intent();
            shakeIntent.setAction(Functions.SHAKE_DETECTED_BROADCAST);
            LocalBroadcastManager.getInstance(this).sendBroadcast(shakeIntent);
        }

        mLastTime = System.currentTimeMillis();
        mLastSpeedX = sensorEvent.values[0];
        mLastSpeedY = sensorEvent.values[1];
        mLastSpeedZ = sensorEvent.values[2];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}
}
