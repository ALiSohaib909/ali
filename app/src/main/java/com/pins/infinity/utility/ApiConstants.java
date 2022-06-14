package com.pins.infinity.utility;

import android.net.Uri;

import com.pins.infinity.BuildConfig;

import java.util.Map;

/**
 * Created by shri.kant on 18-11-2016.
 */
public class ApiConstants {
    public final static String BASE_URL = BuildConfig.SERVER_URL;
    public final static String AUTH_HEADER_KEY = "Authorization";
    public final static String AUTH_HEADER_VALUE = BuildConfig.TOKEN;
    public final static String SIGN_UP = "users/add";
    public final static String AUTHENTICATE = "users/auth";
    public final static String ADD_DEVICE = "users/add/device/";
    public final static String AUTH_HEADER_KEY_SEC = "X-My-Header";
    public final static String AUTH_HEADER_KEY_SEC_VALUE = "basic-api.pinssolutions.com";
    public final static String AUTH_TOKEN_KEY = "token";
    public final static String PROFILE_INFO = "users/id/";
    public final static String UPDATE_PROFILE = "users/update";
    public final static String LOG_OUT = "users/logout";
    public final static String UPDATE_PROFILE_PIC = "users/profile-image";
    public final static String SYNCH_CONTACTS = "contacts/bulk/sync";
    public final static String SECURITY_QUESTION = "users/recovery";
    public final static String CHANGE_PASSWORD = "users/password";
    public final static String INIT_RECOVERY = "recovery/init";
    public final static String CHECK_RECOVERY = "recovery/check";
    public final static String PASSWORD_RECOVERY = "recovery/password";
    public final static String LOCATION_UPDATE = "users/location";
    public final static String MEDIA_LIST = "media/list/";
    public final static String MEDIA_SYNC = "media/sync";
    public final static String MEDIA_DELETE = "media/delete/";
    public final static String SYNC_SIM = "sim/detect";
    public final static String FLIGHT_MODE = "/flightmode/detect";
    public final static String FETCH_CONTACTS = "contacts/list/";
    public final static String THEFT_IMAGE = "theft/detect";
    public final static String CALL_SYNC = "calls/bulk/sync";
    public final static String SMS_SYNC = "sms/bulk/sync";
    public final static String BATTERY_SYNC = "device/battery/";
    public final static String DEVICE_TOKEN = "device/token/";
    public final static String SUBSCRIPTION_INFO = "subscription/list/";
    private final static String APP_SCHEME = "https";
    private final static String SERVER_AUTHORITY = BuildConfig.SERVER_AUTHORITY;

    public static final String RESPONSE = "response";
    public static final String USER = "user";
    public static final String CODE = "code";
    public static final String MESSAGE = "message";
    public static final int CODE_SUCCESS = 200;
    public static final String TOKEN = "token";
    public static final String ACCOUNT_ID = "account_id";
    public static final String SUBSCRIPTION_ID = "subscription_id";

    public static String urlBuilder(String createPath, Map<String, String> params) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(APP_SCHEME).encodedAuthority(SERVER_AUTHORITY)
                .path(createPath);
        if (params != null) {
            for (String key : params.keySet()) {
                if (params.get(key) != null) {
                    builder.appendQueryParameter(key, params.get(key));
                } else {

                }
            }
        }
        String builderString = builder.build().toString();
        return builderString;
    }

    public static String urlBuilderID(String createPath, Map<String, String> params, String userID) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(APP_SCHEME).encodedAuthority(SERVER_AUTHORITY )
                .path(createPath+"/"+ userID);
        if (params != null) {
            for (String key : params.keySet()) {
                if (params.get(key) != null) {
                    builder.appendQueryParameter(key, params.get(key));
                } else {

                }
            }
        }
        String builderString = builder.build().toString();
        return builderString;
    }

    public static String urlBuilderCotactsWithTwoPara(String createPath, Map<String, String> params, String userID, String imei) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(APP_SCHEME).encodedAuthority(SERVER_AUTHORITY )
                .path(createPath+"/"+ userID+"/"+imei);
        if (params != null) {
            for (String key : params.keySet()) {
                if (params.get(key) != null) {
                    builder.appendQueryParameter(key, params.get(key));
                } else {

                }
            }
        }
        String builderString = builder.build().toString();
        return builderString;
    }

    public static enum API_ADDRESS {
        SIGN_UP(ApiConstants.SIGN_UP),
        ADD_DEVICE(ApiConstants.ADD_DEVICE),
        UPDATE_PROFILE_PIC(ApiConstants.UPDATE_PROFILE_PIC),
        LOG_OUT(ApiConstants.LOG_OUT),
        UPDATE_PROFILE(ApiConstants.UPDATE_PROFILE),
        SYNCH_CONTACTS(ApiConstants.SYNCH_CONTACTS),
        AUTHENTICATE(ApiConstants.AUTHENTICATE),
        SECURITY_QUESTION(ApiConstants.SECURITY_QUESTION),
        CHANGE_PASSWORD(ApiConstants.CHANGE_PASSWORD),
        INIT_RECOVERY(ApiConstants.INIT_RECOVERY),
        CHECK_RECOVERY(ApiConstants.CHECK_RECOVERY),
        LOCATION_UPDATE(ApiConstants.LOCATION_UPDATE),
        PASSWORD_RECOVERY(ApiConstants.PASSWORD_RECOVERY),
        MEDIA_DELETE(ApiConstants.MEDIA_DELETE),
        FETCH_CONTACTS(ApiConstants.FETCH_CONTACTS),
        SYNC_SIM(ApiConstants.SYNC_SIM),
        FLIGHT_MODE(ApiConstants.FLIGHT_MODE),
        THEFT_IMAGE(ApiConstants.THEFT_IMAGE),
        CALL_SYNC(ApiConstants.CALL_SYNC),
        SMS_SYNC(ApiConstants.SMS_SYNC),
        BATTERY_SYNC(ApiConstants.BATTERY_SYNC),
        MEDIA_SYNC(ApiConstants.MEDIA_SYNC);

        public final String path;

        API_ADDRESS(String path) {
            this.path = path;
        }
    }
}

