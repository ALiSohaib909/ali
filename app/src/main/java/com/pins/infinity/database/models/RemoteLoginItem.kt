package com.pins.infinity.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import androidx.annotation.NonNull
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Pavlo Melnyk on 29.11.2018.
 */

@Entity()
@Parcelize
data class RemoteLoginItem(
        @PrimaryKey(autoGenerate = true) var id: Int = 0,
        var title: String,
        var plan: String,
        var monthPrice: String,
        var yearPrice: String,
        var currency: String = ""
) : Parcelable
