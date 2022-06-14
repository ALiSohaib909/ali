package com.pins.infinity.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pins.infinity.services.CallLogsUploadService;

/**
 * Created by bimalchawla on 3/9/17.
 */

public class OutgoingCallListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, CallLogsUploadService.class));

    }
}
