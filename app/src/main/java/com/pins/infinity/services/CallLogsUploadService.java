package com.pins.infinity.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.CallLog;
import androidx.annotation.Nullable;
import android.telephony.TelephonyManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pins.infinity.BuildConfig;
import com.pins.infinity.application.MyApplication;
import com.pins.infinity.interfaces.ApiResponseListener;
import com.pins.infinity.model.CallModel;
import com.pins.infinity.model.ProfileBaseModel;
import com.pins.infinity.utility.ApiCall;
import com.pins.infinity.utility.ApiConstants;
import com.pins.infinity.utility.AppSharedPrefrence;
import com.pins.infinity.utility.Utility;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bimalchawla on 2/9/17.
 */

public class CallLogsUploadService extends IntentService implements ApiResponseListener {

    List<CallModel> callList;
    SharedPreferences sharedpreferences;
    private int callCount = 0, remainingCallCount = 0;
    private String token = "";
    private String userId = "";
    private String date = "";
    private long timeInMillies = 0;
    boolean isSyncInProgress = false;


    public CallLogsUploadService() {
        super("CallLogsUploadService");
    }
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public CallLogsUploadService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (Utility.checkString(AppSharedPrefrence.getString(MyApplication.getInstance(), AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO))) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    try {
                        Cursor cursor = null;
                        sharedpreferences = getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
                        try {
                            Uri allCalls = Uri.parse("content://call_log/calls");

                            String selection = "date > ?";
                            String[] projection = {"date", "type", "number", "duration"};

                            SimpleDateFormat df = new SimpleDateFormat("EEE MMM d HH:mm:ss zz yyyy");
                            Date startDate = null;

                            try {
                                startDate = df.parse(sharedpreferences.getString("syncDate", ""));
                                System.out.println("timeIn Millies  " + sharedpreferences.getString("syncDate", ""));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (null != startDate) {
                                timeInMillies = startDate.getTime();
                            }

                            String[] selectionArgument = {String.valueOf(timeInMillies)};
                            System.out.println("timeIn Millies 111111111 " + timeInMillies);

                            cursor = getContentResolver().query(allCalls, projection, selection, selectionArgument, android.provider.CallLog.Calls.DATE + " ASC");
                            int totalCalls = cursor.getCount();
                            callList = new ArrayList<>();
                            String dir = null;
                            String myNumber = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
                            if (!Utility.checkString(myNumber)) {
                                myNumber = "na";
                            }
                            if (cursor.moveToFirst()) {
                                for (int i = 0; i < totalCalls; i++) {
                                    CallModel model = new CallModel();
                                    model.setUniqueHash(new Date(Long.valueOf(cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE)))).getTime() + "");
                                    model.setTime("" + new Date(Long.valueOf(cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE)))));
                                    int callType = Integer.parseInt(cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE)));
                                    System.out.println("timeIn Millies 2222222 " + cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE)));
                                    switch (callType) {
                                        case CallLog.Calls.OUTGOING_TYPE:
                                            dir = "outgoing";
                                            model.setTo(cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)));
                                            model.setFrom(myNumber);
                                            break;

                                        case CallLog.Calls.INCOMING_TYPE:
                                            dir = "incoming";
                                            model.setFrom(cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)));
                                            model.setTo(myNumber);
                                            break;

                                        case CallLog.Calls.MISSED_TYPE:
                                            dir = "missed";
                                            model.setFrom(cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)));
                                            model.setTo(myNumber);
                                            break;
                                    }

                                    model.setType(dir);
                                    model.setDuration(cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION)));

                                    callList.add(model);
                                    i++;
                                    cursor.moveToNext();
                                }
                            }
                            cursor.close();
                            callCount = (callList.size() / 50);
                            if(!isSyncInProgress) uploadCall(callList);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        } finally {
                            if (null != cursor) {
                                cursor.close();
                            }
                        }

                    } catch (Exception e) {
                        stopSelf();
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void uploadCall(List<CallModel> callList) {

        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "";
        int length = 0;

        try {
            System.out.println("remaining calllsss  " + remainingCallCount + "   " +callCount+"     "+ callList.size());
            if ((callList.size() / ((remainingCallCount + 1) * 50)) > 0) {
                length = 50;
            } else {
                length = callList.size() - ((remainingCallCount) * 50);
            }
            for (int count = (remainingCallCount * 50); count < ((remainingCallCount * 50) + length); count++) {
                if ((remainingCallCount * 50) == count) {

                    jsonInString = "[" + mapper.writeValueAsString(callList.get(count)) + ",";
                } else if (count == ((remainingCallCount * 50) + length) - 1) {
                    date = callList.get(count).getTime();
                    System.out.println("timeeeee   "+date);
                    jsonInString = jsonInString + mapper.writeValueAsString(callList.get(count)) + "]";
                } else {
                    jsonInString = jsonInString + mapper.writeValueAsString(callList.get(count)) + ",";
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
                    isSyncInProgress = true;

                    ApiCall.getInstance().makePostRequestContact(this, ApiConstants.API_ADDRESS.CALL_SYNC.path, jsonInString, header, this, 103, userId, Utility.getImei(this));

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onResponse(String response, int requestCode) throws IOException {
        isSyncInProgress = false;

        if (requestCode == 103) {
            int res = Integer.parseInt(response);
            if (res != 0 && res == 200) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("syncDate", date);
                editor.apply();
                if(callCount == remainingCallCount) {
                        stopSelf();
                    } else {
                        remainingCallCount++;
                        uploadCall(callList);
                    }
            } else {
               stopSelf();
            }
        }
    }

    @Override
    public void onError(String error, int errorCode, int requestCode) {
        isSyncInProgress = false;
        stopSelf();
    }
}
