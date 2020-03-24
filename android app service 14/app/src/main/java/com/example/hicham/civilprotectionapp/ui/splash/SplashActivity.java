package com.example.hicham.civilprotectionapp.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.hicham.civilprotectionapp.BaseApp;
import com.example.hicham.civilprotectionapp.R;
import com.example.hicham.civilprotectionapp.models.DataManager;
import com.example.hicham.civilprotectionapp.ui.map.MainActivity;
import com.example.hicham.civilprotectionapp.ui.phone.PhoneNumberActivity;

public class SplashActivity extends AppCompatActivity implements SplashMvpView {
    private SplashPresenter mSplashPresenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        DataManager dataManager = ((BaseApp) getApplicationContext()).getDataManager();
        mSplashPresenter = new SplashPresenter(dataManager);
        mSplashPresenter.onAttach(this);
        mSplashPresenter.decideActivity();

    }

    @Override
    public void openMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void openPhoneActivity() {
        startActivity(new Intent(this, PhoneNumberActivity.class));
        finish();
    }
}
