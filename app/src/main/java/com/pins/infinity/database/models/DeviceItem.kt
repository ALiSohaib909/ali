package com.pins.infinity.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Pavlo Melnyk on 29.11.2018.
 */

@Entity()
@Parcelize
data class DeviceItem(
        @PrimaryKey(autoGenerate = true) var id: Int = 0,
        @SerializedName("Imei") var imei: String,
        val isAppLockActive: Boolean,
        @SerializedName("SerialNo") val serialNo: String,
        @SerializedName("CountryCode") val countryCode: String,
        @SerializedName("Model") val model: String = "",
        @SerializedName("Latitude") val latitude: String = "",
        @SerializedName("Longitude") var longitude: String = "",
        @SerializedName("Country") var country: String = "",
        @SerializedName("DeviceStatus") var deviceStatus: String = "",
        @SerializedName("Name") var name: String = "") : Parcelable


