package com.pins.infinity.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pins.infinity.application.MyApplication;
import com.pins.infinity.interfaces.ApiResponseListener;
import com.pins.infinity.model.DBModel;
import com.pins.infinity.model.ProfileBaseModel;
import com.pins.infinity.utility.ApiCall;
import com.pins.infinity.utility.ApiConstants;
import com.pins.infinity.utility.AppSharedPrefrence;
import com.pins.infinity.utility.ServiceUtils;
import com.pins.infinity.utility.Utility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.RealmResults;

/**
 * Created by bimalchawla on 22/7/17.
 */

public class DbUploadService extends IntentService implements ApiResponseListener {

    List<DBModel> list;
    ProfileBaseModel profileBaseModel = null;
    private int count = 0;
    private File file;

    public DbUploadService() {
        super("DbUploadService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DbUploadService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        ServiceUtils.startForeground(this);

        if (Utility.checkString(AppSharedPrefrence.getString(MyApplication.getInstance(), AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO))) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    try {
                        RealmResults results = MyApplication.realm.where(DBModel.class).findAll();
                        list = new ArrayList<>();
                        list.addAll(results);
                        count = list.size() - 1;
                        if (list.size() == 0) {
                            stopSelf();
                        } else {
                            sendData();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void sendData() {
        try {

            String response = AppSharedPrefrence.getString(this, AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO);
            profileBaseModel = new ObjectMapper().readValue(response, ProfileBaseModel.class);

            Map<String, String> header = new HashMap<>();
            header.put(ApiConstants.AUTH_HEADER_KEY, ApiConstants.AUTH_HEADER_VALUE);
            header.put(ApiConstants.AUTH_HEADER_KEY_SEC, ApiConstants.AUTH_HEADER_KEY_SEC_VALUE);
            header.put(ApiConstants.AUTH_TOKEN_KEY, profileBaseModel.getResponse().getToken());
            System.out.println("token " + Build.MODEL + "  // " + Build.SERIAL);

            DBModel model = list.get(count);
            HashMap<String, String> params = new HashMap<>();
            params.put("imei", model.getImei());
            params.put("carrier", model.getCarrier());
            params.put("msisdn", model.getIsisdn());
            params.put("sim_id", model.getSimId());
            params.put("country_code", model.getCountryCode());
            params.put("country", model.getCountry());
            params.put("account_id", model.getAccountId());
            params.put("longitude", model.getLongitude());
            params.put("latitude", model.getLatitude());
            params.put("state", model.getState());
            params.put("address", model.getAddress());
            if (Utility.checkString(Build.MODEL)) {
                params.put("phone_model", Build.MODEL);
            } else {
                params.put("phone_model", "na");
            }

            if (Utility.checkString(Build.SERIAL)) {
                params.put("serial_number", Build.SERIAL);
            } else {
                params.put("serial_number", "na");
            }

            String path = model.getImagePath();
            if (Utility.checkString(path)) {
                file = new File(path);
            }
            if (model.getID() == 1) {
                ApiCall.getInstance().makeMultiPartRequest(ApiConstants.API_ADDRESS.THEFT_IMAGE.path, file, params, header, DbUploadService.this, 103, false);
            } else if (model.getID() == 3) {
                ApiCall.getInstance().makeMultiPartRequest(ApiConstants.API_ADDRESS.FLIGHT_MODE.path, file, params, header, DbUploadService.this, 103, false);
            } else if (model.getID() == 2) {
                ApiCall.getInstance().makeMultiPartRequest(ApiConstants.API_ADDRESS.SYNC_SIM.path, file, params, header, DbUploadService.this, 103, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(String response, int requestCode) throws IOException {
        System.out.println("ssssssss " + count + "   " + list.size());
        if ((count + 1) < list.size()) {
            count++;
            sendData();
        } else {
            try {
                System.out.println("ssssssss  clearing db");
                MyApplication.realm.beginTransaction();
                MyApplication.realm.delete(DBModel.class);
                MyApplication.realm.commitTransaction();
            } catch (Exception e) {
                e.printStackTrace();
            }
            stopSelf();
        }
    }

    @Override
    public void onError(String error, int erroCode, int requestCode) {
        if (Utility.isInternetOn(MyApplication.getInstance())) {
            if ((count + 1) < list.size()) {
                count++;
                sendData();
            } else {
                try {
                    System.out.println("sssssssserror  clearing db");
                    MyApplication.realm.beginTransaction();
                    MyApplication.realm.delete(DBModel.class);
                    MyApplication.realm.commitTransaction();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                stopSelf();
            }
        } else {
            stopSelf();
        }
    }
}
