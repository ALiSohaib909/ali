package com.pins.infinity.utility.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.pins.infinity.BuildConfig;
import com.pins.infinity.utility.AppConstants;

import java.util.Locale;

/**
 * Created by Pavlo Melnyk on 26.07.2018.
 */
public class Utils {

    public static void saveSimCardData(Context context, TelephonyManager telephoneManager ) {
        String simId = telephoneManager.getSimSerialNumber();
        String simNumber = telephoneManager.getLine1Number();
        String carrier = telephoneManager.getNetworkOperatorName();
        String msisdn = telephoneManager.getSubscriberId();
        String imei = telephoneManager.getDeviceId();
        String countryCode = telephoneManager.getSimCountryIso();
        String country = new Locale("", countryCode).getDisplayCountry();

        SharedPreferences.Editor editor = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE).edit();
        editor.putString(Const.SIM_ID, simId);
        editor.putString(Const.SIM_NUMBER, simNumber);
        editor.putString(Const.CARRIER, carrier);
        editor.putString(Const.MSISDN, msisdn);
        editor.putString(Const.IMEI, imei);
        editor.putString(Const.COUNTRY_CODE, countryCode);
        editor.putString(Const.COUNTRY, country);
        editor.apply();
    }
}
