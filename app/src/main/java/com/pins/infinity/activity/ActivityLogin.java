package com.pins.infinity.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.microsoft.appcenter.AppCenter;
import com.pins.infinity.BuildConfig;
import com.pins.infinity.R;
import com.pins.infinity.activity.payment.PaymentActivity;
import com.pins.infinity.interfaces.ApiResponseListener;
import com.pins.infinity.utility.ApiCall;
import com.pins.infinity.utility.ApiConstants;
import com.pins.infinity.utility.AppConstants;
import com.pins.infinity.utility.AppSharedPrefrence;
import com.pins.infinity.utility.DialogUtils;
import com.pins.infinity.utility.Logger;
import com.pins.infinity.utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.pins.infinity.database.SettingsManagerImpl.KEY_ACCESS_TOKEN;
import static com.pins.infinity.database.SettingsManagerImpl.KEY_IMEI;
import static com.pins.infinity.database.SettingsManagerImpl.KEY_USER_ID;
import static com.pins.infinity.database.SettingsManagerImpl.KEY_USER_SUBSCRIPTION_ID;

/**
 * Created by shri.kant on 09-11-2016.
 */
public class ActivityLogin extends AppCompatActivity implements View.OnClickListener, ApiResponseListener {
    private ImageView back;
    private View loginView;
    private String imei = "";
    private ProgressDialog progressDialog;
    private EditText input_email;
    private EditText input_password;
    private TextView forgotPassword;
    private String email_id_txt_Str = "";
    private String input_password_Str = "";

    private static final String RESPONSE = "response";
    private static final String USER = "user";
    private static final String TOKEN = "token";
    private static final String ACCOUNT_ID = "account_id";
    private static final String SUBSCRIPTION_ID = "subscription_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_login);
        imei = Utility.getImei(this);
        initIds();
        initClickListners();
        back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0,0);
            }
        });
    }

    private void initIds() {
        loginView = findViewById(R.id.btn_login);

        forgotPassword = (TextView) findViewById(R.id.forgot_password);
        input_email = (EditText) findViewById(R.id.input_email);
        input_password = (EditText) findViewById(R.id.input_password);

    }

    private void initClickListners() {
        loginView.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                if (Utility.isInternetOn(this)) {
                    if (validation()) {
                        authenticateUser();
                    }
                } else {
                    Utility.showToast(this, getResources().getString(R.string.error_no_internet_connection));
                }
                break;

            case R.id.forgot_password:
                startActivity(new Intent(ActivityLogin.this, InitRecoveryActivity.class));
                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0,0);

    }



    private boolean validation() {

        email_id_txt_Str = input_email.getText().toString().trim();
        input_password_Str = input_password.getText().toString().trim();

        if (email_id_txt_Str.length() < 0 || email_id_txt_Str.length() == 0) {
            Utility.showToast(this, "Please enter your Email ID.");
            return false;
        }
//        else if (email_id_txt_Str.length() > 0) {
//            if (!Utility.isValidEmail(email_id_txt_Str)) {
//                Utility.showToast(this, "Please enter valid Email ID.");
//                return false;
//            }
//        }
        else if (input_password_Str == null || input_password_Str.length() == 0) {
            Utility.showToast(this, "Please enter your password");
            return false;

        }
        return true;

    }

    private void authenticateUser() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        HashMap<String, String> params = new HashMap<>();
        params.put("identifier", email_id_txt_Str);
        params.put("password", input_password_Str);
        Map<String, String> header = new HashMap<>();
        header.put(ApiConstants.AUTH_HEADER_KEY, ApiConstants.AUTH_HEADER_VALUE);
        header.put(ApiConstants.AUTH_HEADER_KEY_SEC, ApiConstants.AUTH_HEADER_KEY_SEC_VALUE);

            params.put("imei", imei);
//            params.put("imei", "523797b5l45");
            ApiCall.getInstance().makePostRequest(this, ApiConstants.API_ADDRESS.AUTHENTICATE.path, params, header, this, 101);

    }

    private void addDevice(String token) {

        if (null == progressDialog) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }
        HashMap<String, String> params = new HashMap<>();
        Map<String, String> header = new HashMap<>();
        header.put(ApiConstants.AUTH_HEADER_KEY, ApiConstants.AUTH_HEADER_VALUE);
        header.put(ApiConstants.AUTH_HEADER_KEY_SEC, ApiConstants.AUTH_HEADER_KEY_SEC_VALUE);
        header.put(ApiConstants.AUTH_TOKEN_KEY, token);
  if (Utility.checkString(Build.MODEL)) {
            params.put("device_model", Build.MODEL);
        } else {
            params.put("device_model", "na");
        }
        if (Utility.checkString(Build.MANUFACTURER)) {
            params.put("device_name", Build.MANUFACTURER);
        } else {
            params.put("device_name", "na");
        }
        params.put("device", "android");
        if (Utility.checkString(Build.SERIAL)) {
            params.put("device_serial_no", Build.SERIAL);
        } else {
            params.put("device_serial_no", "na");
        }
        ApiCall.getInstance().makePostRequest(this, ApiConstants.API_ADDRESS.ADD_DEVICE.path + imei, params, header, this, 102);

    }

    @Override
    public void onResponse(String response, int requestCode) throws IOException {

        AppSharedPrefrence.putString(this, KEY_IMEI, imei);

        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (IllegalArgumentException e) {
            progressDialog = null;
        } finally {
            progressDialog = null;
        }
        if (response != null) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject != null) {
                    if (!jsonObject.optString("error").equalsIgnoreCase("true")) {
                        if (requestCode == 101) {
                            if (jsonObject.optString("code").equalsIgnoreCase("100")) {
                                JSONObject object = jsonObject.optJSONObject("response");
                                new AlertDialog.Builder(ActivityLogin.this)
                                        .setMessage(R.string.add_device_account)
                                        .setIcon(R.mipmap.ic_launcher)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                addDevice(object.optString("token"));
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                            }
                                        })
                                        .show();

                            } else {
                                saveUserData(this, response, imei, email_id_txt_Str);

                                // You can call any combination of these three methods
                                AppCenter.setUserId(email_id_txt_Str);
                                sendSms(this);
                                nextScreen();
                                sendToken(jsonObject.optJSONObject("response").optString("token"));
                            }
                        } else if (requestCode == 123 || requestCode == 102) {
                            if(requestCode == 102) {
                                saveUserData(this, response, imei, email_id_txt_Str);
                            }

                            nextScreen();
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(String error, int errorCode, int requestCode) {
        Logger.log("Login error");

        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (IllegalArgumentException e) {
            progressDialog = null;
        } finally {
            progressDialog = null;
        }
        if (error != null) {
            try {
                if (errorCode == 402) {
                    AppSharedPrefrence.clearAllPrefs(ActivityLogin.this);
                    Intent login = new Intent(ActivityLogin.this, ActivityLogin.class);
                    login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(login);
                    finish();
                } else {
                JSONObject jsonObject = new JSONObject(error);

                if (jsonObject != null) {
                    String message = jsonObject.getString("message");
                    Utility.showToast(this, message);
                }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void nextScreen() {
        String subscriptionId = AppSharedPrefrence.getString(this, KEY_USER_SUBSCRIPTION_ID);

       if (subscriptionId == null || subscriptionId.length() < 1) {
            Intent intent = new Intent(ActivityLogin.this, PaymentActivity.class);
            startActivity(intent);
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public static void sendSms(Context context) {
        String accountId = AppSharedPrefrence.getString(context, KEY_USER_ID);

        SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(BuildConfig.SMS_RECEIVER_NUMBER, null, accountId, null, null);
            Toast.makeText(context, "sms sent", Toast.LENGTH_SHORT).show();
    }

    private void sendToken(String token) {
        try {

                HashMap<String, String> params = new HashMap<>();
            params.put("device_token", AppSharedPrefrence.getString(this,
                    "device_token"));

                Map<String, String> header = new HashMap<>();
                header.put(ApiConstants.AUTH_HEADER_KEY, ApiConstants.AUTH_HEADER_VALUE);
                header.put(ApiConstants.AUTH_HEADER_KEY_SEC, ApiConstants.AUTH_HEADER_KEY_SEC_VALUE);
            header.put(ApiConstants.AUTH_TOKEN_KEY, token);

                ApiCall.getInstance().makePutRequestWithUrl(ActivityLogin.this,
                        ApiConstants.DEVICE_TOKEN + Utility.getImei(this), params,
                        header, this, 123, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveUserData(Context context, String response, String imei, String email) {

        AppConstants.saveValue(context, email);
        AppSharedPrefrence.putString(context, email, imei);
        AppSharedPrefrence.putString(context, KEY_IMEI, imei);
        AppSharedPrefrence.putString(context, AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO, response);

        try {
            JSONObject allResponseJson = new JSONObject(response);
            JSONObject responseJson = allResponseJson.optJSONObject(RESPONSE);
            JSONObject userJson = responseJson.optJSONObject(USER);

            String userId = userJson.optString(ACCOUNT_ID);
            String subscriptionId = userJson.optString(SUBSCRIPTION_ID);
            String token = responseJson.optString(TOKEN);

            AppSharedPrefrence.putString(context, KEY_USER_ID, userId);
            AppSharedPrefrence.putString(context, KEY_USER_SUBSCRIPTION_ID, subscriptionId);
            AppSharedPrefrence.putString(context, KEY_ACCESS_TOKEN, token);

        } catch (Exception e) {
            e.printStackTrace();
            Logger.log("saveUserData error");
            DialogUtils.showAlertOkDialog(context, null, context.getString(R.string.error_title));
        }
    }
}
