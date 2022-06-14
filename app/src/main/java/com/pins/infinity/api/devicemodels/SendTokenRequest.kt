package com.pins.infinity.api.devicemodels

import android.content.Context
import com.pins.infinity.database.SettingsManager
import com.pins.infinity.utility.Utility
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by Pavlo Melnyk on 22.02.2019.
 */
class SendTokenRequest : KoinComponent {

    private val context: Context by inject()
    private val settingsManager: SettingsManager by inject()

    val deviceToken: String
        get() = settingsManager.deviceToken

    val imei: String
        get() = Utility.getImei(context)

}