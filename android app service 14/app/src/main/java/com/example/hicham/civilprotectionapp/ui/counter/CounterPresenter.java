package com.example.hicham.civilprotectionapp.ui.counter;

import com.example.hicham.civilprotectionapp.models.DataManager;
import com.example.hicham.civilprotectionapp.ui.base.BasePresenter;
import com.example.hicham.civilprotectionapp.utils.Incident;

public class CounterPresenter<V extends CounterActivity> extends BasePresenter<V> implements CounterMvpPresenter<V> {
    public CounterPresenter(DataManager dataManager) {
        super(dataManager);
    }

    public void startCountDownTimer() {
        getView().startCountDownTimer();
    }

    public void sendNotification(int incidentType, double latitude, double longitude) {
        Incident incident = new Incident(incidentType, latitude, longitude, getDataManager().getDeviceId(), getDataManager().getPhoneNumber(), getDataManager().getRegion(), 2);
        getDataManager().loadNotificationToDatabase(incident, getView());
        getView().sendNotification();
    }

    public void updateCountDownTv() {
        getView().updateCountDownTv();
    }

    public void cancelNotification() {
        getView().cancelNotification();
    }
}
