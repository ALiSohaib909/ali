package com.pins.infinity.activity;

import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.appcenter.AppCenter;
import com.pins.infinity.R;
import com.pins.infinity.model.ProfileBaseModel;
import com.pins.infinity.utility.AppSharedPrefrence;
import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 * Created by bimalchawla on 18/3/17.
 */

public class FullImageActivity extends AppCompatActivity {
    private Toolbar toolbar;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        try {
            String response = AppSharedPrefrence.getString(FullImageActivity.this, AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO);
            ProfileBaseModel profileBaseModel = new ObjectMapper().readValue(response, ProfileBaseModel.class);

            if (profileBaseModel != null) {
                // You can call any combination of these three methods
                AppCenter.setUserId(profileBaseModel.getResponse().getUser().getEmail());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        image = (ImageView) findViewById(R.id.full_image);
        toolbar = (Toolbar) findViewById(R.id.full_image_toolbar);
        if (toolbar != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            }
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            toolbar.setTitle(getIntent().getStringExtra("fileName"));
            toolbar.setTitleTextColor(getResources().getColor(R.color.color_title));
            toolbar.setNavigationIcon(R.drawable.back_icn);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        Picasso.with(FullImageActivity.this)
                .load(getIntent().getStringExtra("imageUrl"))
                .placeholder(R.drawable.placeholder) // optional
                .error(R.drawable.placeholder)         // optional
                .into(image);

    }
}
