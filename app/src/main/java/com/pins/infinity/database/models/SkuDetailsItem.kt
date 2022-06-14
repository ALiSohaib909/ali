package com.pins.infinity.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Pavlo Melnyk on 07.01.2019.
 */
@Entity()
@Parcelize
data class SkuDetailsItem(
        @PrimaryKey(autoGenerate = true) var id: Int = 0,
        var sku: String,
        var price: String
) : Parcelable

