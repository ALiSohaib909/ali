package com.pins.infinity.api.devicemodels

import com.google.gson.annotations.SerializedName
import com.pins.infinity.api.usermodels.DeviceStatus
import com.pins.infinity.database.models.DeviceItem

/**
 * Created by Pavlo Melnyk on 10.02.2019.
 */

data class DeviceResponse (
        val message: String,
        val code: Long,
        val response: Response,
        val error: Boolean
){
        fun castDevice() = DeviceItem(
                imei = response.device.imei,
                serialNo = response.device.deviceSerialNo,
                countryCode = response.device.deviceCountryCode,
                model = response.device.deviceModel,
                latitude = response.device.deviceLatitude,
                longitude = response.device.deviceLongitude,
                country = response.device.deviceCountry,
                isAppLockActive = (response.device.activateLockPin == "1"),
                deviceStatus = response.device.deviceStatus ?: DeviceStatus.Active.value,
                name = response.device.deviceName
        )
}

data class Response (
        val device: Device
)

data class Device (
        @SerializedName("device_token")
        val deviceToken: String,

        @SerializedName("device_address")
        val deviceAddress: String,

        @SerializedName("device_country_code")
        val deviceCountryCode: String,

        @SerializedName("update_timestamp")
        val updateTimestamp: String,

        @SerializedName("activate_lock_pin")
        val activateLockPin: String,

        val device: String,

        @SerializedName("registered_date")
        val registeredDate: String,

        @SerializedName("device_longitude")
        val deviceLongitude: String,

        @SerializedName("address_source")
        val addressSource: String,

        @SerializedName("update_date")
        val updateDate: String,

        @SerializedName("device_status")
        val deviceStatus: String?,

        @SerializedName("track_id")
        val trackID: String,

        @SerializedName("power_status")
        val powerStatus: String,

        @SerializedName("device_action")
        val deviceAction: String,

        @SerializedName("account_id")
        val accountID: String,

        @SerializedName("device_latitude")
        val deviceLatitude: String,

        @SerializedName("device_country")
        val deviceCountry: String,

        @SerializedName("device_battery")
        val deviceBattery: String,

        val imei: String,

        @SerializedName("device_serial_no")
        val deviceSerialNo: String,

        @SerializedName("device_location")
        val deviceLocation: String,

        @SerializedName("device_model")
        val deviceModel: String,

        @SerializedName("device_name")
        val deviceName: String
)
