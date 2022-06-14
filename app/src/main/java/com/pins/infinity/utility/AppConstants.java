package com.pins.infinity.utility;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.pins.infinity.BuildConfig;
import com.pins.infinity.model.ScanModel;

import java.util.Arrays;
import java.util.List;

/**
 * Created by shri.kant on 18-11-2016.
 */
public class AppConstants {
    public final static String DEVICE_TYPE = "android";
    public final static String APP_NAME = "denp";
    public static final String SERIAL_NUMBER = "serial";
    public static final String SUBSCRIBE_NUMBER = "subscribe";
    public static final float FIVE = 5f;
    public static final float TWENTY = 20f;
    public static final float TEN = 10f;
    public static final float FIFTEEN = 15f;
    public static final float TWENTY_FIVE = 25f;
    public static final float ONE = 1f;
    public static ScanModel scanModel;
    public static Location newLocation, oldLocation;
    public static String city, countryCode, countryName, fullAddress, state;
    public static String APP_IMAGE_FOLDER = android.os.Environment.getExternalStorageDirectory()+"/PIN/Images";
    public static boolean IS_SERVICE_RUNNING = false;
    public static boolean IS__SMS_SYNC_SERVICE_RUNNING = false;
    public static List virusList = Arrays.asList("Perfect Cleaner",
            "Demo",
            "WiFi Enhancer",
            "Snake",
            "gla.pev.zvh",
            "Html5 Games",
            "Demm",
            "memory booster",
            "StopWatch",
            "Clear",
            "ballSmove_004",
            "Flashlight Free",
            "memory booste",
            "Touch Beauty",
            "Demoad",
            "Small Blue Point",
            "Battery Monitor",
            "UC Mini",
            "Shadow Crush",
            "Sex Photo",
            "tub.ajy.ics",
            "Hip Good",
            "Memory Booster",
            "phone booster",
            "SettingService",
            "Wifi Master",
            "Fruit Slots",
            "System Booster",
            "Dircet Browser",
            "FUNNY DROPS",
            "Puzzle Bubble-Pet Paradise",
            "GPS",
            "Light Browser",
            "Clean Master",
            "YouTube Downloader",
            "KXService",
            "Best Wallpapers",
            "Smart Touch",
            "Light Advanced",
            "SmartFolder",
            "youtubeplayer",
            "Beautiful Alarm",
            "PronClub",
            "Detecting instrument",
            "GPS Speed",
            "Fast Cleaner",
            "Blue Point",
            "CakeSweety",
            "Pedometer",
            "Compass Lite",
            "Fingerprint unlock",
            "PornClub",
            "com.browser.provider",
            "Assistive Touch",
            "Sex Cademy",
            "OneKeyLock",
            "Wifi Speed Pro",
            "Minibooster",
            "com.so.itouch",
            "com.fabullacop.loudcallernameringtone",
            "Kiss Browser",
            "Weather",
            "Chrono Marker",
            "Slots Mania",
            "Multifunction Flashlight",
            "So Hot",
            "Google",
            "HotH5Games",
            "Swamm Browser",
            "Billiards",
            "TcashDemo",
            "Sexy hot wallpaper",
            "Wifi Accelerate",
            "Simple Calculator",
            "Daily Racing",
            "Talking Tom 3",
            "com.example.ddeo",
            "Test",
            "Hot Photo",
            "QPlay",
            "Virtual",
            "Music Cloud");

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void saveValue(Context con, String value) {
        SharedPreferences sharedpreferences = con.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("email", value);
        editor.commit();
    }

    public static String getValue(Context con) {
        SharedPreferences sharedpreferences = con.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        return sharedpreferences.getString("email", null);
    }

//    public static boolean isLocationEnabled(Context context) {
//        int locationMode = 0;
//        String locationProviders;
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
//            try {
//                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
//
//            } catch (Settings.SettingNotFoundException e) {
//                e.printStackTrace();
//                return false;
//            }
//
//            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
//
//        }else{
//            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
//            return !TextUtils.isEmpty(locationProviders);
//        }
//
//
//    }
}
