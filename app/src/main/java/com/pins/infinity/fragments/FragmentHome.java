package com.pins.infinity.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pins.infinity.R;
import com.pins.infinity.activity.ActivityDataBackup;
import com.pins.infinity.activity.ActivityGoogleMap;
import com.pins.infinity.activity.ActivityPin;
import com.pins.infinity.activity.ActivityRemoteLock;
import com.pins.infinity.activity.ActivityRemoteTracking;
import com.pins.infinity.activity.Diagnose;
import com.pins.infinity.activity.ScanOptionsActivity;
import com.pins.infinity.utility.AppSharedPrefrence;

import java.text.SimpleDateFormat;
import java.util.Calendar;

//import com.pins.infinity.activity.ActivityMap;

/**
 * Created by shri.kant on 26-10-2016.
 */
public class FragmentHome extends Fragment implements View.OnClickListener {
    private View view = null;
    private ImageView scan;
    private TextView lastScan;
    private View remote_lock_ll, remote_track_ll, data_back_ll, pinll, insureLayout, payLayout, googleLayout;

    /**
     * Return date in specified format.
     *
     * @param milliSeconds Date in milliseconds
     * @param dateFormat   Date format
     * @return String representing date in specified format
     */
    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home,
                container, false);

        initIds();
        initClickListners();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        long scanTime = AppSharedPrefrence.getLong(getActivity(), AppSharedPrefrence.PREF_KEY.SCAN);
        lastScan.setText("Last scanned: " + getDate(scanTime, "EEE MMM dd, yyyy h:mm a"));
    }

    private void initIds() {
        scan = (ImageView) view.findViewById(R.id.remote_lock_icon);
        remote_lock_ll = view.findViewById(R.id.remote_lock_ll);
        googleLayout = view.findViewById(R.id.remote_find_ll_google);
        remote_track_ll = view.findViewById(R.id.remote_track_ll);
//        data_back_ll = view.findViewById(R.id.data_back_ll);
//        pinll = view.findViewById(R.id.remote_wipe_layout);
        insureLayout = view.findViewById(R.id.insure_phone_layout);
        lastScan = (TextView) view.findViewById(R.id.last_scan);
    }

    private void initClickListners() {
        remote_lock_ll.setOnClickListener(this);
       // findLayout.setOnClickListener(this);
        googleLayout.setOnClickListener(this);
//        remote_track_ll.setOnClickListener(this);
//        data_back_ll.setOnClickListener(this);
     //   pinll.setOnClickListener(this);
      //  scan.setOnClickListener(this);
        scan.setClickable(false);
        scan.setFocusable(false);
        insureLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.remote_lock_ll:
                startActivity(new Intent(getActivity(), ActivityRemoteLock.class));
                getActivity().overridePendingTransition(0,0);
                break;
            /*case R.id.remote_find_ll:
                startActivity(new Intent(getActivity(), ActivityGoogleMap.class));
                break;*/
            case R.id.remote_find_ll_google:
                startActivity(new Intent(getActivity(), ActivityGoogleMap.class));
                getActivity().overridePendingTransition(0,0);
                break;
           /* case R.id.remote_track_ll:
                startActivity(new Intent(getActivity(), ActivityRemoteTracking.class));
                getActivity().overridePendingTransition(0,0);
                break;*/
           /* case R.id.data_back_ll:
                startActivity(new Intent(getActivity(), ActivityDataBackup.class));
                getActivity().overridePendingTransition(0,0);
                break;*/
           /* case R.id.remote_wipe_layout:
                startActivity(new Intent(getActivity(), ActivityPin.class));
                getActivity().overridePendingTransition(0,0);
                break;*/
         /*   case R.id.remote_lock_icon:
                startActivity(new Intent(getActivity(), ScanOptionsActivity.class));
                getActivity().overridePendingTransition(0,0);
                break;*/

            case R.id.insure_phone_layout:
                startActivity(new Intent(getActivity(), Diagnose.class));
                getActivity().overridePendingTransition(0,0);
                break;

            default:
                break;
        }
    }
}