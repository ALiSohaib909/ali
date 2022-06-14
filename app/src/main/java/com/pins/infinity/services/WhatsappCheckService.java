package com.pins.infinity.services;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.pins.infinity.application.MyApplication;
import com.pins.infinity.utility.AppConstants;
import com.pins.infinity.utility.AppSharedPrefrence;
import com.pins.infinity.utility.ContextUtils;
import com.pins.infinity.utility.ServiceUtils;
import com.pins.infinity.utility.SharedPreferences.Const;
import com.pins.infinity.utility.Utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WhatsappCheckService extends Service {
    UsageStatsManager mUsageStatsManager;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mUsageStatsManager = ContextUtils.getUsageStatsManager(MyApplication.getInstance().getApplicationContext());
        if(mUsageStatsManager == null) {
             return;
        }

        ServiceUtils.startForeground(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            //checking if user is logged in!!!

            if (Utility.checkString(AppSharedPrefrence.getString(MyApplication.getInstance(), AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO))) {
                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        List<UsageStats> usageStatsList = getUsageStatistics();
                        Collections.sort(usageStatsList, new LastTimeLaunchedComparatorDesc());
                        updateAppsList(usageStatsList);
                    }
                }, 2000, 10000);
            }
        }
        return START_STICKY;
    }

    private List<UsageStats> getUsageStatistics() {
        // Get the app statistics since one year ago from the current time.
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);

        List<UsageStats> queryUsageStats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, cal.getTimeInMillis(),
                System.currentTimeMillis());

        return queryUsageStats;
    }

    private void updateAppsList(List<UsageStats> usageStatsList) {
        List<UsageStats> customUsageStatsList = new ArrayList<>();
        loop:
        for (int i = 0; i < usageStatsList.size(); i++) {
            long lastTimeUsed = usageStatsList.get(i).getLastTimeUsed();
            switch (usageStatsList.get(i).getPackageName()) {
                case "com.whatapp":
                case "com.facebook.katana":
                case "com.skype.raider":
                case "com.google.android.talk":
                case "com.facebook.orca":
                    customUsageStatsList.add(usageStatsList.get(i));

                    if (AppSharedPrefrence.getLong(MyApplication.getInstance(),
                            AppSharedPrefrence.PREF_KEY.WHATSAPP_TIME) < lastTimeUsed) {
                        if (!AppConstants.IS_SERVICE_RUNNING) {
                            AppConstants.IS_SERVICE_RUNNING = true;
                            Intent intnt = new Intent(WhatsappCheckService.this, CaptureImageService.class);
                            intnt.putExtra(Const.THEFT_KEY, true);
                            startService(intnt);
                        }
                    }
                    AppSharedPrefrence.putLong(MyApplication.getInstance(), AppSharedPrefrence.PREF_KEY.WHATSAPP_TIME, lastTimeUsed);
                    break loop;
            }
        }
    }

    /**
     * The {@link Comparator} to sort a collection of {@link UsageStats} sorted by the timestamp
     * last time the app was used in the descendant order.
     */
    private static class LastTimeLaunchedComparatorDesc implements Comparator<UsageStats> {

        @Override
        public int compare(UsageStats left, UsageStats right) {
            return Long.compare(right.getLastTimeUsed(), left.getLastTimeUsed());
        }
    }

}
