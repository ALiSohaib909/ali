package com.pins.infinity.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avl.engine.AVLAppInfo;
import com.avl.engine.AVLEngine;
import com.avl.engine.AVLScanListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.appcenter.AppCenter;
import com.pins.infinity.R;
import com.pins.infinity.adapters.DividerItemDecoration;
import com.pins.infinity.adapters.ScanResultAdapter;
import com.pins.infinity.application.MyApplication;
import com.pins.infinity.model.ProfileBaseModel;
import com.pins.infinity.model.ScanModel;
import com.pins.infinity.model.ScanResultModel;
import com.pins.infinity.utility.AppConstants;
import com.pins.infinity.utility.AppSharedPrefrence;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bimalchawla on 21/1/17.
 */

public class VirusScanActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    LinearLayout resultLayout;
    private ImageView back;
    TextView txtProgress,text;
    ProgressBar progress;
    boolean complete;
    TextView scanDate;
    List virus;
    List<ScanResultModel> scanResult;
    int size = 0, count = 0;
    Button view_threat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virus_scan);

        try {
            String response = AppSharedPrefrence.getString(VirusScanActivity.this, AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO);
            ProfileBaseModel profileBaseModel = new ObjectMapper().readValue(response, ProfileBaseModel.class);

            if (profileBaseModel != null) {
                // You can call any combination of these three methods
                AppCenter.setUserId(profileBaseModel.getResponse().getUser().getEmail());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        initIds();
        scanResult = new ArrayList<>();
     virus = new ArrayList();
        if(AppConstants.scanModel != null) {
            ScanModel scanModel = AppConstants.scanModel;
            ScanResultModel model = new ScanResultModel();
            model.setName(getString(R.string.total_scan));
            model.setImage(R.drawable.total_scan);
            model.setNumber(scanModel.getTotalApps());
            scanResult.add(model);

            ScanResultModel firstModel = new ScanResultModel();
            firstModel.setName(getString(R.string.good_scan));
            firstModel.setImage(R.drawable.secured);
            firstModel.setNumber(scanModel.getGoodApps());
            scanResult.add(firstModel);

            ScanResultModel secondModel = new ScanResultModel();
            secondModel.setName(getString(R.string.bad_scan));
            secondModel.setImage(R.drawable.infected);
            secondModel.setNumber(scanModel.getInfectedApps());
            scanResult.add(secondModel);

            scanDate.setText(scanModel.getDate());

            virus = Arrays.asList(scanModel.getVirus().split("\\s*,\\s*"));
            finalUpdate();
            AppConstants.scanModel = null;
        } else {
            scanNow();
        }

    }


    private void initIds() {
        recyclerView = (RecyclerView) findViewById(R.id.virus_result_recycler_view);
        progress = (ProgressBar) findViewById(R.id.progressBar2);
        resultLayout = (LinearLayout) findViewById(R.id.virus_result_layout);
        txtProgress = (TextView) findViewById(R.id.txtProgress);
        scanDate = (TextView) findViewById(R.id.scan_date);
        view_threat = (Button) findViewById(R.id.view_threat);
        progress.setProgress(0);
        back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0,0);
            }
        });

    }


void scanNow() {
            List<String> pathList = new ArrayList<String>();
//    String extStore = System.getenv("EXTERNAL_STORAGE");
//    File sdCard = Environment.getExternalStorageDirectory();
//            pathList.add(extStore);
//    pathList.add(sdCard.getAbsolutePath());

//    System.out.println("files path......."+extStore+"    "+sdCard.getAbsolutePath()+"    "+Environment.getDataDirectory());
            AVLEngine.setSDCard(pathList);

            final AVLScanListener scanListener = new AVLScanListener() {
                @Override
                public void scanStart() {
// called when batch scan start
                    System.out.println("scan started......");
                }
                @Override
                public void scanCount(int count) {
//called before scan
                    System.out.println("scan count......"+count);
                    size = count;
                }

                @Override
                public void scanSingleIng(String s, String s1, String s2) {
                    System.out.println("scan single file ......"+s+"    "+s1+"    "+s2);
                }

                @Override
                public void scanSingleEnd(AVLAppInfo avlAppInfo) {
                    System.out.println("scan end......"+avlAppInfo.toString());
                    if (avlAppInfo.getDangerLevel() == 1) {
//malicious
//                        [appName=2048,packageName=com.c2048.mobidev,path=/data/app/com.c2048.mobidev-1/base.apk,virusName=AdWare/Android.FacebookAd.a[ads,gen],dangerLevel=2]

                        virus.add(avlAppInfo.getAppName());
                    } else if (avlAppInfo.getDangerLevel() == 2) {
//risk
                        virus.add(avlAppInfo.getAppName());
                    } else {
//safe
                    }
                    count++;

                    double d1 = count;
                    double d2 = size;
                    double d3 = (d1/d2)*100;
                    System.out.println("jjj     "+d1+"   "+d2+"   "+d3);
                    progress.setProgress((int) d3 );
                    txtProgress.setText((int) d3  + " %");
                }

                @Override
                public void scanStop() {
                    //called when scan stopped
                    System.out.println("scan stopped......");
                }
                @Override
                public void scanFinished() {
//called when batch scan finished
                    System.out.println("scan finished......");
                    complete = true;

                    prepareList();
                    finalUpdate();
                }
                @Override
                public void onCrash() {
//called when crashed
                    System.out.println("scan crashed......");
                    complete = true;
                }
            };

            AVLEngine.scanAllEx(VirusScanActivity.this, scanListener, AVLEngine.IGNORE_FLAG_SYSTEM);

        }


    void finalUpdate() {
        progress.setVisibility(View.GONE);
        txtProgress.setVisibility(View.GONE);

        resultLayout.setVisibility(View.VISIBLE);

        ScanResultAdapter mAdapter = new ScanResultAdapter(VirusScanActivity.this, scanResult,virus);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
//        recyclerView.addItemDecoration(
//                new HorizontalDividerItemDecoration.Builder(VirusScanActivity.this)
//                        .color(getResources().getColor(R.color.color_title))
//                        .sizeResId(R.dimen.one)
//                        .marginResId(R.dimen.ten, R.dimen.ten)
//                        .build());
        recyclerView.setAdapter(mAdapter);


        view_threat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    Bundle bun = new Bundle();
                    bun.putSerializable("virus", (Serializable) virus);
                    Intent intent = new Intent(VirusScanActivity.this, VirusListActivity.class);
                    intent.putExtras(bun);
                    startActivity(intent);
                    overridePendingTransition(0,0);


            }
        });
    }

    private void prepareList() {
        ScanResultModel model = new ScanResultModel();
        model.setName(getString(R.string.total_scan));
        model.setImage(R.drawable.total_scan);
        model.setNumber(size+"");
        scanResult.add(model);

        ScanResultModel firstModel = new ScanResultModel();
        firstModel.setName(getString(R.string.good_scan));
        firstModel.setImage(R.drawable.secured);
        firstModel.setNumber((size - virus.size())+"");
        scanResult.add(firstModel);

        ScanResultModel secondModel = new ScanResultModel();
        secondModel.setName(getString(R.string.bad_scan));
        secondModel.setImage(R.drawable.infected);
        secondModel.setNumber(virus.size()+"");
        scanResult.add(secondModel);

        long date = System.currentTimeMillis();
        //saving scan time
        AppSharedPrefrence.putLong(VirusScanActivity.this, AppSharedPrefrence.PREF_KEY.SCAN, date);

        SimpleDateFormat sdf = new SimpleDateFormat("MMM MM dd, yyyy h:mm a");
        String dateString = sdf.format(date);
        scanDate.setText(dateString);

            // Persist your data easily
        MyApplication.realm.beginTransaction();

        ScanModel book = MyApplication.realm.createObject(ScanModel.class);
        book.setTotalApps(size+"");
        book.setGoodApps((size - virus.size())+"");
        book.setInfectedApps(virus.size()+"");
        book.setDate(dateString);
        book.setVirus(join(virus, ","));

        System.out.println("book    "+book.toString());
        MyApplication.realm.commitTransaction();

    }

    String join(List<String> list, String delim) {

        StringBuilder sb = new StringBuilder();

        String loopDelim = "";

        for(String s : list) {

            sb.append(loopDelim);
            sb.append(s);

            loopDelim = delim;
        }

        System.out.println("st    "+sb.toString());

        return sb.toString();
    }
}
