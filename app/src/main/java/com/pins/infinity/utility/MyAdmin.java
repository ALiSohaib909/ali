package com.pins.infinity.utility;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.pins.infinity.services.CaptureImageService;
import com.pins.infinity.utility.SharedPreferences.Const;

/**
 * Created by bimalchawla on 24/1/17.
 */

public class MyAdmin extends DeviceAdminReceiver {



    static String PREF_PASSWORD_QUALITY = "password_quality";
    static String PREF_PASSWORD_LENGTH = "password_length";
    static String PREF_MAX_FAILED_PW = "max_failed_pw";

    void showToast(Context context, CharSequence msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return "This is an optional message to warn the user about disabling.";
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {
    }

    @Override
    public void onPasswordFailed(Context context, Intent intent) {
        if(!AppConstants.IS_SERVICE_RUNNING) {
            AppConstants.IS_SERVICE_RUNNING = true;
            Intent intnt = new Intent(context, CaptureImageService.class);
            intnt.putExtra(Const.THEFT_KEY, true);
            context.startService(intnt);

        }
    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
    }

}