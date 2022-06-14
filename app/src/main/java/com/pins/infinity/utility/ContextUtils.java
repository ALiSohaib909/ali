package com.pins.infinity.utility;

import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;

/**
 * Created by Pavlo Melnyk on 19.09.2018.
 */
public final class ContextUtils {

    private static final String USAGESTATS = "usagestats";

    private ContextUtils(){
    }

    public static UsageStatsManager getUsageStatsManager(Context context) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            return (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        } else if(Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            //noinspection ResourceType
            return (UsageStatsManager) context.getSystemService(USAGESTATS);
        } else {
            return null;
        }
    }
}
