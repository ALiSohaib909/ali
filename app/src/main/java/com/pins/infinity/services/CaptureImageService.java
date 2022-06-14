package com.pins.infinity.services;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.androidhiddencamera.CameraConfig;
import com.androidhiddencamera.CameraError;
import com.androidhiddencamera.HiddenCameraService;
import com.androidhiddencamera.HiddenCameraUtils;
import com.androidhiddencamera.config.CameraFacing;
import com.androidhiddencamera.config.CameraImageFormat;
import com.androidhiddencamera.config.CameraResolution;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.pins.infinity.BuildConfig;
import com.pins.infinity.application.MyApplication;
import com.pins.infinity.database.SettingsManager;
import com.pins.infinity.interfaces.ApiResponseListener;
import com.pins.infinity.model.DBModel;
import com.pins.infinity.model.ProfileBaseModel;
import com.pins.infinity.model.SmsMessageModel;
import com.pins.infinity.repositories.UserRepository;

import com.pins.infinity.usecases.DangerTriggeredUseCase;
import com.pins.infinity.utility.ApiConstants;
import com.pins.infinity.utility.AppConstants;
import com.pins.infinity.utility.AppSharedPrefrence;
import com.pins.infinity.utility.PhotoUtils;
import com.pins.infinity.utility.SharedPreferences.Const;
import com.pins.infinity.utility.Utility;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import io.reactivex.disposables.Disposable;
import kotlin.Lazy;

import static com.pins.infinity.utility.SharedPreferences.Const.TAG_FLIGHT_MODE;
import static com.pins.infinity.utility.SharedPreferences.Const.TAG_SMS;
import static org.koin.java.KoinJavaComponent.inject;

/**
 * Created by bimalchawla on 29/3/17.
 */

public class CaptureImageService extends HiddenCameraService implements ApiResponseListener {
    private static final String TAG = "ImageCapture";
    private static final String TAG_ADDRESS = "address_from_shared";
    private static final int CAPTURE_TIME = 7000;

    public Lazy<SettingsManager> settings = inject(SettingsManager.class);
    public Lazy<UserRepository> userRepository = inject(UserRepository.class);
    public Lazy<DangerTriggeredUseCase> dangerTriggeredUseCase = inject(DangerTriggeredUseCase.class);

    private Disposable checkSubscriptionDisposable;

    String token;
    boolean isTheft;
    boolean isFlightMode;
    SmsMessageModel smsMessage;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if(checkSubscriptionDisposable != null) {
            checkSubscriptionDisposable.dispose();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final Runnable isCaptureImageRunnable = new Runnable() {
            public void run() {
                AppConstants.IS_SERVICE_RUNNING = false;
            }
        };
        new Handler().postDelayed(isCaptureImageRunnable, CAPTURE_TIME);

        checkSubscriptionDisposable = userRepository.getValue().initCheck()
                .subscribe(isSubscriptionValid -> {
                    if (isSubscriptionValid) {
                        runCamera(intent);
                    } else {
                        stopForeground(true);
                        stopSelf();
                    }
                },
                    onError -> {
                        onError.printStackTrace();
                        AppConstants.IS_SERVICE_RUNNING = false;
                    }
                );

        return START_NOT_STICKY;
    }

    private void runCamera(Intent intent) {
        if (Utility.checkString(AppSharedPrefrence.getString(MyApplication.getInstance(), AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO))) {
            if (intent.hasExtra(Const.THEFT_KEY) && intent.getBooleanExtra(Const.THEFT_KEY, false)) {
                isTheft = true;
                intent.removeExtra(Const.THEFT_KEY);
            } else if (intent.hasExtra(Const.FLIGHTMODE_KEY) && intent.getBooleanExtra(Const.FLIGHTMODE_KEY, false)) {
                isFlightMode = true;
            }
            if (intent.hasExtra(Const.SMS_MESSAGE_KEY)) {
                String message = intent.getStringExtra(Const.SMS_MESSAGE_KEY);
                smsMessage = new Gson().fromJson(message, SmsMessageModel.class);
                intent.removeExtra(Const.SMS_MESSAGE_KEY);
            }
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    if (HiddenCameraUtils.canOverDrawOtherApps(this)) {
                        CameraConfig cameraConfig = new CameraConfig()
                                .getBuilder(this)
                                .setCameraFacing(CameraFacing.FRONT_FACING_CAMERA)
                                .setCameraResolution(CameraResolution.MEDIUM_RESOLUTION)
                                .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                                .build();

                        startCamera(cameraConfig);

                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    takePicture();
                                } catch (RuntimeException e) {
                                    e.printStackTrace();
                                    callService(null);
                                }
                            }
                        }, 5000);
                    } else {
                        AppConstants.IS_SERVICE_RUNNING = false;
//                    Open settings to grant permission for "Draw other apps".
                        HiddenCameraUtils.openDrawOverPermissionSetting(this);
                    }
                } else {
                    AppConstants.IS_SERVICE_RUNNING = false;
                    //TODO Ask your parent activity for providing runtime permission
                    Toast.makeText(this, "Camera permission not available", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                callService(null);
            }
        }
    }

    @Override
    public void onImageCapture(@NonNull File imageFile) {
        stopCamera();
        callService(PhotoUtils.preparePhotoForUpload(imageFile, true));
    }

    private void callService(Bitmap bitmap) {
        String imei = null, network = null, subscriber = null, serial = null;
        File file = null;
        ProfileBaseModel profileBaseModel = null;
        try {

            String response = AppSharedPrefrence.getString(this, AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO);
            profileBaseModel = new ObjectMapper().readValue(response, ProfileBaseModel.class);
            TelephonyManager telemamanger = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

            if (telemamanger != null) {
                imei = telemamanger.getDeviceId();
                network = telemamanger.getNetworkOperatorName();
                subscriber = telemamanger.getLine1Number();
                serial = telemamanger.getSimSerialNumber();
            }

            if (imei == null || imei.length() == 0)
                imei = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

            if (null != bitmap) {
                file = Utility.getFileFromBitmap(bitmap);
                Log.d(TAG, file.getAbsolutePath());
            }

        } catch (Exception e) {
            AppConstants.IS_SERVICE_RUNNING = false;
        }

        if (profileBaseModel != null) {

            try {
                token = profileBaseModel.getResponse().getToken();

                SharedPreferences sharedPref = getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);

                String latitude = sharedPref.getString(Const.LATITUDE, "");
                String longitude = sharedPref.getString(Const.LONGITUDE, "");
                String address = sharedPref.getString(Const.ADDRESS, "");
                String country = sharedPref.getString(Const.COUNTRY, "");
                String countryCode = sharedPref.getString(Const.COUNTRY_CODE, "");
                String city = sharedPref.getString(Const.CITY, "");

                Log.d(TAG_ADDRESS, city + AppConstants.city + countryCode + address);

                Map<String, String> header = new HashMap<>();
                header.put(ApiConstants.AUTH_HEADER_KEY, ApiConstants.AUTH_HEADER_VALUE);
                header.put(ApiConstants.AUTH_HEADER_KEY_SEC, ApiConstants.AUTH_HEADER_KEY_SEC_VALUE);
                header.put(ApiConstants.AUTH_TOKEN_KEY, token);
                System.out.println("token " + Build.MODEL + "  // " + Build.SERIAL + "     //   " + token);

                DangerTriggeredUseCase.PresenterParam params = new DangerTriggeredUseCase.PresenterParam(
                        Utility.checkString(imei) ? imei : "na",
                        Utility.checkString(network) ? network : "na",
                        Utility.checkString(subscriber) ? subscriber : "na",
                        Utility.checkString(serial) ? serial : "na",
                        Utility.checkString(country) ? country : "nigeria",
                        Utility.checkString(countryCode) ? countryCode : "ng",
                        profileBaseModel.getResponse().getUser().getAccountId(),
                        Utility.checkString(latitude) ? latitude : "0.0",
                        Utility.checkString(longitude) ? longitude : "0.0",
                        Utility.checkString(city) ? city : "na",
                        Utility.checkString(address) ? address : "na",
                        Utility.checkString(Build.MODEL) ? Build.MODEL : "na",
                        Utility.checkString(Build.SERIAL) ? Build.SERIAL : "na"
                );

                Log.d(TAG, "location to api from shared preferences: Lat & Lng :" + latitude + "  " + longitude);

                if (!settings.getValue().isSubscriptionValid()) {
                    return;
                }

                if (Utility.isInternetOn(this)) {
                    if (isTheft) {
                        dangerTriggeredUseCase.getValue().execute(getDangerParams(params, DangerTriggeredUseCase.DangerType.THEFT_DETECT)).subscribe();
                    } else if (isFlightMode) {
                        dangerTriggeredUseCase.getValue().buildUseCase(getDangerParams(params, DangerTriggeredUseCase.DangerType.FLIGHT_MODE_DETECT)).subscribe();;
                        Log.v(TAG_FLIGHT_MODE, "FLIGHT_MODE api triggered");
                    } else {
                        dangerTriggeredUseCase.getValue().buildUseCase(getDangerParams(params, DangerTriggeredUseCase.DangerType.SIM_DETECT)).subscribe();;
                        Log.v(TAG_SMS, "SYNC_SIM api triggered");
                    }
                } else {
                    // Persist your data easily
                    MyApplication.realm.beginTransaction();

                    DBModel model = MyApplication.realm.createObject(DBModel.class);

                    if (isTheft) {
                        model.setID(1);
                    } else if (isFlightMode) {
                        model.setID(3);
                        Log.v(TAG_FLIGHT_MODE, "FLIGHT_MODE set id");

                    } else {
                        model.setID(2);
                    }
                    model.setImei(params.getImei());
                    model.setCarrier(params.getCarrier());
                    model.setIsisdn(params.getMsisdn());
                    model.setSimId(params.getSimId());
                    model.setCountry(params.getCountry());
                    model.setCountryCode(params.getCountryCode());
                    model.setAccountId(params.getAccountId());
                    model.setLatitude(params.getLatitude());
                    model.setLongitude(params.getLongitude());
                    model.setState(params.getState());
                    model.setAddress(params.getAddress());

                    if (null != file) {
                        model.setImagePath(file.getAbsolutePath());
                    }

                    System.out.println("model    " + model.toString());
                    MyApplication.realm.commitTransaction();
                    AppConstants.IS_SERVICE_RUNNING = false;
                    stopSelf();
                }
            } catch (Exception e) {
                AppConstants.IS_SERVICE_RUNNING = false;
                stopSelf();
            }
        }
    }

    private DangerTriggeredUseCase.Param getDangerParams(
            DangerTriggeredUseCase.PresenterParam presenterParam,
            DangerTriggeredUseCase.DangerType type
    ) {
        DangerTriggeredUseCase.Param params = new DangerTriggeredUseCase.Param(
                presenterParam.getImei(),
                presenterParam.getCarrier(),
                presenterParam.getMsisdn(),
                presenterParam.getSimId(),
                presenterParam.getCountry(),
                presenterParam.getCountryCode(),
                presenterParam.getAccountId(),
                presenterParam.getLatitude(),
                presenterParam.getLongitude(),
                presenterParam.getState(),
                presenterParam.getAddress(),
                presenterParam.getModel(),
                presenterParam.getSerialNumber(),
                type);
        return params;
    }

    @Override
    public void onCameraError(@CameraError.CameraErrorCodes int errorCode) {
        switch (errorCode) {
            case CameraError.ERROR_CAMERA_OPEN_FAILED:
                //Camera open failed. Probably because another application
                //is using the camera
                Toast.makeText(this, "Cannot open camera.", Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE:
                //camera permission is not available
                //Ask for the camra permission before initializing it.
                Toast.makeText(this, "Camera permission not available.", Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_OVERDRAW_PERMISSION:
                //Display information dialog to the user with steps to grant "Draw over other app"
                //permission for the app.
                HiddenCameraUtils.openDrawOverPermissionSetting(this);
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA:
                Toast.makeText(this, "Your device does not have front camera.", Toast.LENGTH_LONG).show();
                break;
        }
        AppConstants.IS_SERVICE_RUNNING = false;
        stopSelf();
    }

    @Override
    public void onResponse(String response, int requestCode) throws IOException {

        if (requestCode == 103) {

            try {
                JSONObject jsonObject = new JSONObject(response);

                if (jsonObject.getString("error").equalsIgnoreCase("true")) {
                    return;
                }
                if (!isTheft && !isFlightMode && smsMessage != null && Utility.checkString(smsMessage.toString())) {
                    String detectId = getDetectId(jsonObject.getString("response"));
                    smsMessage.setDetectId(detectId);

                    SmsManager smsManager = SmsManager.getDefault();

                    smsManager.sendTextMessage(BuildConfig.SMS_RECEIVER_NUMBER, null,
                            getFormattedSmsMessage(), null, null);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        AppConstants.IS_SERVICE_RUNNING = false;
        stopSelf();
    }

    @NotNull
    private String getFormattedSmsMessage() {
        return new Gson().toJson(smsMessage)
                                .replaceAll(Pattern.quote("{"), "(")
                                .replaceAll(Pattern.quote("}"), ")");
    }


    private String getDetectId(String json) {
        String detectId = "0";
        try {
            JSONObject obj = new JSONObject(json);
            String detect = obj.getString("detect");
            detectId = new JSONObject(detect).getString("detect_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return detectId;
    }

    @Override
    public void onError(String error, int erroCode, int requestCode) {
        Log.v(TAG_SMS, "SMS api onError");

        if (error != null) {
            try {
                JSONObject jsonObject = new JSONObject(error);

                if (jsonObject != null && jsonObject.getString("error").equalsIgnoreCase("true")) {
                    Log.d("Sim", "error occured");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        AppConstants.IS_SERVICE_RUNNING = false;
        stopSelf();
    }

}