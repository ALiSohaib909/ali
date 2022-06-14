package com.pins.infinity.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pins.infinity.database.models.DeviceItem
import io.reactivex.Single

/**
 * Created by Pavlo Melnyk on 05.02.2019.
 */

@Dao
interface DeviceDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDevice(details: DeviceItem)

    @Query("SELECT * FROM DeviceItem")
    fun getDevice(): Single<DeviceItem>

    @Query("SELECT * FROM DeviceItem")
    fun getThisDevice(): DeviceItem?

    @Query("DELETE FROM DeviceItem")
    fun clearDevice()
}