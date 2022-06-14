package com.pins.infinity.database

/**
 * Created by Pavlo Melnyk on 30.11.2018.
 */
interface SettingsManager {
    var accessToken: String
    var deviceToken: String
    var sessionState: String
    var shouldCheckSubscriptionStatus: Boolean
    var lastSubscriptionCheck: Long
    var isSubscriptionValid: Boolean
    var isIntruderActive: Boolean
    var imei: String
    var userId: String
    var userSubscriptionId: String
    var latitude: String
    var longitude: String
    var payedByCard: Boolean
    var isShutDown: Boolean

    var isAlarm: Boolean
    var deviceLockPassword: String
    var savedVolume: Int
    var isLocationTrackingActive: Boolean
    var isRingPhoneActive: Boolean
    var isRemoteWipeActive: Boolean

    fun clear()
}