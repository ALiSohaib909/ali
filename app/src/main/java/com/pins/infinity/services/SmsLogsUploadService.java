package com.pins.infinity.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import android.telephony.TelephonyManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pins.infinity.BuildConfig;
import com.pins.infinity.application.MyApplication;
import com.pins.infinity.interfaces.ApiResponseListener;
import com.pins.infinity.model.ProfileBaseModel;
import com.pins.infinity.model.SMSModel;
import com.pins.infinity.utility.ApiCall;
import com.pins.infinity.utility.ApiConstants;
import com.pins.infinity.utility.AppConstants;
import com.pins.infinity.utility.AppSharedPrefrence;
import com.pins.infinity.utility.Utility;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bimalchawla on 2/9/17.
 */

public class SmsLogsUploadService extends IntentService implements ApiResponseListener {

    List<SMSModel> smsList;
    SharedPreferences sharedpreferences;
    private String token = "";
    private String userId = "";
    private int smsCount = 0, remainingSmsCount = 0;
    private String date = "";
    private long timeInMillies = 0;

    public SmsLogsUploadService() {
        super("SmsLogsUploadService");
    }
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public SmsLogsUploadService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (Utility.checkString(AppSharedPrefrence.getString(MyApplication.getInstance(), AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO))) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    try {
                        Cursor c = null;
                        sharedpreferences = getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
                        try {
                            Uri message = Uri.parse("content://sms/");
                            String selection = "date > ?";
                            String[] projection = {"date", "type", "body", "address", "_id"};

                            SimpleDateFormat df = new SimpleDateFormat("EEE MMM d HH:mm:ss zz yyyy");
                            Date startDate = null;
                            try {
                                startDate = df.parse(sharedpreferences.getString("smsDate", ""));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (null != startDate) {
                                timeInMillies = startDate.getTime();
                            }

                            String[] selectionArgument = {String.valueOf(timeInMillies)};
                            System.out.println("timeIn Millies smssss " + timeInMillies);
                            c = getContentResolver().query(message, projection, selection, selectionArgument, null);
                            int totalSMS = c.getCount();
                            smsList = new ArrayList<>();
                            String myNumber = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
                            if (!Utility.checkString(myNumber)) {
                                myNumber = "na";
                            }
                            if (c.moveToFirst()) {
                                for (int i = 0; i < totalSMS; i++) {
                                    SMSModel model = new SMSModel();
                                    model.setUniqueHash(c.getString(c.getColumnIndexOrThrow("_id")));
                                    model.setTime("" + new Date(Long.valueOf(c.getString(c.getColumnIndexOrThrow("date")))));
                                    model.setContent(c.getString(c.getColumnIndexOrThrow("body")));
                                    if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                                        model.setType("incoming");
                                        model.setFrom(c.getString(c.getColumnIndexOrThrow("address")));
                                        model.setTo(myNumber);
                                    } else {
                                        model.setType("outgoing");
                                        model.setTo(c.getString(c.getColumnIndexOrThrow("address")));
                                        model.setFrom(myNumber);
                                    }

                                    smsList.add(model);
                                    c.moveToNext();
                                }
                            }

                            c.close();
                            Collections.reverse(smsList);
                            smsCount = (smsList.size() / 50);
                            uploadSms(smsList);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        } finally {
                            if (null != c) {
                                c.close();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void uploadSms(List<SMSModel> list) {

        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "";
        int length = 0;

        try {
            System.out.println("remaining smssss  " + remainingSmsCount + "   " +smsCount+"     "+ list.size());
            if ((list.size() / ((remainingSmsCount + 1) * 50)) > 0) {
                length = 50;
            } else {
                length = list.size() - ((remainingSmsCount) * 50);
            }
            for (int count = (remainingSmsCount * 50); count < ((remainingSmsCount * 50) + length); count++) {
                if ((remainingSmsCount * 50) == count) {
                    jsonInString = "[" + mapper.writeValueAsString(list.get(count)) + ",";
                } else if (count == ((remainingSmsCount * 50) + length) - 1) {
                    date = list.get(count).getTime();
                    jsonInString = jsonInString + mapper.writeValueAsString(list.get(count)) + "]";
                } else {
                    jsonInString = jsonInString + mapper.writeValueAsString(list.get(count)) + ",";
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        try {
            String response = AppSharedPrefrence.getString(this, AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO);

            if(null != response) {
                ProfileBaseModel profileBaseModel = new ObjectMapper().readValue(response, ProfileBaseModel.class);

                if (profileBaseModel != null) {
                    token = profileBaseModel.getResponse().getToken();
                    userId = profileBaseModel.getResponse().getUser().getAccountId();
                    HashMap<String, String> params = new HashMap<>();
                    params.put("account_id", userId);
                    params.put("imei", Utility.getImei(this));

                    Map<String, String> header = new HashMap<>();
                    header.put(ApiConstants.AUTH_HEADER_KEY, ApiConstants.AUTH_HEADER_VALUE);
                    header.put(ApiConstants.AUTH_HEADER_KEY_SEC, ApiConstants.AUTH_HEADER_KEY_SEC_VALUE);
                    header.put(ApiConstants.AUTH_TOKEN_KEY, token);

                    ApiCall.getInstance().makePostRequestContact(this, ApiConstants.API_ADDRESS.SMS_SYNC.path, jsonInString, header, this, 102, userId, Utility.getImei(this));

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        }


    @Override
    public void onResponse(String response, int requestCode) throws IOException {
        if (requestCode == 102) {
            int res = Integer.parseInt(response);

            if (res != 0 && res == 200) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("smsDate", date);
                editor.apply();
                    if(smsCount == remainingSmsCount) {
                        AppConstants.IS__SMS_SYNC_SERVICE_RUNNING = false;
                        stopSelf();
                    } else {
                        remainingSmsCount++;
                        uploadSms(smsList);
                    }
            } else {
                AppConstants.IS__SMS_SYNC_SERVICE_RUNNING = false;
                stopSelf();
            }
        }
    }

    @Override
    public void onError(String error, int errorCode, int requestCode) {

        AppConstants.IS__SMS_SYNC_SERVICE_RUNNING = false;stopSelf();
    }
}
