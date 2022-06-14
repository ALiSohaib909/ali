package com.pins.infinity.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import androidx.annotation.NonNull
import kotlinx.android.parcel.Parcelize

/**
 * Created by Pavlo Melnyk on 2018-12-05.
 */

@Entity()
@Parcelize
data class UssdItem(
        @PrimaryKey(autoGenerate = true) var id: Int = 0,
        var code: String
) : Parcelable
