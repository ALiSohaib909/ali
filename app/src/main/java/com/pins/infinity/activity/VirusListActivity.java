package com.pins.infinity.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.appcenter.AppCenter;
import com.pins.infinity.R;
import com.pins.infinity.adapters.DividerItemDecoration;
import com.pins.infinity.adapters.VirusResultAdapter;
import com.pins.infinity.model.ProfileBaseModel;
import com.pins.infinity.utility.AppSharedPrefrence;

import java.io.IOException;
import java.util.List;

/**
 * Created by bimalchawla on 12/2/17.
 */

public class VirusListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView back;
    List virus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virus_list);
        try {
            String response = AppSharedPrefrence.getString(VirusListActivity.this, AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO);
            ProfileBaseModel profileBaseModel = new ObjectMapper().readValue(response, ProfileBaseModel.class);

            if (profileBaseModel != null) {
                // You can call any combination of these three methods
                AppCenter.setUserId(profileBaseModel.getResponse().getUser().getEmail());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        virus = (List) getIntent().getSerializableExtra("virus");
        initIds();
    }

    private void initIds() {
        recyclerView = (RecyclerView) findViewById(R.id.virus_list_recycler_view);
        back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0,0);
            }
        });

        VirusResultAdapter mAdapter = new VirusResultAdapter(virus);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
//        recyclerView.addItemDecoration(
//                new HorizontalDividerItemDecoration.Builder(VirusListActivity.this)
//                        .color(getResources().getColor(R.color.color_title))
//                        .sizeResId(R.dimen.one)
//                        .marginResId(R.dimen.ten, R.dimen.ten)
//                        .build());
        recyclerView.setAdapter(mAdapter);

    }
}
