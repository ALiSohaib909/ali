package com.pins.infinity.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.pins.infinity.BuildConfig;

/**
 * Created by lenovo on 18/11/15.
 */
public class AppSharedPrefrence {
    public static int getInt(Context context, PREF_KEY key) {
        SharedPreferences sharedPref = getDefault(context);
        int value = sharedPref.getInt(key.KEY, 0);
        return value;
    }

    public static void putInt(Context context, PREF_KEY key, int value) {
        SharedPreferences sharedPref = getDefault(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(key.KEY, value);

        // Commit the edits!
        editor.commit();
    }

    public static void putLong(Context context, PREF_KEY key, long value) {
        SharedPreferences sharedPref = getDefault(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(key.KEY, value);

        // Commit the edits!
        editor.commit();
    }

    public static int getCount(Context context, String key) {
        SharedPreferences sharedPref = getDefault(context);
        int value = sharedPref.getInt(key, 1);
        return value;
    }

    public static long getLong(Context context, PREF_KEY key) {
        SharedPreferences sharedPref = getDefault(context);
        return sharedPref.getLong(key.KEY, 1);
    }

    public static void putCount(Context context, String key, int value) {
        SharedPreferences sharedPref = getDefault(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        // Commit the edits!
        editor.commit();
    }

    public static void putString(Context context, PREF_KEY key, String value) {
        SharedPreferences sharedPref = getDefault(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key.KEY, value);

        // Commit the edits!
        editor.commit();
    }

    public static String getString(Context context, PREF_KEY key) {
        SharedPreferences sharedPref = getDefault(context);
        String value = sharedPref.getString(key.KEY, "");
        return value;
    }

    public static void putBoolean(Context context, PREF_KEY key, boolean value) {
        SharedPreferences sharedPref = getDefault(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key.KEY, value);
        editor.commit();
    }

    public static boolean getBoolean(Context context, PREF_KEY key) {
        SharedPreferences sharedPref = getDefault(context);
        boolean value = sharedPref.getBoolean(key.KEY, false);
        return value;
    }

    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sharedPref = getDefault(context);
        boolean value = sharedPref.getBoolean(key, false);
        return value;
    }

    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sharedPref = getDefault(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void putString(Context context, String key, String value) {
        SharedPreferences sharedPref = getDefault(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getString(Context context, String key) {
        SharedPreferences sharedPref = getDefault(context);
        String value = sharedPref.getString(key, "");
        return value;
    }

    public static void clearAllPrefs(Context context) {
        SharedPreferences sharedPref = getDefault(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
    }

    public static int getImageSize(Context context, String key) {
        SharedPreferences sharedPref = getDefault(context);
        int value = sharedPref.getInt(key, 0);
        return value;
    }

    public static void putImageSize(Context context, String key, int value) {
        SharedPreferences sharedPref = getDefault(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        // Commit the edits!
        editor.commit();
    }

    public static void putFilterAppliedBoolean(Context context, String key, boolean value) {
        SharedPreferences sharedPref = getDefault(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getFilterAppliedBoolean(Context context, String key) {
        SharedPreferences sharedPref = getDefault(context);
        boolean value = sharedPref.getBoolean(key, false);
        return value;
    }

    private static SharedPreferences getDefault(Context context) {
        return context.getSharedPreferences(BuildConfig.APPLICATION_ID, android.content.Context.MODE_PRIVATE);
    }

    public static enum PREF_KEY {
        USER_PROFILE_INFO("profile_info"),
        IS_FIRST_TIME("firstTime"),
        SHOW_HELP_SCREENS("helpScreens"),
        IS_LOCK_ACTIVE("lock"),
        SCAN("scan"),
        SESSION("session"),
        SESSION_STARTED("session_started"),
        WHATSAPP_TIME("whatsapp_time"),
        FIRST_LOGIN("first_login"),
        FIRST_CONTACT_SYNC("first_contact_sync"),
        //        DATA_SAVING("data_saving_mode"),
//        IS_WAITING_FOR_OTP("IsWaitingForSms"),
        LATITUDE("lat"),
//        DEVICE_TOKEN("device_token"),
//        APP_MSG_COUNT("app_msg_count"),
//        RESUME_PROGRESS("resume_progress"),
//        DEN_SIGNUP_PROFILE("den_signup_profile"),
//        DEN_VC_ID_DEVICE_LIST_RESPONSE("device_list"),
//        CONTINUE_TXT("CONTINUE_TXT"),
//        OTP_MESSAGE_CHILD("OTP_MESSAGE_CHILD"),
//        OTP_MESSAGE_PARENT("OTP_MESSAGE_PARENT"),
//        OTP_MESSAGE_CODE("OTP_MESSAGE_CODE"),
//        VC_ID("vcnumber"),
        LONGITUDE("lng");

        public final String KEY;

        PREF_KEY(String key) {
            this.KEY = key;
        }
    }
}
