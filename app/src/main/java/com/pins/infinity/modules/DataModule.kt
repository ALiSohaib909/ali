package com.pins.infinity.modules

import android.content.Context
import com.pins.infinity.BuildConfig
import com.pins.infinity.database.DatabaseManager
import com.pins.infinity.database.createDatabase
import com.securepreferences.SecurePreferences
import org.koin.dsl.module

/**
 * Created by Pavlo Melnyk on 27.11.2018.
 */

val dataModule = module {
    single { sharedPreferences(context = get()) }
    single { getEncryptedSharedPreferences(context = get()) }
    single { createDatabase(context = get()) }
    single { get<DatabaseManager>().userDao() }
    single { get<DatabaseManager>().paymentDao() }
    single { get<DatabaseManager>().billingDao() }
    single { get<DatabaseManager>().deviceDao() }
    single { get<DatabaseManager>().appLockedDao() }
}

private fun sharedPreferences(context: Context) =
        context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)

private fun getEncryptedSharedPreferences(context: Context): SecurePreferences =
        SecurePreferences(context)