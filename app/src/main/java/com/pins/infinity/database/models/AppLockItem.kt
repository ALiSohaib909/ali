package com.pins.infinity.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Pavlo Melnyk on 2019-04-24.
 */
@Entity
@Parcelize
data class AppLockItem(
        @PrimaryKey val packageName: String,
        val title: String,
        val shouldBeLocked: Boolean,
        val isTemporallyUnlock: Boolean
) : Parcelable
