package com.example.hicham.civilprotectionapp.ui.phone;

import com.example.hicham.civilprotectionapp.models.DataManager;
import com.example.hicham.civilprotectionapp.ui.base.BasePresenter;
import com.example.hicham.civilprotectionapp.utils.Functions;
import com.example.hicham.civilprotectionapp.utils.LogPrinter;

public class PhonePresenter<V extends PhoneNumberActivity> extends BasePresenter<V> implements PhoneMvpPresenter<V> {
    public PhonePresenter(DataManager dataManager) {
        super(dataManager);
    }

    public void addPhoneNumber(String phoneNumber) {
        getDataManager().setPhoneNumber(phoneNumber);
        getView().startMainActivity();
    }

    public void registerImei() {
        String deviceId = Functions.deviceId(getView());
        if (deviceId != null)
            getDataManager().setDeviceId(deviceId);
    }
}
