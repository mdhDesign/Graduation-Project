package com.example.hicham.civilprotectionapp.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;

import com.example.hicham.civilprotectionapp.R;

import java.util.List;

import static android.content.Context.TELEPHONY_SERVICE;


public class Functions {
    public static final String PACKAGE_NAME = "com.example.hicham.civilprotectionapp";
    public static final String SHAKE_DETECTED_BROADCAST = PACKAGE_NAME + ".SHAKE_DETECTED";
    public static final int PERMISSIONS_REQUEST_LOCATION = 14;
    public static final int INCIDENT_TYPE_FIRE = 0;
    public static final int INCIDENT_TYPE_ELECTRICITY = 1;
    public static final int INCIDENT_TYPE_PATIENT = 2;
    public static final int INCIDENT_TYPE_ACCIDENT = 3;
    public static final int ACTIVE_INCIDENT = 1;
    public static final long TIME_BETWEEN_NOTIFICATIONS = 1000 * 30;
    public static final String CHANNEL_ID = "auto_detect_channel";

    private Functions() {}

    public static Boolean checkPermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermissions(Activity context) {
        if (!checkPermission(context)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                ActivityCompat.requestPermissions(context,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    public static int getMarkerDrawable(int incidentType, boolean progress) {
        int drawableId = R.drawable.btn_accident;
        if (progress) {
            switch (incidentType) {
                case INCIDENT_TYPE_FIRE:
                    return R.drawable.marker_fire_progress;
                case INCIDENT_TYPE_ELECTRICITY:
                    return R.drawable.marker_electricity_progress;
                case INCIDENT_TYPE_PATIENT:
                    return R.drawable.marker_patient_progress;
                case INCIDENT_TYPE_ACCIDENT:
                    return R.drawable.marker_accident_progress;
                default:
                    return drawableId;
            }

        } else {
            switch (incidentType) {
                case INCIDENT_TYPE_FIRE:
                    return R.drawable.marker_fire;
                case INCIDENT_TYPE_ELECTRICITY:
                    return R.drawable.marker_electricity;
                case INCIDENT_TYPE_PATIENT:
                    return R.drawable.marker_patient;
                case INCIDENT_TYPE_ACCIDENT:
                    return R.drawable.marker_accident;
                default:
                    return drawableId;
            }
        }
    }

    public static boolean isAppOnForeground(Context context, String packageName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentName = runningTaskInfo.get(0).topActivity;
        return componentName.getPackageName().equals(packageName);
    }

    public static void sendNotification(Context context, boolean inCircle) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, context.getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription(context.getString(R.string.notification));
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.btn_accident)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_large_icon))
                .setColor(Color.BLACK);

        if (inCircle) {
            mBuilder.setContentTitle(context.getString(R.string.in_circle_title))
                    .setContentText(context.getString(R.string.in_circle_text))
                    .setAutoCancel(true)
                    .setOngoing(false);
        } else {
            mBuilder.setContentTitle(context.getString(R.string.out_circle_title))
                    .setContentText(context.getString(R.string.out_circle_text))
                    .setAutoCancel(true)
                    .setOngoing(false);
        }

        mNotificationManager.notify(0, mBuilder.build());
    }

    public static String deviceId(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                return telephonyManager.getImei();
            } else {
                return telephonyManager.getDeviceId();
            }
        }
        return null;
    }
}
