package com.pins.infinity.api.devicemodels

import android.os.Build

/**
 * Created by Pavlo Melnyk on 22.02.2019.
 */
class RemoteLoginRequest(val imei: String) {

    companion object {
        private const val DEVICE: String = "android"
        private const val NA: String = "na"
    }

    val serialNumber: String
        get() = Build.SERIAL

    val device: String
        get() = DEVICE

    val deviceModel: String
        get() = Build.MODEL

    val deviceName: String
        get() = Build.MANUFACTURER

    val deviceCountry: String
        get() = NA

    val deviceCountryCode: String
        get() = NA
}