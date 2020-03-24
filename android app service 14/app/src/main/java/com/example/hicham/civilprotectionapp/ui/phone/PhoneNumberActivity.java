package com.example.hicham.civilprotectionapp.ui.phone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.hicham.civilprotectionapp.BaseApp;
import com.example.hicham.civilprotectionapp.R;
import com.example.hicham.civilprotectionapp.models.DataManager;
import com.example.hicham.civilprotectionapp.service.AutoDetectService;
import com.example.hicham.civilprotectionapp.ui.map.MainActivity;
import com.example.hicham.civilprotectionapp.utils.Functions;
import com.example.hicham.civilprotectionapp.utils.LogPrinter;

public class PhoneNumberActivity extends AppCompatActivity implements PhoneMvpView{
    EditText etPhoneNumber;
    Button btnAddPhoneNumer;
    PhonePresenter mPhonePresenter;
    final static int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 3;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_number_activity);

        DataManager dataManager = ((BaseApp) getApplicationContext()).getDataManager();
        mPhonePresenter = new PhonePresenter(dataManager);
        mPhonePresenter.onAttach(this);

        etPhoneNumber = findViewById(R.id.et_phone_number);
        btnAddPhoneNumer = findViewById(R.id.btn_add_number);

        btnAddPhoneNumer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mPhoneNumber = etPhoneNumber.getText().toString();
                mPhonePresenter.addPhoneNumber(mPhoneNumber);
            }
        });

        mPhonePresenter.registerImei();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
            }
        }
    }

    public void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPhonePresenter.registerImei();
                }
                break;
        }
    }
}
