package com.pins.infinity.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.microsoft.appcenter.AppCenter;
import com.pins.infinity.R;
import com.pins.infinity.interfaces.ApiResponseListener;
import com.pins.infinity.utility.ApiCall;
import com.pins.infinity.utility.ApiConstants;
import com.pins.infinity.utility.AppConstants;
import com.pins.infinity.utility.AppSharedPrefrence;
import com.pins.infinity.utility.Iso2Phone;
import com.pins.infinity.utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.pins.infinity.database.SettingsManagerImpl.KEY_ACCESS_TOKEN;
import static com.pins.infinity.database.SettingsManagerImpl.KEY_IMEI;
import static com.pins.infinity.database.SettingsManagerImpl.KEY_USER_ID;

/**
 * Created by shri.kant on 09-11-2016.
 */
public class ActivitySignup extends AppCompatActivity implements View.OnClickListener, ApiResponseListener {
    private ImageView back;
    private View signUpView;
    private EditText input_fname, input_lname, input_email, inputConfirmEmail, mob_no, input_password, confirmPassword, alternativePhone;
    private String input_fname_Str = "";
    private String input_lname_Str = "";
    private String mobile_txt_Str = "";
    private String email_id_txt_Str = "";
    private String confirm_email_id_txt_Str = "";
    private String input_password_Str = "", confirmPAsswordText;
    private String countryCodePhone = "";
    private String countryName = "";
    private String countryCode = "";
    private String imei = "";
    private ProgressDialog progressDialog;

    public static final String RESPONSE = "response";
    public static final String USER = "user";
    public static final String TOKEN = "token";
    public static final String ACCOUNT_ID = "account_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_signup);

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

        getCountryInfoFromPhone();
    }

    private void getCountryInfoFromPhone() {

        String count = "";
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        countryCode = tm.getSimCountryIso();
        if(!Utility.checkString(countryCode)) {
            countryCode="ng";
        }
        Locale loc = new Locale("", countryCode);
        countryName = loc.getDisplayCountry();

        count = tm.getNetworkCountryIso();
        if (count == null && count.length() == 0)
            count = countryCode;
        countryCodePhone = Iso2Phone.getPhoneCode(count);

        if (tm != null)
            imei = tm.getDeviceId();
        if (imei == null || imei .length() == 0)
            imei = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

    }

    private void initIds() {
        signUpView = findViewById(R.id.btn_signup);
        input_fname = (EditText) findViewById(R.id.input_fname);
        input_lname = (EditText) findViewById(R.id.input_lname);
        input_email = (EditText) findViewById(R.id.input_email);
        inputConfirmEmail = (EditText) findViewById(R.id.input_confirm_email);
        mob_no = (EditText) findViewById(R.id.mob_no);
        input_password = (EditText) findViewById(R.id.input_password);
        confirmPassword = (EditText) findViewById(R.id.confirm_password);
        alternativePhone = (EditText) findViewById(R.id.alternative_number);
    }

    private void initClickListners() {
        signUpView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_signup:

                if (Utility.isInternetOn(this)) {
                    if (validation()) {
                        createUser();
                    }
                } else {
                    Utility.showToast(this, getResources().getString(R.string.error_no_internet_connection));
                }

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

        input_fname_Str = input_fname.getText().toString().trim();
        input_lname_Str = input_lname.getText().toString().trim();
        mobile_txt_Str = mob_no.getText().toString().trim();
        email_id_txt_Str = input_email.getText().toString().trim();
        confirm_email_id_txt_Str = inputConfirmEmail.getText().toString().trim();
        input_password_Str = input_password.getText().toString().trim();
        confirmPAsswordText = confirmPassword.getText().toString().trim();


        if (input_fname_Str == null || input_fname_Str.length() == 0) {
             Utility.showToast(this, "Please enter your first name");
            return false;
        } else if (input_lname_Str == null || input_lname_Str.length() == 0) {
            Utility.showToast(this, "Please enter your last name");
            return false;
        } else if (email_id_txt_Str.length() == 0) {
            Utility.showToast(this, "Please enter your Email ID.");
            return false;
        } else if (email_id_txt_Str.length() > 0 && !Utility.isValidEmail(email_id_txt_Str)) {
            Utility.showToast(this, "Please enter valid Email ID.");
            return false;
        } else if (confirm_email_id_txt_Str.length() == 0) {
            Utility.showToast(this, "Please enter confirm Email ID.");
            return false;
        } else if (confirm_email_id_txt_Str.length() > 0 && !Utility.isValidEmail(confirm_email_id_txt_Str)) {
            Utility.showToast(this, "Please enter valid confirm Email ID.");
            return false;
        } else if (!email_id_txt_Str.equals(confirm_email_id_txt_Str)) {
            Utility.showToast(this, "Email mismatch");
            return false;
        } else if (mobile_txt_Str.trim().length() < 0 || mobile_txt_Str.length() == 0) {
            Utility.showToast(this, "Please enter your Mobile Number.");
            return false;
        } else if (mobile_txt_Str.trim().length() > 11 || mobile_txt_Str.trim().length() < 11) {
            Utility.showToast(this, "Please enter a valid 11 digit Mobile Number.");
            return false;

        } else if (input_password_Str == null || input_password_Str.length() == 0) {
            Utility.showToast(this, "Please enter your password");
            return false;

        }  else if (confirmPAsswordText == null || confirmPAsswordText.length() == 0) {
            Utility.showToast(this, "Please enter confirm password");
            return false;

        }  else if (!input_password_Str.equals(confirmPAsswordText)) {
            Utility.showToast(this, "Passwords mismatch");
            return false;

        }
        return true;

    }

    private void createUser() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        HashMap<String, String> params = new HashMap<>();
        params.put("first_name", input_fname_Str);
        params.put("last_name", input_lname_Str);
        params.put("int_code", countryCodePhone);
        params.put("phone", mobile_txt_Str);
        params.put("email", email_id_txt_Str);
        params.put("password", input_password_Str);
        params.put("country", countryName);
        params.put("country_code", countryCode);
        params.put("alt_phone", alternativePhone.getText().toString());
        params.put("imei", imei);
//         params.put("imei", "52379745l98");//52379745l95
        params.put("device", AppConstants.DEVICE_TYPE);
        if (Utility.checkString(Build.MODEL)) {
            params.put("device_model", Build.MODEL);
        } else {
            params.put("device_model", "na");
        }
        if (Utility.checkString(Build.BRAND)) {
            params.put("device_name", Build.BRAND);
        } else {
            params.put("device_name", "na");
        }

        if (Utility.checkString(Build.SERIAL)) {
            params.put("device_serial_no", Build.SERIAL);
        } else {
            params.put("device_serial_no", "na");
        }

        Map<String, String> header = new HashMap<>();
        header.put(ApiConstants.AUTH_HEADER_KEY, ApiConstants.AUTH_HEADER_VALUE);
        header.put(ApiConstants.AUTH_HEADER_KEY_SEC, ApiConstants.AUTH_HEADER_KEY_SEC_VALUE);

        ApiCall.getInstance().makePostRequest(this, ApiConstants.API_ADDRESS.SIGN_UP.path, params, header, this, 101);
    }

    @Override
    public void onResponse(String response, int requestCode) throws IOException {

        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (IllegalArgumentException e) {
            progressDialog = null;
        } finally {
            progressDialog = null;
        }
        if (response != null && requestCode == 101) {
            try {
                JSONObject jsonObject = new JSONObject(response);

                if (jsonObject != null) {
                    String message = jsonObject.getString("message");
                    Utility.showToast(this, message);
                    if (!jsonObject.getString("error").equalsIgnoreCase("true")) {
                        sendtoken(jsonObject.optString("token"));

                        String userId = jsonObject.optJSONObject(RESPONSE).optJSONObject(USER).optString(ACCOUNT_ID);
                        String token = jsonObject.optJSONObject(RESPONSE).optString(TOKEN);
                        AppSharedPrefrence.putString(this, KEY_IMEI, imei);
                        AppSharedPrefrence.putString(this, email_id_txt_Str, imei);
                        AppSharedPrefrence.putString(this, KEY_USER_ID, userId);
                        AppSharedPrefrence.putString(this, KEY_ACCESS_TOKEN, token);
                        AppSharedPrefrence.putString(this, AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO, response);
                        //moving to security question screen
                        AppConstants.saveValue(ActivitySignup.this, email_id_txt_Str);
                        // You can call any combination of these three methods
                        AppCenter.setUserId(email_id_txt_Str);
                        startActivity(new Intent(this, SecurityQuestionActivity.class));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
//            Log.i("response", response);
    }

    @Override
    public void onError(String error, int errorCode, int requestCode) {
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
                    AppSharedPrefrence.clearAllPrefs(ActivitySignup.this);
                    Intent login = new Intent(ActivitySignup.this, ActivityLogin.class);
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


    private void sendtoken(String token) {
//        try {
//            String response1 = AppSharedPrefrence.getString(this, AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO);
//            ProfileBaseModel profileBaseModel = new ObjectMapper().readValue(response1, ProfileBaseModel.class);
//
//            if (profileBaseModel != null) {

                HashMap<String, String> params = new HashMap<>();
        params.put("device_token", AppSharedPrefrence.getString(this,
                "device_token"));

                Map<String, String> header = new HashMap<>();
                header.put(ApiConstants.AUTH_HEADER_KEY, ApiConstants.AUTH_HEADER_VALUE);
                header.put(ApiConstants.AUTH_HEADER_KEY_SEC, ApiConstants.AUTH_HEADER_KEY_SEC_VALUE);
        header.put(ApiConstants.AUTH_TOKEN_KEY, token);

                ApiCall.getInstance().makePutRequestWithUrl(ActivitySignup.this,
                        ApiConstants.DEVICE_TOKEN + Utility.getImei(this), params,
                        header, this, 123, null);

//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}