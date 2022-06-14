package com.pins.infinity.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pins.infinity.database.models.SkuDetailsItem
import io.reactivex.Single

/**
 * Created by Pavlo Melnyk on 07.01.2019.
 */

@Dao
interface BillingDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSkuDetails(details: List<SkuDetailsItem>)

    @Query("SELECT * FROM SkuDetailsItem")
    fun getSkuDetails(): Single<List<SkuDetailsItem>>

    @Query("DELETE FROM SkuDetailsItem")
    fun clearSkuDetails()
}