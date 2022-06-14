package com.pins.infinity.utility;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;

/**
 * Created by Pavlo Melnyk on 20.08.2018.
 */
public final class ServiceUtils {
    private final static int SERVICE_ID = 101;

    private ServiceUtils(){
    }

    public static void startForeground(Service service) {
        String channelId = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel(service);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(service, channelId );
        Notification notification = notificationBuilder.setOngoing(true)
                .setPriority(PRIORITY_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        service.startForeground(SERVICE_ID, notification);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private static String createNotificationChannel(Service service){
        String channelId = "my_service";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager se = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
        se.createNotificationChannel(chan);
        return channelId;
    }

    public static void startForegroundService(Intent intent, Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }
}
