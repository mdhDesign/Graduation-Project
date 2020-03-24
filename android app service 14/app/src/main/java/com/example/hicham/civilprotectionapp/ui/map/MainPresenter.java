package com.example.hicham.civilprotectionapp.ui.map;



import android.widget.Toast;

import com.example.hicham.civilprotectionapp.models.DataManager;
import com.example.hicham.civilprotectionapp.models.network.utils;
import com.example.hicham.civilprotectionapp.ui.base.BasePresenter;
import com.example.hicham.civilprotectionapp.utils.Functions;
import com.example.hicham.civilprotectionapp.utils.Incident;
import com.example.hicham.civilprotectionapp.utils.LogPrinter;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.database.ChildEventListener;

public class MainPresenter<V extends MainActivity> extends BasePresenter<V> implements MainMvpPresenter<V>  {

    public MainPresenter(DataManager dataManager) {
        super(dataManager);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        getView().onMapReady(googleMap);
    }

    public void initGoogleApiClientProvider() {
        getView().initGoogleApiClientProvider();
    }

    public void requestLocationUpdates() {
        getView().requestLocationUpdates();
    }

    public void removeLocationRequest() {
        getView().removeLocationRequest();
    }

    public void displayLocation() {
        getView().displayLocation();
    }


    public void toggleAutoNotify(Boolean isActivated) {
        getDataManager().setAutoCall(isActivated);
    }

    public Boolean getAutoCall() {
        return getDataManager().getAutoCall();
    }

    public void incidentsListener(ChildEventListener childEventListener) {
        getDataManager().incidentsListener(childEventListener);
    }

    public Boolean isButtonReady(int incidentType) {
        long lastNotify = System.currentTimeMillis() - getDataManager().getNotifyTime(String.valueOf(incidentType));
        if (lastNotify <= Functions.TIME_BETWEEN_NOTIFICATIONS)
            return false;
        else
            return true;
    }

    public void startCountDownIntent(int incidentType) {
        getView().startCountDownIntent(incidentType);
    }

    public boolean isInternetAvailable() {
        return getDataManager().isInternetAvailable(getView());
    }

    public boolean isGpsEnabled() {
        return getDataManager().isGpsEnabled(getView());
    }

    public void onShakeDetected() {
        if (getDataManager().getAutoCall()) {
            getView().onShakeDetected();
        }
    }
}
