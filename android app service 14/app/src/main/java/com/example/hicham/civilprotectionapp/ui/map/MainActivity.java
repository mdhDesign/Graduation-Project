package com.example.hicham.civilprotectionapp.ui.map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.hicham.civilprotectionapp.BaseApp;
import com.example.hicham.civilprotectionapp.R;
import com.example.hicham.civilprotectionapp.models.DataManager;
import com.example.hicham.civilprotectionapp.service.AutoDetectService;
import com.example.hicham.civilprotectionapp.ui.counter.CounterActivity;
import com.example.hicham.civilprotectionapp.utils.Functions;
import com.example.hicham.civilprotectionapp.utils.Incident;
import com.example.hicham.civilprotectionapp.utils.LogPrinter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity implements MainMvpView, View.OnClickListener {
    LinearLayout llBtnsContainer;
    RelativeLayout rlMapContainer;
    SupportMapFragment supportMapFragment;
    MainPresenter mainPresenter;
    private Boolean mMapReady = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private Location mLocation;
    private GoogleMap mGoogleMap;
    private Marker mMyPositionMarker;
    private BitmapDescriptor mBitmapDescriptor;
    RelativeLayout rlCountDownLayout;
    Button btnFire;
    Button btnElectricity;
    Button btnPatient;
    Button btnAccident;
    SwitchCompat scAutoNotify;

    private ShakeDetectionReceiver mShakeDetectionReceiver;
    private Context mContext;


    private static final long REREIVER_CALL_DURATION = 30000;
    private long mLastReceiverCallTime;


    private HashMap<String, Circle> mCircleHashMap;
    private HashMap<String, Marker> mMarkerHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        mCircleHashMap = new HashMap<>();
        mMarkerHashMap = new HashMap<>();

        // Initialize Presenter
        DataManager dataManager = ((BaseApp)getApplicationContext()).getDataManager();
        mainPresenter = new MainPresenter(dataManager);
        mainPresenter.onAttach(this);
        mainPresenter.initGoogleApiClientProvider();

        llBtnsContainer = findViewById(R.id.ll_btns_container);
        rlMapContainer = findViewById(R.id.rl_map_container);
        llBtnsContainer.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View view, View view1) {
                rlMapContainer.setPadding(0, 0, 0, llBtnsContainer.getHeight());
            }
        });

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
        supportMapFragment.getMapAsync(mainPresenter);

        btnFire = findViewById(R.id.btn_fire);
        btnElectricity = findViewById(R.id.btn_electricity);
        btnPatient = findViewById(R.id.btn_patient);
        btnAccident = findViewById(R.id.btn_accident);
        scAutoNotify = findViewById(R.id.sc_auto_notify);

        btnFire.setOnClickListener(this);
        btnElectricity.setOnClickListener(this);
        btnPatient.setOnClickListener(this);
        btnAccident.setOnClickListener(this);
        scAutoNotify.setOnClickListener(this);

        scAutoNotify.setChecked(mainPresenter.getAutoCall());

        rlCountDownLayout = findViewById(R.id.rl_count_down);

        mShakeDetectionReceiver = new ShakeDetectionReceiver();

        if (!mainPresenter.isInternetAvailable())
            internetToast();
        if (!mainPresenter.isGpsEnabled())
            gpsToast();
        if (Functions.checkPermission(this))
            startService(new Intent(this, AutoDetectService.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mainPresenter.requestLocationUpdates();
        LocalBroadcastManager.getInstance(this).registerReceiver(mShakeDetectionReceiver, new IntentFilter(Functions.SHAKE_DETECTED_BROADCAST));
    }

    @Override
    protected void onStop() {
        mainPresenter.removeLocationRequest();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mShakeDetectionReceiver);
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Functions.PERMISSIONS_REQUEST_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mainPresenter.requestLocationUpdates();
                    startService(new Intent(this, AutoDetectService.class));
                } else {
                    mainPresenter.removeLocationRequest();
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_fire:
                if (mainPresenter.isButtonReady(Functions.INCIDENT_TYPE_FIRE)) {
                    mainPresenter.startCountDownIntent(Functions.INCIDENT_TYPE_FIRE);
                } else {
                    Toast.makeText(this, "Please wait, you've already send a notification", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_electricity:
                if (mainPresenter.isButtonReady(Functions.INCIDENT_TYPE_ELECTRICITY)) {
                    mainPresenter.startCountDownIntent(Functions.INCIDENT_TYPE_ELECTRICITY);
                } else {
                    Toast.makeText(this, "Please wait, you've already send a notification", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_patient:
                if (mainPresenter.isButtonReady(Functions.INCIDENT_TYPE_PATIENT)) {
                    mainPresenter.startCountDownIntent(Functions.INCIDENT_TYPE_PATIENT);
                } else {
                    Toast.makeText(this, "Please wait, you've already send a notification", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_accident:
                if (mainPresenter.isButtonReady(Functions.INCIDENT_TYPE_ACCIDENT)) {
                    mainPresenter.startCountDownIntent(Functions.INCIDENT_TYPE_ACCIDENT);
                } else {
                    Toast.makeText(this, "Please wait, you've already send a notification", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.sc_auto_notify:
                mainPresenter.toggleAutoNotify(scAutoNotify.isChecked());
                break;
        }
    }

    public void onMapReady(GoogleMap googleMap) {
        LogPrinter.print("key ......... Map Ready");
        mGoogleMap = googleMap;
        mMapReady = true;
        MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style);
        mBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.marker_location);
        mGoogleMap.setMapStyle(mapStyleOptions);
        mainPresenter.incidentsListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Incident singleIncident = dataSnapshot.getValue(Incident.class);
                long incidentLifeTime = System.currentTimeMillis() - singleIncident.getIncidentTimeLong();

                if (singleIncident.getActive() == Functions.ACTIVE_INCIDENT) {
                    addIncidentMarker(singleIncident.getLatitude(), singleIncident.getLongitude(), singleIncident.getIncidentType(), dataSnapshot.getKey(), false);
                    if (singleIncident.getIncidentType() == Functions.INCIDENT_TYPE_ACCIDENT) {

                        CircleOptions circleOptions = new CircleOptions()
                                .center(new LatLng(singleIncident.getLatitude(), singleIncident.getLongitude()))
                                .radius(965)
                                .fillColor(0x1Eba8749)
                                .strokeColor(0xff64482a)
                                .strokeWidth(2.0F);
                        mCircleHashMap.put(dataSnapshot.getKey(), mGoogleMap.addCircle(circleOptions));
                    }
                } else if (singleIncident.getActive() == 2 && singleIncident.getDeviceImei().equals(mainPresenter.getDataManager().getDeviceId())) {
                    addIncidentMarker(singleIncident.getLatitude(), singleIncident.getLongitude(), singleIncident.getIncidentType(), dataSnapshot.getKey(), true);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Incident singleIncident = dataSnapshot.getValue(Incident.class);

                if ((singleIncident.getActive() == 0 || singleIncident.getActive() == 4) && mMarkerHashMap.containsKey(dataSnapshot.getKey())) {
                    mMarkerHashMap.get(dataSnapshot.getKey()).remove();
                    mMarkerHashMap.remove(dataSnapshot.getKey());

                    if (singleIncident.getIncidentType() == Functions.INCIDENT_TYPE_ACCIDENT && mCircleHashMap.containsKey(dataSnapshot.getKey())) {
                        mCircleHashMap.get(dataSnapshot.getKey()).remove();
                        mCircleHashMap.remove(dataSnapshot.getKey());
                    }
                } else if (singleIncident.getActive() == Functions.ACTIVE_INCIDENT) {
                    addIncidentMarker(singleIncident.getLatitude(), singleIncident.getLongitude(), singleIncident.getIncidentType(), dataSnapshot.getKey(), false);

                    if (singleIncident.getIncidentType() == Functions.INCIDENT_TYPE_ACCIDENT) {
                        CircleOptions circleOptions = new CircleOptions()
                                .center(new LatLng(singleIncident.getLatitude(), singleIncident.getLongitude()))
                                .radius(965)
                                .fillColor(0x1Eba8749)
                                .strokeColor(0xff64482a)
                                .strokeWidth(2.0F);
                        mCircleHashMap.put(dataSnapshot.getKey(), mGoogleMap.addCircle(circleOptions));
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
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
                    mainPresenter.displayLocation();
                }
            }
        };

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    public void displayLocation() {
        if (!mMapReady)
            return;
        if (mMyPositionMarker != null)
            mMyPositionMarker.remove();
        if (mLocation != null) {
            LatLng myPosition = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(myPosition)
                    .icon(mBitmapDescriptor)
                    .title("You");
            mMyPositionMarker = mGoogleMap.addMarker(markerOptions);
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 16.0f));
        }
    }

    public void requestLocationUpdates() {
        if (Functions.checkPermission(this)) {
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        } else {
            Functions.requestPermissions(this);
        }
    }

    public void removeLocationRequest() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    public void internetToast() {
        Toast.makeText(this, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
    }

    public void gpsToast() {
        Toast.makeText(this, "Please turn on Location!", Toast.LENGTH_SHORT).show();
    }

    public void addIncidentMarker(Double latitude, Double longitude, int incidentType, String key, boolean progress) {
        LogPrinter.print("key .......... " + key);
        if (mMarkerHashMap.containsKey(key)) {
            mMarkerHashMap.get(key).remove();
            mMarkerHashMap.remove(key);
        }
        LatLng currentLatLng = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(currentLatLng);

        if (progress) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(Functions.getMarkerDrawable(incidentType, true)));
            markerOptions.title("In progress, please wait");
        } else {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(Functions.getMarkerDrawable(incidentType, false)));
            markerOptions.title("Approved we're coming");
        }

        mMarkerHashMap.put(key, mGoogleMap.addMarker(markerOptions));
    }

    public void startCountDownIntent(int incidentType) {
        if (mLocation == null) {
            Toast.makeText(this,"Please wait until your location get updated", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent counter = new Intent(mContext, CounterActivity.class);
        counter.putExtra("latitude", mLocation.getLatitude());
        counter.putExtra("longitude", mLocation.getLongitude());
        counter.putExtra("incidentType", incidentType);
        startActivity(counter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public class ShakeDetectionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogPrinter.print("key ....... onReceive" );
            mainPresenter.onShakeDetected();
        }
    }

    public void onShakeDetected() {
        long timeDifference = System.currentTimeMillis() - mLastReceiverCallTime;
//        if (!Functions.isAppOnForeground(mContext, "com.example.hicham.civilprotectionapp.ui.map")) {
//
//            Intent newIntent = new Intent();
//            newIntent.setClassName("com.example.hicham.civilprotectionapp", "com.example.hicham.civilprotectionapp.ui.map.MainActivity");
//            newIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//            mContext.startActivity(newIntent);
//            PowerManager powerManager = ((PowerManager) getSystemService(Context.POWER_SERVICE));
//            PowerManager.WakeLock wake = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
//            wake.acquire();
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
//                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
//                    | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
//
//        }
        LogPrinter.print("key ....... timeDifference" + timeDifference);
        if (timeDifference <= REREIVER_CALL_DURATION)
            return;
        mLastReceiverCallTime = System.currentTimeMillis();
        mainPresenter.startCountDownIntent(Functions.INCIDENT_TYPE_ACCIDENT);
    }
}
