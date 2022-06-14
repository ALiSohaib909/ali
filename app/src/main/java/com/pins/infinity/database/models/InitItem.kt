package com.pins.infinity.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Pavlo Melnyk on 2018-12-07.
 */
@Entity()
@Parcelize
data class InitItem(
        @PrimaryKey(autoGenerate = true) var id: Int = 0,
        var transId: String,
        var vat: Long = 5L,
        var error: Boolean,
        val totalInUnit: Long,
        val total: Long

        ) : Parcelable
