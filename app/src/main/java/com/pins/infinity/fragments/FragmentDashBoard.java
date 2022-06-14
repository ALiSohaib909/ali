package com.pins.infinity.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pins.infinity.R;

/**
 * Created by shri.kant on 08-11-2016.
 */
public class FragmentDashBoard extends Fragment {
    private View view = null;
    private WebView webview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dashboard,
                container, false);

       initIds();

        webview.loadUrl("http://underconstruction.website/is/pins_main/");

        // Enable Javascript
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // Force links and redirects to open in the WebView instead of in a browser
        webview.setWebViewClient(new WebViewClient());
//        initClickListners();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void initIds() {
        webview = view.findViewById(R.id.webview);


    }




}