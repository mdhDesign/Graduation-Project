package com.example.hicham.civilprotectionapp.ui.splash;

import android.os.Handler;

import com.example.hicham.civilprotectionapp.models.DataManager;
import com.example.hicham.civilprotectionapp.ui.base.BasePresenter;

public class SplashPresenter<V extends SplashActivity> extends BasePresenter<V> implements SplashMvpPresenter<V> {
    public SplashPresenter(DataManager dataManager) {
        super(dataManager);
    }

    @Override
    public void decideActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String mPhoneNumber = getDataManager().getPhoneNumber();
                if (mPhoneNumber != null) {
                    getView().openMainActivity();
                } else {
                    getView().openPhoneActivity();
                }
            }
        }, 2000);
    }
}
