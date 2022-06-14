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

/**
 * Created by bimalchawla on 5/1/17.
 */

public class PasswordChangeActivity extends AppCompatActivity implements View.OnClickListener,ApiResponseListener {

    private ImageView back;
    private View submitView;
    private ProgressDialog progressDialog;
    private EditText currentPassword, newPassword, confirmPassword;
    private String currPass, newPass, confirmPass, token = "", userId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_password_change);
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
        submitView = findViewById(R.id.btnSubmitChangePassword);

        currentPassword = (EditText) findViewById(R.id.currentPassword);
        newPassword = (EditText) findViewById(R.id.newPassword);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);

    }

    private void initClickListners() {
        submitView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSubmitChangePassword:
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

        currPass = currentPassword.getText().toString().trim();
        newPass = newPassword.getText().toString().trim();
        confirmPass = confirmPassword.getText().toString().trim();

        if (currPass.length() < 0 || currPass.length() == 0) {
            Utility.showToast(this, "Please enter your current password");
            return false;
        } else if (newPass == null || newPass.length() == 0) {
            Utility.showToast(this, "Please enter your new password");
            return false;

        } else if (confirmPass == null || confirmPass.length() == 0) {
            Utility.showToast(this, "Please enter your confirm password");
            return false;

        } else if (!confirmPass.equalsIgnoreCase(newPass)) {
            Utility.showToast(this, "Password mismatch");
            return false;

        }
        return true;

    }

    private void ChangePassword() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        HashMap<String, String> params = new HashMap<>();
        params.put("current_password", currPass);
        params.put("new_password", newPass);

        try {
            String response = AppSharedPrefrence.getString(this, AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO);
            ProfileBaseModel profileBaseModel = new ObjectMapper().readValue(response, ProfileBaseModel.class);

            if (profileBaseModel != null) {
                token = profileBaseModel.getResponse().getToken();
                userId = profileBaseModel.getResponse().getUser().getAccountId();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, String> header = new HashMap<>();
        header.put(ApiConstants.AUTH_HEADER_KEY, ApiConstants.AUTH_HEADER_VALUE);
        header.put(ApiConstants.AUTH_HEADER_KEY_SEC, ApiConstants.AUTH_HEADER_KEY_SEC_VALUE);
        header.put(ApiConstants.AUTH_TOKEN_KEY, token);


        ApiCall.getInstance().makePostRequest(ApiConstants.API_ADDRESS.CHANGE_PASSWORD.path, params, header, this, 101, userId);


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
                    finish();

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
                    AppSharedPrefrence.clearAllPrefs(PasswordChangeActivity.this);
                    Intent login = new Intent(PasswordChangeActivity.this, ActivityLogin.class);
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

