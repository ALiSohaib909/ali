package com.pins.infinity.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.pins.infinity.services.CaptureImageService;
import com.pins.infinity.utility.AppConstants;
import com.pins.infinity.utility.AppSharedPrefrence;
import com.pins.infinity.utility.SharedPreferences.Const;
import com.pins.infinity.utility.Utility;

/**
 * Created by bimalchawla on 23/3/17.
 */

public class ConnectivityReceiver  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AirplaneMode", "Service state changed" + AppConstants.IS_SERVICE_RUNNING);
        System.out.print("AirplaneMode Broadcast");
        TelephonyManager telemamanger = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String getSimSerialNumber = telemamanger.getSimSerialNumber();
        String getSimNumber = telemamanger.getLine1Number();
        String carrierName = telemamanger.getNetworkOperatorName();
        String subscriber = telemamanger.getSubscriberId();

        if(!AppConstants.IS_SERVICE_RUNNING) {

                    AppConstants.IS_SERVICE_RUNNING = true;
                    Intent intnt = new Intent(context, CaptureImageService.class);
                    intnt.putExtra(Const.THEFT_KEY, false);
                    intnt.putExtra(Const.FLIGHTMODE_KEY, true);
            context.startService(intnt);

        }
            AppSharedPrefrence.putString(context, AppConstants.SERIAL_NUMBER, getSimSerialNumber);
            AppSharedPrefrence.putString(context, AppConstants.SUBSCRIBE_NUMBER, subscriber);

    }
}