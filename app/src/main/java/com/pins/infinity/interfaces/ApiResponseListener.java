package com.pins.infinity.interfaces;

import android.net.Uri;

import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

/**
 * Created by shri.kant on 18-11-2016.
 */
public interface ApiResponseListener {

    void onResponse(String response, int requestCode) throws IOException;
    void onError(String error, int errorCode, int requestCode);
}
