package com.example.hicham.civilprotectionapp.ui.base;

import com.example.hicham.civilprotectionapp.models.DataManager;

public class BasePresenter<V extends MvpView> implements MvpPresenter<V> {
    private V mView;
    private DataManager mDataManager;

    public BasePresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void onAttach(V view) {
        mView = view;
    }

    public V getView() {
        return mView;
    }

    public DataManager getDataManager() {
        return mDataManager;
    }
}
