package com.pins.infinity.model;

import com.google.gson.annotations.SerializedName;
import com.pins.infinity.utility.SharedPreferences.Const;


/**
 * Created by Pavlo Melnyk on 29.08.2018.
 */
public class SmsMessageModel {
    @SerializedName(Const.ACCOUNT_ID)
    private String mAccountId;

    @SerializedName(Const.DETECT_ID)
    private String mDetectId;

    @SerializedName(Const.LATITUDE)
    private String mLatitude;

    @SerializedName(Const.LONGITUDE)
    private String mLongitude;

    @SerializedName(Const.TRIGGER)
    private String mTrigger;

    public SmsMessageModel(String accountId, String latitude, String longitude, String trigger){
        mAccountId = accountId;
        mLatitude = latitude;
        mLongitude = longitude;
        mTrigger = trigger;
    }

    public String getAccountId() {
        return mAccountId;
    }

    public String getDetectId() {
        return mDetectId;
    }

    public void setDetectId(String detectId) {
        this.mDetectId = detectId;
    }

    public String getLatitude() {
        return mLatitude;
    }

    public String getLongitude() {
        return mLongitude;
    }

    public String getTrigger() {
        return mTrigger;
    }
}
