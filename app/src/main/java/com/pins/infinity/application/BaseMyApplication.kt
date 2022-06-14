package com.pins.infinity.application

import androidx.multidex.MultiDexApplication
import androidx.appcompat.app.AppCompatDelegate
import com.pins.infinity.api.usermodels.DeviceStatus
import com.pins.infinity.database.daos.DeviceDao
import com.pins.infinity.modules.*
import com.pins.infinity.utility.log
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by Pavlo Melnyk on 27.11.2018.
 */
open class BaseMyApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        addKoin()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    private fun addKoin() {
        val dataModules = listOf(
                viewModelModule,
                networkModule,
                repositoriesModule,
                useCasesModule,
                dataModule,
                dialogModule,
                serviceModule)

        startKoin {
            androidContext(this@BaseMyApplication)
            modules(dataModules)
        }
    }
}