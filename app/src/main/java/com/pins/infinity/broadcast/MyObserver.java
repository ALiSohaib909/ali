package com.pins.infinity.broadcast;

import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import com.pins.infinity.application.MyApplication;
import com.pins.infinity.services.SmsLogsUploadService;
import com.pins.infinity.utility.AppConstants;
import com.pins.infinity.utility.AppSharedPrefrence;
import com.pins.infinity.utility.Utility;

public class MyObserver extends ContentObserver {

    public MyObserver(Handler handler) {
        super(handler);
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        if (Utility.checkString(AppSharedPrefrence.getString(MyApplication.getInstance(), AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO))) {
            Uri uriSMSURI = Uri.parse("content://sms");
            Cursor cur = MyApplication.getInstance().getContentResolver().query(uriSMSURI, null, null, null, null);
            if (null != cur) {
                cur.moveToFirst();
                String type = cur.getString(cur.getColumnIndexOrThrow("type"));
                cur.close();

                if (null != type && type.contains("2") && !AppConstants.IS__SMS_SYNC_SERVICE_RUNNING) {
                    AppConstants.IS__SMS_SYNC_SERVICE_RUNNING = true;
                    MyApplication.getInstance().startService(new Intent(MyApplication.getInstance(), SmsLogsUploadService.class));
                }
            }
        }
    }

}
