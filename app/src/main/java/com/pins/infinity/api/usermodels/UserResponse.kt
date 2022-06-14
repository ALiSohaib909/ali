package com.pins.infinity.api.usermodels

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
import com.pins.infinity.api.utils.fromApiStringBoolean
import com.pins.infinity.database.models.DeviceItem
import com.pins.infinity.database.models.UserItem

/**
 * Created by Pavlo Melnyk on 2018-12-17.
 */
data class UserResponse(
        val message: String,
        val code: Long,
        val response: Response,
        val error: Boolean
) {
    fun castUser(): UserItem {
        return UserItem(
                userId = response.user.accountID,
                userName = response.user.firstName,
                userLastName = response.user.lastName,
                email = response.user.email,
                activeIntruder = response.user.activityEmail.fromApiStringBoolean(),
                verified = response.user.verified.fromApiStringBoolean(),
                plan = response.user.plan)
    }
}

data class Response(
        val user: User,
        val token: String
)

data class User(

        @SerializedName("alt_phone")
        @JsonProperty("alt_phone")
        val altPhone: String,

        @SerializedName("last_name")
        @JsonProperty("last_name")
        val lastName: String,

        val pin: String,

        @SerializedName("activity_email")
        @JsonProperty("activity_email")
        val activityEmail: String,

        val image: String,

        @SerializedName("plan_type")
        @JsonProperty("plan_type")
        val planType: String,

        @SerializedName("registered_date")
        @JsonProperty("registered_date")
        val registeredDate: String,

        @SerializedName("country_code")
        @JsonProperty("country_code")
        val countryCode: String,

        @SerializedName("first_name")
        @JsonProperty("first_name")
        val firstName: String,

        @SerializedName("affiliate_id")
        @JsonProperty("affiliate_id")
        val affiliateID: Any? = null,

        val verified: String,
        val recovery: String,

        @SerializedName("plan_exp_time")
        @JsonProperty("plan_exp_time")
        val planExpTime: String,

        val state: Any? = null,

        @SerializedName("subscription_id")
        @JsonProperty("subscription_id")
        val subscriptionID: String,

        val email: String,

        @SerializedName("account_type")
        @JsonProperty("account_type")
        val accountType: String,

        @SerializedName("account_id")
        @JsonProperty("account_id")
        val accountID: String,

        @SerializedName("image_id")
        @JsonProperty("image_id")
        val imageID: String,

        val birthday: Any? = null,
        val plan: String,
        val address: Any? = null,
        val imei: String,

        @SerializedName("recovery_plan")
        @JsonProperty("recovery_plan")
        val recoveryPlan: String,

        @SerializedName("enable_email")
        @JsonProperty("enable_email")
        val enableEmail: String,

        val phone: String,
        val country: String,
        val devices: List<DeviceElement>,

        @SerializedName("int_code")
        @JsonProperty("int_code")
        val intCode: String,

        val gender: String
)

data class DeviceElement(
        @SerializedName("device_serial_no")
        @JsonProperty("device_serial_no")
        val deviceSerialNo: String,

        @SerializedName("device_country_code")
        @JsonProperty("device_country_code")
        val deviceCountryCode: String,

        @SerializedName("device_model")
        @JsonProperty("device_model")
        val deviceModel: String,

        @SerializedName("device_latitude")
        @JsonProperty("device_latitude")
        val deviceLatitude: String,

        @SerializedName("device_country")
        @JsonProperty("device_country")
        val deviceCountry: String,

        @SerializedName("device_status")
        @JsonProperty("device_status")
        val deviceStatus: String?,

        @SerializedName("device_name")
        @JsonProperty("device_name")
        val deviceName: String,

        @SerializedName("device_longitude")
        @JsonProperty("device_longitude")
        val deviceLongitude: String,

        val imei: String,
        val device: DeviceEnum
) {
    fun castDevice() = DeviceItem(
            imei = imei,
            serialNo = deviceSerialNo,
            countryCode = deviceCountryCode,
            model = deviceModel,
            latitude = deviceLatitude,
            longitude = deviceLongitude,
            country = deviceCountry,
            deviceStatus = deviceStatus ?: DeviceStatus.Active.value,
            isAppLockActive = false,
            name = deviceName
    )
}

enum class DeviceEnum(val value: String) {
    Android("android");

    companion object {
        public fun fromValue(value: String): DeviceEnum = when (value) {
            "android" -> Android
            else -> throw IllegalArgumentException()
        }
    }
}

enum class DeviceStatus(val value: String) {
    Active("active"),
    Stolen("stolen"),
    Wipe("wipe"),
    Lock("lock"),
    Kill("kill"),
    Ringing("ringing");

    companion object {
        fun fromValue(value: String): DeviceStatus = values().find { it.value == value }
                ?: throw IllegalArgumentException()
    }
}
