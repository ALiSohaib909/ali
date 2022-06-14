package com.pins.infinity.broadcast;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.pins.infinity.BuildConfig;
import com.pins.infinity.model.ProfileBaseModel;
import com.pins.infinity.model.SmsMessageModel;
import com.pins.infinity.services.CaptureImageService;
import com.pins.infinity.utility.AppConstants;
import com.pins.infinity.utility.AppSharedPrefrence;
import com.pins.infinity.utility.SharedPreferences.Const;
import com.pins.infinity.utility.SharedPreferences.Utils;
import com.pins.infinity.utility.Utility;

import java.io.IOException;

import static com.pins.infinity.utility.SharedPreferences.Const.TAG_SMS;

/**
 * Created by bimalchawla on 22/3/17.
 */

public class SimChangedReceiver extends BroadcastReceiver {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";
    private static final String ACTION_SIM_STATE_ABSENT = "android.intent.action.SIM_STATE_ABSENT";
    private static final String EXTRAS_SIM_STATUS = "ss";

    private SharedPreferences sharedPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);

        Boolean shutdown = sharedPreferences.getBoolean(Const.SHUTDOWN, false);

        if(shutdown){
            SharedPreferences.Editor editor = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE).edit();
            editor.putBoolean(Const.SHUTDOWN, false);
            editor.apply();
            return;
        }

        if (ACTION_SIM_STATE_CHANGED.equals(intent.getAction()) ||
            ACTION_SIM_STATE_ABSENT.equals(intent.getAction())) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                        == PackageManager.PERMISSION_DENIED) {

                    String[] permissions = {Manifest.permission.READ_PHONE_STATE};

                    ActivityCompat.requestPermissions(((Activity) context), permissions, PERMISSION_REQUEST_CODE);

                } else {
                    prepareForSendingSms(context, intent);
                }
            } else {
                prepareForSendingSms(context, intent);
            }

        }
    }

    private void prepareForSendingSms(Context context, Intent intent) {

        Toast.makeText(context, "sim change detected ", Toast.LENGTH_SHORT).show();

        Bundle extras = intent.getExtras();

        if(extras != null) {

            String message = "";
            String ss = extras.getString(EXTRAS_SIM_STATUS);

            if (ss.equals("ABSENT")) {
            } else if (ss.equals("IMSI") || ss.equals("LOADED")) {

                if (!AppConstants.IS_SERVICE_RUNNING) {
                    try {
                        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                        System.out.println(TAG_SMS+"sim msisdn: " + tm.getSubscriberId() + " sim nr: " + tm.getLine1Number());

                        String simNumber = sharedPreferences.getString(Const.SIM_NUMBER, "");

                        if(Utility.checkString(tm.getLine1Number()) && !simNumber.equals(tm.getLine1Number())){
                            Utils.saveSimCardData(context, tm);
                        }

                        String response1 = AppSharedPrefrence.getString(context, AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO);
                        ProfileBaseModel profileBaseModel = new ObjectMapper().readValue(response1, ProfileBaseModel.class);

                        if (profileBaseModel != null && null != profileBaseModel.getResponse() && null != profileBaseModel.getResponse().getUser()) {
                            /** Getting an instance of SmsManager to sent sms message from the application*/
                            SmsManager smsManager = SmsManager.getDefault();

                            String latitude = sharedPreferences.getString(Const.LATITUDE, "");
                            String longitude = sharedPreferences.getString(Const.LONGITUDE, "");

                            if (Utility.checkString(latitude) && Utility.checkString(longitude)) {
                                // Sending the Sms message to the intended party
                                SmsMessageModel messageModel = new SmsMessageModel(profileBaseModel.getResponse().getUser().getAccountId(),
                                        latitude, longitude, Const.SMS);

                                message = new Gson().toJson(messageModel);


                            } else {
                                // Sending the Sms message to the intended party
                                smsManager.sendTextMessage(BuildConfig.SMS_RECEIVER_NUMBER, null,
                                        profileBaseModel.getResponse().getUser().getAccountId(), null, null);

                            }
                            Toast.makeText(context, "sms sent", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    startCaptureImageService(context, message);
                }
            }
        }
    }

    private void startCaptureImageService(Context context, String message){
        AppConstants.IS_SERVICE_RUNNING = true;
        Intent intent = new Intent(context, CaptureImageService.class);
        intent.putExtra(Const.THEFT_KEY, false);
        intent.putExtra(Const.SMS_MESSAGE_KEY, message);
        context.startService(intent);
    }
}
