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
data class UserItem(
        @PrimaryKey(autoGenerate = true) var id: Int = 0,
        @SerializedName("UserId") val userId: String,
        @SerializedName("Name") val userName: String = "",
        @SerializedName("LastName") val userLastName: String = "",
        @SerializedName("Token") val token: String = "",
        @SerializedName("Verified") var verified: Boolean = false,
        @SerializedName("Email") var email: String = "",
        @SerializedName("EnableEmail") var enableEmail: Boolean = false,
        @SerializedName("ActiveIntruder") var activeIntruder: Boolean = false,
        @SerializedName("Plan") var plan: String = "") : Parcelable