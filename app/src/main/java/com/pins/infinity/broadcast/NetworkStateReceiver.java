package com.pins.infinity.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import com.pins.infinity.services.DbUploadService;
import com.pins.infinity.utility.ServiceUtils;

/**
 * Created by bimalchawla on 17/7/17.
 */

public class NetworkStateReceiver extends BroadcastReceiver {

    private boolean connected;
    public void onReceive(Context context, Intent intent) {
        Log.d("app", "Network connectivity change");
        if (intent.getExtras() != null) {
            final ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

            if (ni != null && ni.isConnected()) {
                if(!connected) {
                    connected = true;
                    Log.i("app", "Network " + ni.getTypeName() + " connected");

                    ServiceUtils.startForegroundService(new Intent(context, DbUploadService.class), context);
                }
            } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                connected = false;
                Log.d("App", "There's no network connectivity");
            }
        }
    }
}
