
package com.pins.infinity.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.telephony.TelephonyManager
import com.pins.infinity.BuildConfig
import com.pins.infinity.database.SettingsManager
import com.pins.infinity.repositories.PaymentRepository
import com.pins.infinity.services.CaptureImageService
import com.pins.infinity.services.LocationService
import com.pins.infinity.services.applock.AppLaunchService
import com.pins.infinity.utility.AppConstants
import com.pins.infinity.utility.AppSharedPrefrence
import com.pins.infinity.utility.SharedPreferences.Const
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by bimalchawla on 29/3/17.
 * Refactored by Pavlo Melnyk 20-03-2018
 */

class BootReceiver : BroadcastReceiver(), KoinComponent {

    private val paymentRepository: PaymentRepository by inject()
    private val settingsManager: SettingsManager by inject()

    override fun onReceive(context: Context, intent: Intent) {
        // Uncomment in case of logging and tracking this service
//        println(TAG_LOCATION+ "--> SIM state changed at boot<--" + AppConstants.IS_SERVICE_RUNNING)

        val editor: SharedPreferences.Editor =
            context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE).edit()
        editor.putBoolean(Const.SHUTDOWN, false)
        editor.apply()
        settingsManager.isShutDown = false

        paymentRepository.isSubscriptionValid()

        val manager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val getSimSerialNumber = manager.simSerialNumber
        val subscriber = manager.subscriberId

        startCaptureImageService(context)

        AppSharedPrefrence.putString(context, AppConstants.SERIAL_NUMBER, getSimSerialNumber)
        AppSharedPrefrence.putString(context, AppConstants.SUBSCRIBE_NUMBER, subscriber)

        startLocationService(context, intent)
        startAppLaunchService(context, intent)
    }

    private fun startCaptureImageService(context: Context) {
        if (!AppConstants.IS_SERVICE_RUNNING) {
            AppConstants.IS_SERVICE_RUNNING = true
            val intentCaptureImage = Intent(context, CaptureImageService::class.java)
            intentCaptureImage.putExtra(Const.THEFT_KEY, false)
            intentCaptureImage.putExtra(Const.FLIGHTMODE_KEY, false)
            // Uncomment in case of logging and tracking this service
//            println(TAG_LOCATION+ " startService CaptureImageService")
            context.startService(intentCaptureImage)
        }
    }

    private fun startLocationService(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_BOOT_COMPLETED, Intent.ACTION_BOOT_COMPLETED, QUICKBOOT_POWERON, Intent.ACTION_REBOOT -> {
                // Uncomment in case of logging and tracking this service
//                println(TAG_LOCATION+ " BOOT_COMPLETED and starting location service")

                val intentLocation = Intent(context, LocationService::class.java)
                context.startService(intentLocation)
            }
        }
    }

    private fun startAppLaunchService(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_BOOT_COMPLETED, Intent.ACTION_BOOT_COMPLETED, QUICKBOOT_POWERON, Intent.ACTION_REBOOT -> {
                // Uncomment in case of logging and tracking this service
//                println(TAG_APP_LAUNCH+ " BOOT_COMPLETED and app launch service")

                context.startService(Intent(context, AppLaunchService::class.java))
            }
        }
    }

    companion object {
        const val TAG_LOCATION = "OnBootLocationService"
        const val TAG_APP_LAUNCH = "OnBootAppLaunchService"
        const val ACTION_BOOT_COMPLETED = "android.intent.action.ACTION_BOOT_COMPLETED"
        const val QUICKBOOT_POWERON = "android.intent.action.QUICKBOOT_POWERON"
    }
}
