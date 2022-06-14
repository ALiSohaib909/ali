package com.pins.infinity.services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pins.infinity.application.MyApplication;
import com.pins.infinity.broadcast.MyObserver;
import com.pins.infinity.interfaces.ApiResponseListener;
import com.pins.infinity.model.ProfileBaseModel;
import com.pins.infinity.utility.ApiCall;
import com.pins.infinity.utility.ApiConstants;
import com.pins.infinity.utility.AppConstants;
import com.pins.infinity.utility.AppSharedPrefrence;
import com.pins.infinity.utility.ServiceUtils;
import com.pins.infinity.utility.Utility;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class BackgroundService extends Service implements ApiResponseListener {


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ServiceUtils.startForeground(this);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (Utility.checkString(AppSharedPrefrence.getString(MyApplication.getInstance(), AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO))) {
            MyObserver myObserver = new MyObserver(new Handler());
            ContentResolver contentResolver = this.getApplicationContext().getContentResolver();
            contentResolver.registerContentObserver(Uri.parse("content://sms"), true, myObserver);

            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = this.registerReceiver(null, ifilter);


            final String str = "";
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                int phonelaunched = 0, phoneclosed = 0;
                int phonelaunches = 1;

                @Override
                public void run() {

                    int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                    int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

                    float batteryPct = (level / (float) scale) * 100;
                    if (batteryPct == AppConstants.ONE || batteryPct == AppConstants.FIVE
                            || batteryPct == AppConstants.TEN || batteryPct == AppConstants.FIFTEEN
                            || batteryPct == AppConstants.TWENTY || batteryPct == AppConstants.TWENTY_FIVE) {
                        sendData((int) (Math.round(batteryPct)) + "");
                    }

                    ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                    List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfo = am.getRunningAppProcesses();

                    if (null != runningAppProcessInfo && !runningAppProcessInfo.isEmpty()) {
                        for (ActivityManager.RunningAppProcessInfo appProcess : runningAppProcessInfo) {
                            if (appProcess.processName.equals("com.whatsapp")) {
                                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND  /*isForeground(getApplicationContext(),runningAppProcessInfo.get(i).processName)*/) {
                                    if (phonelaunched == 0) {
                                        phonelaunched = 1;
                                        Log.d(str, "dude phone has been launched");
                                    } else if (phoneclosed == 1) {
                                        phonelaunches++;
                                        phoneclosed = 0;
                                        Log.d(String.valueOf(phonelaunches), "dude that was counter");
                                    }
                                } else {
                                    phoneclosed = 1;
                                    Log.d(str, "dude phone has been closed");

                                }
                            }
                        }
                    }
                }
            }, 2000, 5000);
        }

        return START_STICKY;
    }


    private void sendData(String batteryStatus) {
        try {
            String response = AppSharedPrefrence.getString(this, AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO);
            ProfileBaseModel profileBaseModel = new ObjectMapper().readValue(response, ProfileBaseModel.class);

            if (null != profileBaseModel) {
                Map<String, String> header = new HashMap<>();
                header.put(ApiConstants.AUTH_HEADER_KEY, ApiConstants.AUTH_HEADER_VALUE);
                header.put(ApiConstants.AUTH_HEADER_KEY_SEC, ApiConstants.AUTH_HEADER_KEY_SEC_VALUE);
                header.put(ApiConstants.AUTH_TOKEN_KEY, profileBaseModel.getResponse().getToken());
                System.out.println("token " + Build.MODEL + "  // " + Build.SERIAL);

                HashMap<String, String> params = new HashMap<>();
                params.put("latitude", String.valueOf(AppConstants.newLocation.getLatitude()));
                params.put("longitude", String.valueOf(AppConstants.newLocation.getLongitude()));
                params.put("country_code", AppConstants.countryCode);
                params.put("country", AppConstants.countryName);
                params.put("state", AppConstants.city);
                params.put("address", AppConstants.fullAddress);
                params.put("device_battery", batteryStatus);
                params.put("account_id", profileBaseModel.getResponse().getUser().getAccountId());
                ApiCall.getInstance().makePostRequest(this, ApiConstants.API_ADDRESS.BATTERY_SYNC.path + Utility.getImei(this), params, header, this, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(String response, int requestCode) throws IOException {
        if (response != null) {
            Log.d("battery", "sync done");
        }
    }

    @Override
    public void onError(String error, int erroCode, int requestCode) {
        Log.d("battery", "sync fail");
    }

}