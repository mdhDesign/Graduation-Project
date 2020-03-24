package com.example.hicham.civilprotectionapp.ui.counter;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.hicham.civilprotectionapp.BaseApp;
import com.example.hicham.civilprotectionapp.R;
import com.example.hicham.civilprotectionapp.models.DataManager;
import com.example.hicham.civilprotectionapp.utils.LogPrinter;

import java.util.Locale;

public class CounterActivity extends AppCompatActivity implements CounterMvpView, View.OnClickListener {
    CounterPresenter counterPresenter;
    private TextView mCountDownTv;
    Button btnSendNotification;
    Button btnStopNotification;
    private static final long NOTIFICATION_COUNT_SECONDS = 1000 * 10; // 30 Seconds;
    private long mRemainingSeconds = NOTIFICATION_COUNT_SECONDS;
    private long mSystemTimeRemainingSeconds;
    CountDownTimer countDownTimer;
    private boolean mIsCountDownTimerStarted = false;
    private double mLatitude;
    private double mLongitude;
    private int mIncidentType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.count_down_layout);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            mLatitude = extras.getDouble("latitude");
            mLongitude = extras.getDouble("longitude");
            mIncidentType = extras.getInt("incidentType");

            LogPrinter.print("key ........ " + mLatitude);
        }

        DataManager dataManager = ((BaseApp)getApplicationContext()).getDataManager();
        counterPresenter = new CounterPresenter(dataManager);
        counterPresenter.onAttach(this);

        mCountDownTv = findViewById(R.id.tv_count_down);
        btnSendNotification = findViewById(R.id.btn_send_notification);
        btnStopNotification = findViewById(R.id.btn_stop_notification);

        btnStopNotification.setOnClickListener(this);
        btnSendNotification.setOnClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsCountDownTimerStarted  == false)
            counterPresenter.startCountDownTimer();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send_notification:
                counterPresenter.sendNotification(mIncidentType, mLatitude, mLongitude);
                break;
            case R.id.btn_stop_notification:
                counterPresenter.cancelNotification();
                break;
        }
    }

    public void startCountDownTimer() {
        mSystemTimeRemainingSeconds = System.currentTimeMillis() + mRemainingSeconds;
        countDownTimer = new CountDownTimer(mRemainingSeconds, 1000) {
            @Override
            public void onTick(long l) {
                mRemainingSeconds = l;
                mIsCountDownTimerStarted = true;
                counterPresenter.updateCountDownTv();
            }

            @Override
            public void onFinish() {
                mCountDownTv.setText("00");
                counterPresenter.sendNotification(mIncidentType, mLatitude, mLongitude);
            }
        };
        countDownTimer.start();
    }

    public void updateCountDownTv() {
        int seconds = (int) mRemainingSeconds / 1000;
        String remainingSecondsFormatted = String.format(Locale.getDefault(), "%02d", seconds);
        mCountDownTv.setText(remainingSecondsFormatted);
    }


    public void sendNotification() {
        if (countDownTimer != null)
            countDownTimer.cancel();
        finish();
    }

    public void cancelNotification() {
        countDownTimer.cancel();
        mRemainingSeconds = NOTIFICATION_COUNT_SECONDS;
        finish();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && mIsCountDownTimerStarted) {
            counterPresenter.cancelNotification();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("mSystemTimeRemainingSeconds", mSystemTimeRemainingSeconds);
        outState.putBoolean("mIsCountDownTimerStarted", true);
        if (countDownTimer != null)
            countDownTimer.cancel();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mIsCountDownTimerStarted = savedInstanceState.getBoolean("mIsCountDownTimerStarted");
        mRemainingSeconds = savedInstanceState.getLong("mSystemTimeRemainingSeconds") - System.currentTimeMillis();
        if (mRemainingSeconds <= 0) {
            counterPresenter.sendNotification(mIncidentType, mLatitude, mLongitude);
        } else {
            counterPresenter.startCountDownTimer();
        }
    }
}
