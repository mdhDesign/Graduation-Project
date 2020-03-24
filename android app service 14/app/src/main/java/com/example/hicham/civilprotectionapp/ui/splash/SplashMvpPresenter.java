package com.example.hicham.civilprotectionapp.ui.splash;

import com.example.hicham.civilprotectionapp.models.DataManager;
import com.example.hicham.civilprotectionapp.ui.base.BasePresenter;
import com.example.hicham.civilprotectionapp.ui.base.MvpPresenter;

public interface SplashMvpPresenter<V extends SplashActivity> extends MvpPresenter<V> {
    public void decideActivity();
}
