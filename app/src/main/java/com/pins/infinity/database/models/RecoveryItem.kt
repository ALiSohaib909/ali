package com.pins.infinity.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Pavlo Melnyk on 2018-12-05.
 */


@Entity()
@Parcelize
data class RecoveryItem(
        @PrimaryKey(autoGenerate = true) var id: Int = 0,
        var title: String,
        var price: Long
) : Parcelable
