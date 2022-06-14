package com.pins.infinity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
 * Created by bimalchawla on 29/1/17.
 */

public class ScanOptionsActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView back;
    private Button  history;
    private ImageView scan;
    private ViewPager viewpager;
    ViewPagerAdapter adapter;
    ArrayList<Integer> images = new ArrayList<>();
    final Handler handler = new Handler();
    Timer timer;
    final long DELAY_MS = 3000;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000; // time in milliseconds between successive task executions.

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_options);

        try {
            String response = AppSharedPrefrence.getString(ScanOptionsActivity.this, AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO);
            ProfileBaseModel profileBaseModel = new ObjectMapper().readValue(response, ProfileBaseModel.class);

            if (profileBaseModel != null) {
                // You can call any combination of these three methods
                AppCenter.setUserId(profileBaseModel.getResponse().getUser().getEmail());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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


        try {
            images.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        images.add(R.drawable.scan_slide1);
        images.add(R.drawable.scan_slide2);
        images.add(R.drawable.scan_slide3);

        viewpager = (ViewPager)findViewById(R.id.viewpager);

        adapter = new ViewPagerAdapter(images,ScanOptionsActivity.this);
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
    }

    private void initIds() {
        scan = (ImageView) findViewById(R.id.scan_now);
        history = (Button) findViewById(R.id.scan_history);
    }

    private void initClickListners() {
        scan.setOnClickListener(this);
        history.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scan_now:
                startActivity(new Intent(ScanOptionsActivity.this, VirusScanActivity.class));
                overridePendingTransition(0,0);
                break;

            case R.id.scan_history:
                startActivity(new Intent(ScanOptionsActivity.this, ScanHistoryActivity.class));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0,0);

    }
}
