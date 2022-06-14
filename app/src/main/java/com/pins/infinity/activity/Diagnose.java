package com.pins.infinity.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.appcenter.AppCenter;
import com.pins.infinity.R;
import com.pins.infinity.adapters.ViewPagerAdapter;
import com.pins.infinity.interfaces.ApiResponseListener;
import com.pins.infinity.model.ProfileBaseModel;
import com.pins.infinity.utility.ApiCall;
import com.pins.infinity.utility.ApiConstants;
import com.pins.infinity.utility.AppSharedPrefrence;
import com.pins.infinity.utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by shri.kant on 26-10-2016.
 */
public class Diagnose extends AppCompatActivity implements ApiResponseListener {
    private ImageView back;
    private ViewPager viewpager;
    ViewPagerAdapter adapter;
    Button start_diagnose, start_diagnose_insure;
    ArrayList<Integer> images = new ArrayList<>();
    private ProgressDialog progressDialog;
    private String token = "";
    private String userId = "";
    final Handler handler = new Handler();
    Timer timer;
    final long DELAY_MS = 3000;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000; // time in milliseconds between successive task executions.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.diagnose);


        try {
            images.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

        images.add(R.drawable.diagnose_slide1);
        images.add(R.drawable.diagnose_slide2);

        viewpager = (ViewPager) findViewById(R.id.viewpager);
        start_diagnose = (Button) findViewById(R.id.start_diagnose);

        start_diagnose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getSesiionId();


            }
        });

        start_diagnose_insure = (Button) findViewById(R.id.start_diagnose_insure);
        start_diagnose_insure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Diagnose.this, DiagnoseInsure.class));
                overridePendingTransition(0, 0);
            }
        });

        adapter = new ViewPagerAdapter(images, Diagnose.this);
        viewpager.setAdapter(adapter);

        /*After setting the adapter use the timer */

        final Runnable Update = new Runnable() {
            public void run() {
                viewpager.setCurrentItem(viewpager.getCurrentItem() + 1);
            }
        };

        timer = new Timer(); // This will create a new Thread
        timer.schedule(new TimerTask() { // task to be scheduled

            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);

        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (timer != null)
                    timer.cancel(); // This will create a new Thread

                timer = new Timer(); // This will create a new Thread
                timer.schedule(new TimerTask() { // task to be scheduled

                    @Override
                    public void run() {
                        handler.post(Update);
                    }
                }, DELAY_MS, PERIOD_MS);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        try {
            String response = AppSharedPrefrence.getString(Diagnose.this, AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO);
            ProfileBaseModel profileBaseModel = new ObjectMapper().readValue(response, ProfileBaseModel.class);

            if (profileBaseModel != null) {
                // You can call any combination of these three methods
                AppCenter.setUserId(profileBaseModel.getResponse().getUser().getEmail());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (AppSharedPrefrence.getBoolean(Diagnose.this, AppSharedPrefrence.PREF_KEY.SESSION_STARTED)) {
            AppSharedPrefrence.putBoolean(Diagnose.this, AppSharedPrefrence.PREF_KEY.SESSION_STARTED, false);
        }

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

        if (requestCode == 211) {

            String session_id = "";

            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject != null) {
                        JSONObject sessionObject = jsonObject.getJSONObject("response");
                        session_id = sessionObject.getString("session_id");
                        AppSharedPrefrence.putString(Diagnose.this, AppSharedPrefrence.PREF_KEY.SESSION, session_id);

                    }
                    AppSharedPrefrence.putBoolean(Diagnose.this, AppSharedPrefrence.PREF_KEY.SESSION_STARTED, true);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == 201) {
            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject != null) {
                        String message = jsonObject.getString("message");
                        Utility.showToast(this, message);
                    }
                    AppSharedPrefrence.putString(Diagnose.this, AppSharedPrefrence.PREF_KEY.SESSION, "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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

                JSONObject jsonObject = new JSONObject(error);
                if (jsonObject != null) {
                    String message = jsonObject.getString("message");
                    Utility.showToast(this, message);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void getSesiionId() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

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

        //String url = "http://private-anon-5ec4da1eb1-pin1.apiary-mock.com"+ ApiConstants.DIAGNOSE  + "/"+ userId + "/" + Utility.getImei(this);
        String url = ApiConstants.BASE_URL + "diagnose/session/" + userId + "/" + Utility.getImei(this) + "/";
        ApiCall.getInstance().makeGetRequestWithUrl(Diagnose.this, url, null, header, this, 211);

    }

    private void closeDiagnoseSession() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

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

        //String url = "http://private-anon-5ec4da1eb1-pin1.apiary-mock.com"+ ApiConstants.DIAGNOSE  + "/"+ userId + "/" + Utility.getImei(this);
        String url = ApiConstants.BASE_URL + "diagnose/complete/" + userId + "/" + AppSharedPrefrence.getString(Diagnose.this, AppSharedPrefrence.PREF_KEY.SESSION) + "/";
        // String url = ApiConstants.urlBuilderCotactsWithTwoPara(ApiConstants.API_ADDRESS.DIAGNOSE.path, null, userId, Utility.getImei(this));
        ApiCall.getInstance().makeGetRequestWithUrl(Diagnose.this, url, null, header, this, 201);

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);

    }


}
