package com.pins.infinity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
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
public class ActivityDataBackup extends AppCompatActivity implements View.OnClickListener {
    private ImageView back;
    private View viewContactBackuo, pictureBackup;
    private ViewPager viewpager;
    ViewPagerAdapter adapter;
    ArrayList<Integer> images = new ArrayList<>();
    final Handler handler = new Handler();
    Timer timer;
    final long DELAY_MS = 3000;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000; // time in milliseconds between successive task executions.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_backup);

        try {
            String response = AppSharedPrefrence.getString(ActivityDataBackup.this, AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO);
            ProfileBaseModel profileBaseModel = new ObjectMapper().readValue(response, ProfileBaseModel.class);

            if (profileBaseModel != null) {
                // You can call any combination of these three methods
                AppCenter.setUserId(profileBaseModel.getResponse().getUser().getEmail());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            images.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

        images.add(R.drawable.cloud_backup_slide1);
        images.add(R.drawable.cloud_backup_slide2);
        images.add(R.drawable.cloud_backup_slide3);

        viewpager = (ViewPager)findViewById(R.id.viewpager);

        adapter = new ViewPagerAdapter(images,ActivityDataBackup.this);
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


        initIds();
        initClickListners();

    }


    private void initIds() {
        viewContactBackuo = findViewById(R.id.backup_contacts_btn);
        pictureBackup = findViewById(R.id.backup_pictures_btn);

    }

    private void initClickListners() {
        viewContactBackuo.setOnClickListener(this);
        pictureBackup.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backup_contacts_btn:
                startActivity(new Intent(this, ActivityContacts.class));
                break;

            case R.id.backup_pictures_btn:
                startActivity(new Intent(this, PicturesActivity.class));
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
}
