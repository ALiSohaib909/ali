package com.pins.infinity.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pins.infinity.database.models.UserItem
import io.reactivex.Single

/**
 * Created by Pavlo Melnyk on 29.11.2018.
 */

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(planItems: UserItem): Long


    @Query("SELECT * FROM UserItem")
    fun getUser(): Single<UserItem>

    @Query("DELETE FROM UserItem")
    fun clearUserItem()

}