package com.example.hicham.civilprotectionapp.ui.map;

import com.example.hicham.civilprotectionapp.ui.base.MvpPresenter;
import com.google.android.gms.maps.OnMapReadyCallback;

public interface MainMvpPresenter<V extends MainActivity> extends MvpPresenter<V>, OnMapReadyCallback {
}
