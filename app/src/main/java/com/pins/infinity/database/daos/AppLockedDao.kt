package com.pins.infinity.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pins.infinity.database.models.AppLockItem
import io.reactivex.Single

/**
 * Created by Pavlo Melnyk on 24.04.2019.
 */

@Dao
interface AppLockedDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAppLocked(app: AppLockItem)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAppLockItems(app: List<AppLockItem>)

    @Query("SELECT * FROM AppLockItem")
    fun getAppLocked(): Single<AppLockItem>

    @Query("SELECT * FROM AppLockItem")
    fun getAllAppLockItems(): Single<List<AppLockItem>>

    @Query("SELECT * FROM AppLockItem WHERE shouldBeLocked LIKE :shouldBeLocked")
    fun getAppLockFiltered(shouldBeLocked: Boolean): List<AppLockItem>

    @Query("UPDATE AppLockItem SET shouldBeLocked=:shouldBeLocked, isTemporallyUnlock=:isTemporallyUnlock WHERE packageName = :packageName")
    fun update(shouldBeLocked: Boolean, isTemporallyUnlock: Boolean, packageName: String)

    @Query("DELETE FROM AppLockItem")
    fun clearAppLocked()
}