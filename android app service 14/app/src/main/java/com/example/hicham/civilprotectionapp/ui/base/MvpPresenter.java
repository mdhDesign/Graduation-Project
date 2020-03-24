package com.example.hicham.civilprotectionapp.ui.base;

public interface MvpPresenter<V extends MvpView> {
    void onAttach(V view);
}
