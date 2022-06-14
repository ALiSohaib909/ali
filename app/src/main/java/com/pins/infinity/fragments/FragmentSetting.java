package com.pins.infinity.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pins.infinity.BuildConfig;
import com.pins.infinity.R;
import com.pins.infinity.activity.ActivityDataBackup;
import com.pins.infinity.activity.ActivityPin;
import com.pins.infinity.activity.ActivityRemoteLock;
import com.pins.infinity.activity.ActivityRemoteTracking;
import com.pins.infinity.utility.Utility;

/**
 * Created by shri.kant on 08-11-2016.
 */
public class FragmentSetting extends Fragment implements View.OnClickListener {
    private View view = null;
    private View remote_lock_ll, remote_track_ll, data_back_ll, pinll, privacyLayout, termsLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting,
                container, false);

        initIds();
        initClickListners();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void initIds() {
        remote_lock_ll = view.findViewById(R.id.remote_lock_ll);
        remote_track_ll = view.findViewById(R.id.remote_track_ll);
        data_back_ll = view.findViewById(R.id.data_back_ll);
        pinll = view.findViewById(R.id.pinll);
        privacyLayout = view.findViewById(R.id.privacyll);
        termsLayout = view.findViewById(R.id.termsll);

    }

    private void initClickListners() {
        remote_lock_ll.setOnClickListener(this);
        remote_track_ll.setOnClickListener(this);
        data_back_ll.setOnClickListener(this);
        pinll.setOnClickListener(this);
        privacyLayout.setOnClickListener(this);
        termsLayout.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.remote_lock_ll:
                startActivity(new Intent(getActivity(), ActivityRemoteLock.class));
                break;
            case R.id.remote_track_ll:
                startActivity(new Intent(getActivity(), ActivityRemoteTracking.class));
                break;
            case R.id.data_back_ll:
                startActivity(new Intent(getActivity(), ActivityDataBackup.class));
                break;
            case R.id.pinll:
                startActivity(new Intent(getActivity(), ActivityPin.class));
                break;
            case R.id.privacyll:
                Utility.openBrowser(getActivity(), BuildConfig.TERMS_AND_CONDITIONS_URL);
                break;
            case R.id.termsll:
                Utility.openBrowser(getActivity(), BuildConfig.PRIVACY_URL);
                break;
            default:
                break;
        }
    }
}