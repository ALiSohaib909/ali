package com.pins.infinity.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pins.infinity.R;
import com.pins.infinity.interfaces.ApiResponseListener;
import com.pins.infinity.model.ProfileBaseModel;
import com.pins.infinity.utility.ApiCall;
import com.pins.infinity.utility.ApiConstants;
import com.pins.infinity.utility.AppSharedPrefrence;
import com.pins.infinity.utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.pins.infinity.R.id.recovery_mode;

/**
 * Created by bimalchawla on 5/1/17.
 */

public class InitRecoveryActivity extends AppCompatActivity implements View.OnClickListener,ApiResponseListener {
    private ImageView back;
    private View submitView;
    private ProgressDialog progressDialog;
    private EditText emailRecovery;
    private RadioGroup mode;
    private String email, recovery, token = "", imei = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_init_recovery);

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
        submitView = findViewById(R.id.btnSubmitInitRecovery);

        mode = (RadioGroup) findViewById(recovery_mode);
        emailRecovery = (EditText) findViewById(R.id.initRecoveryEmail);

    }

    private void initClickListners() {
        submitView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSubmitInitRecovery:
                if (Utility.isInternetOn(this)) {
                    if (validation()) {
                        ChangePassword();
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

        email = emailRecovery.getText().toString().trim();

        // get selected radio button from radioGroup
        int selectedId = mode.getCheckedRadioButtonId();

        // find the radiobutton by returned id
        RadioButton radio = ((RadioButton) findViewById(selectedId));

        System.out.println(""+radio.getId());
        if(radio.getId() == R.id.reset_code) {
            recovery = "reset-code";
        } else {
            recovery = "question";
        }


        if (email.length() == 0) {
            Utility.showToast(this, "Please enter your email/pin");
            return false;
        }
        return true;

    }

    private void ChangePassword() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        HashMap<String, String> params = new HashMap<>();
        params.put("recovery_mode", recovery);
        params.put("identifier", email);
        params.put("imei", imei);

        try {
            String response = AppSharedPrefrence.getString(this, AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO);
            ProfileBaseModel profileBaseModel = new ObjectMapper().readValue(response, ProfileBaseModel.class);

            if (profileBaseModel != null) {
                token = profileBaseModel.getResponse().getToken();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, String> header = new HashMap<>();
        header.put(ApiConstants.AUTH_HEADER_KEY, ApiConstants.AUTH_HEADER_VALUE);
        header.put(ApiConstants.AUTH_HEADER_KEY_SEC, ApiConstants.AUTH_HEADER_KEY_SEC_VALUE);
        header.put(ApiConstants.AUTH_TOKEN_KEY, token);

        ApiCall.getInstance().makePostRequest(this, ApiConstants.API_ADDRESS.INIT_RECOVERY.path, params, header, this, 101);
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
        if (response != null) {
            try {
                JSONObject jsonObject = new JSONObject(response);

                if (jsonObject != null) {
                    String message = jsonObject.getString("message");
                    Utility.showToast(this, message);
                    if(jsonObject.has("response")) {
                        String res = new JSONObject(jsonObject.optString("response")).optString("token");
                        Intent check = new Intent(InitRecoveryActivity.this, CheckRecoveryActivity.class);
                        check.putExtra("token", res);
                    startActivity(check);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
                    AppSharedPrefrence.clearAllPrefs(InitRecoveryActivity.this);
                    Intent login = new Intent(InitRecoveryActivity.this, ActivityLogin.class);
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
}