package com.pins.infinity.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.pins.infinity.BuildConfig;
import com.pins.infinity.utility.SharedPreferences.Const;

import static com.pins.infinity.utility.SharedPreferences.Const.SHUTDOWN_RECEIVER;

/**
 * Created by Pavlo Melnyk on 25.07.2018.
 */
public class ShutdownReceiver extends BroadcastReceiver {

    private static final String ACTION_SHUTDOWN = "android.intent.action.ACTION_SHUTDOWN";
    private static final String QUICKBOOT_POWEROFF = "android.intent.action.QUICKBOOT_POWEROFF";


    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_SHUTDOWN.equals(intent.getAction()) ||
                QUICKBOOT_POWEROFF.equals(intent.getAction())) {

            SharedPreferences.Editor editor = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE).edit();
            editor.putBoolean(Const.SHUTDOWN, true);

            editor.apply();
        }
    }

}
