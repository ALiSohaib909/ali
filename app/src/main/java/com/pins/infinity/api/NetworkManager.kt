package com.pins.infinity.api

import android.content.Context
import com.pins.infinity.utility.isInternetConnection

interface NetworkManager {
    fun isInternetAvailable(): Boolean
}

class NetworkManagerImpl(private val context: Context) : NetworkManager {
    override fun isInternetAvailable(): Boolean = context.isInternetConnection()
}