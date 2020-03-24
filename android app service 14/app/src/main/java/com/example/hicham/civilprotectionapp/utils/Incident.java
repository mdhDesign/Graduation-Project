package com.example.hicham.civilprotectionapp.utils;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.Map;

public class Incident {
    private int mIncidentType;
    private double mLatitude;
    private double mLongitude;
    private Map<String, String> mIncidentTime;
    private long mIncidentTimeLong;
    private String mDeviceImei;
    private String mCallerPhone;
    private String mRegion;
    private int mActive = 1;

    public Incident() {
    }

    public Incident(int incidentType, double latitude, double longitude, String deviceImei, String callerPhone, String region, int active) {
        this.mIncidentType = incidentType;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mIncidentTime = ServerValue.TIMESTAMP;
        this.mDeviceImei = deviceImei;
        this.mCallerPhone = callerPhone;
        this.mRegion = region;
        this.mActive = active;
    }

    public int getIncidentType() {
        return mIncidentType;
    }

    public void setIncidentType(int incidentType) {
        this.mIncidentType = incidentType;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        this.mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        this.mLongitude = longitude;
    }

    public Map<String, String> getIncidentTime() {
        return mIncidentTime;
    }

    public void setIncidentTime(long incidentTime) {
        this.mIncidentTimeLong = incidentTime;
    }

    @Exclude
    public long getIncidentTimeLong() {
        return mIncidentTimeLong;
    }

    public String getDeviceImei() {
        return mDeviceImei;
    }

    public void setDeviceImei(String deviceImei) {
        this.mDeviceImei = deviceImei;
    }

    public String getCallerPhone() {
        return this.mCallerPhone;
    }

    public void setCallerPhone(String callerPhone) {
        this.mCallerPhone = callerPhone;
    }

    public String getRegion() {
        return this.mRegion;
    }

    public void setRegion(String region) {
        this.mRegion = region;
    }

    public int getActive() {
        return mActive;
    }

    public void setActive(int active) {
        this.mActive = active;
    }
}
