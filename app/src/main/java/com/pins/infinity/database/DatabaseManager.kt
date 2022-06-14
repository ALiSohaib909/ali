package com.pins.infinity.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.pins.infinity.BuildConfig
import com.pins.infinity.database.daos.*
import com.pins.infinity.database.models.*

/**
 * Created by Pavlo Melnyk on 30.11.2018.
 */
@Database(
        entities = [
            UserItem::class,
            PlanItem::class,
            RecoveryItem::class,
            UssdItem::class,
            InitItem::class,
            CompleteItem::class,
            SkuDetailsItem::class,
            DeviceItem::class,
            AppLockItem::class
        ],
        version = 1
)
internal abstract class DatabaseManager : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun paymentDao(): PaymentDao
    abstract fun billingDao(): BillingDao
    abstract fun deviceDao(): DeviceDao
    abstract fun appLockedDao(): AppLockedDao
}

internal fun createDatabase(context: Context) =
        Room.databaseBuilder(context, DatabaseManager::class.java, "room${BuildConfig.APPLICATION_ID}_db")
                .allowMainThreadQueries()
                .build()