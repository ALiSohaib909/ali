package com.pins.infinity.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.util.Log;
import android.widget.Toast;

import com.pins.infinity.services.SmsLogsUploadService;

/**
 * Created by bimalchawla on 3/9/17.
 */

public class SmsListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("PINSAPP Received sms");
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            context.startService(new Intent(context, SmsLogsUploadService.class));
        }
    }
}
