package com.pins.infinity.activity;

import android.os.Bundle;
import android.os.Handler;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.appcenter.AppCenter;
import com.pins.infinity.R;
import com.pins.infinity.adapters.ViewPagerAdapter;
import com.pins.infinity.model.ProfileBaseModel;
import com.pins.infinity.utility.AppSharedPrefrence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by shri.kant on 26-10-2016.
 */
public class ActivityRemoteLock extends AppCompatActivity {
    final Handler handler = new Handler();
    final long DELAY_MS = 3000;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000; // time in milliseconds between successive task executions.
    ImageView back;
    ViewPagerAdapter adapter;
    ArrayList<Integer> images = new ArrayList<>();
    Timer timer;
    private Button btnActive;
    private ViewPager viewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_lock);
        btnActive = (Button) findViewById(R.id.active);

        try {
            images.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

        images.add(R.drawable.antitheft_slide1);
        images.add(R.drawable.antitheft_slide2);

        viewpager = (ViewPager)findViewById(R.id.viewpager);

        adapter = new ViewPagerAdapter(images,ActivityRemoteLock.this);
        viewpager.setAdapter(adapter);
         /*After setting the adapter use the timer */

        final Runnable Update = new Runnable() {
            public void run() {
                viewpager.setCurrentItem(viewpager.getCurrentItem()+1);
            }
        };

        timer = new Timer(); // This will create a new Thread
        timer .schedule(new TimerTask() { // task to be scheduled

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
                timer .schedule(new TimerTask() { // task to be scheduled

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

        back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0,0);
            }
        });
//        btnActive.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(btnActive.getText().toString().equalsIgnoreCase("ACTIVE")) {
//                    AppSharedPrefrence.putBoolean(ActivityRemoteLock.this, AppSharedPrefrence.PREF_KEY.IS_LOCK_ACTIVE, false);
//                    btnActive.setText("INACTIVE");
//                } else {
//                    KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
//                    boolean lockStatus = false;
//                    if (Build.VERSION.SDK_INT >= 23) {
//                        lockStatus = keyguardManager.isDeviceSecure();
//                    } else {
//                        lockStatus = keyguardManager.isKeyguardSecure();
//                    }
//
//                    if(!lockStatus) {
//                        Intent intent = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
//                        startActivity(intent);
//                    } else {
//                        AppSharedPrefrence.putBoolean(ActivityRemoteLock.this, AppSharedPrefrence.PREF_KEY.IS_LOCK_ACTIVE, true);
//                        btnActive.setText("ACTIVE");
//                    }
//                }
//            }
//        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            String response = AppSharedPrefrence.getString(ActivityRemoteLock.this, AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO);
            ProfileBaseModel profileBaseModel = new ObjectMapper().readValue(response, ProfileBaseModel.class);

            if (profileBaseModel != null) {
                // You can call any combination of these three methods
                AppCenter.setUserId(profileBaseModel.getResponse().getUser().getEmail());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        boolean lock = AppSharedPrefrence.getBoolean(ActivityRemoteLock.this, AppSharedPrefrence.PREF_KEY.IS_LOCK_ACTIVE);
//
//        if (lock) {
//            btnActive.setText("INACTIVE");
//        } else {
//            btnActive.setText("ACTIVE");
//        }
//
//        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
//        boolean lockStatus = false;
//        if (Build.VERSION.SDK_INT >= 23) {
//            lockStatus = keyguardManager.isDeviceSecure();
//        } else {
//            lockStatus = keyguardManager.isKeyguardSecure();
//        }
//
//        if(!lockStatus) {
//            Intent intent = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
//            startActivity(intent);
//        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0,0);

    }
}
