package com.pins.infinity.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import com.pins.infinity.application.MyApplication
import com.pins.infinity.database.SettingsManager
import com.pins.infinity.utility.AppConstants
import com.pins.infinity.utility.log
import org.koin.android.ext.android.inject

/**
 * Created by Pavlo Melnyk on 17.07.2018.
 */
class LocationService : Service() {

    private val settings: SettingsManager by inject()

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                //The last location in the list is the newest
                val location = locationList[locationList.size - 1]

                saveLocationToSharedPreference(location)

                if (settings.isSubscriptionValid) {
                    MyApplication.onAddressUpdate(location)
                } else {
                    log("Did not send Location update because user valid is: ${settings.isSubscriptionValid}")
                }

                Log.i(TAG, "Location Fused:  Lat:${location.latitude} & Lng:${location.longitude}")
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        super.onStartCommand(intent, flags, startId)
        locationRequest?.let { fusedLocationClient?.requestLocationUpdates(it, locationCallback, Looper.myLooper())}

        return Service.START_STICKY
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate")
        initializeLocationManager()
        initializeLocationRequest()
    }

    override fun onDestroy() {
        if (fusedLocationClient != null) {
            fusedLocationClient?.removeLocationUpdates(locationCallback)
            Log.e(TAG, "removedLocationUpdates")
        }
        Log.e(TAG, "onDestroy")
        super.onDestroy()
    }

    private fun initializeLocationManager() {
        Log.d(TAG, "initializeLocationManager")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun initializeLocationRequest() {
        Log.d(TAG, "initializeLocationRequest")

        locationRequest = LocationRequest().apply {
            interval = LOCATION_INTERVAL.toLong()
            fastestInterval = LOCATION_INTERVAL.toLong()
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun saveLocationToSharedPreference(location: Location) {
        settings.latitude = location.latitude.toString()
        settings.longitude = location.longitude.toString()
        Log.d(TAG, "saveLocationToSharedPreference: Lat:${settings.latitude} & Lng:${settings.longitude}")

        AppConstants.newLocation = location
    }

    companion object {
        private const val LOCATION_INTERVAL = 15000
        const val TAG = "CUSTOM_LOCATION_SERVICE"
    }
}
