package com.pins.infinity.activity;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.appcenter.AppCenter;
import com.pins.infinity.R;
import com.pins.infinity.adapters.DividerItemDecoration;
import com.pins.infinity.adapters.ScanHistoryAdapter;
import com.pins.infinity.application.MyApplication;
import com.pins.infinity.model.ProfileBaseModel;
import com.pins.infinity.model.ScanModel;
import com.pins.infinity.utility.AppSharedPrefrence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by bimalchawla on 15/2/17.
 */

public class ScanHistoryActivity extends AppCompatActivity {

    private Toolbar toolbar;
    RecyclerView recyclerView;
    List<ScanModel> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_history);

        try {
            String response = AppSharedPrefrence.getString(ScanHistoryActivity.this, AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO);
            ProfileBaseModel profileBaseModel = new ObjectMapper().readValue(response, ProfileBaseModel.class);

            if (profileBaseModel != null) {
                // You can call any combination of these three methods
                AppCenter.setUserId(profileBaseModel.getResponse().getUser().getEmail());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar_scan_history);
        recyclerView = (RecyclerView) findViewById(R.id.scan_history_recycler_view);
        list = new ArrayList<>();

        if (toolbar != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            }
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            toolbar.setTitle("Scan history");
            toolbar.setTitleTextColor(getResources().getColor(R.color.color_title));
            toolbar.setNavigationIcon(R.drawable.back_icn);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        RealmQuery query = MyApplication.realm.where(ScanModel.class);
        RealmResults results = query.findAll();

        list.addAll(results);

        ScanHistoryAdapter mAdapter = new ScanHistoryAdapter(ScanHistoryActivity.this, list);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
//        recyclerView.addItemDecoration(
//                new HorizontalDividerItemDecoration.Builder(ScanHistoryActivity.this)
//                        .color(getResources().getColor(R.color.color_title))
//                        .sizeResId(R.dimen.one)
//                        .marginResId(R.dimen.ten, R.dimen.ten)
//                        .build());
        recyclerView.setAdapter(mAdapter);
    }

}
