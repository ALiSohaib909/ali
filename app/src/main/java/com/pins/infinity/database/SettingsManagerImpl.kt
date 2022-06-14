package com.pins.infinity.database

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.securepreferences.SecurePreferences
import java.util.concurrent.TimeUnit

/**
 * Created by Pavlo Melnyk on 30.11.2018.
 */
class SettingsManagerImpl(
        private val shared: SharedPreferences,
        private val securePreferences: SecurePreferences
) : SettingsManager {

    override var accessToken: String
        get() = shared.getString(KEY_ACCESS_TOKEN, "").orEmpty()
        set(value) { shared.edit().putString(KEY_ACCESS_TOKEN, value).apply() }

    override var deviceToken: String
        get() = shared.getString(KEY_DEVICE_TOKEN, "").orEmpty()
        set(value) { shared.edit().putString(KEY_ACCESS_TOKEN, value).apply() }

    override var sessionState: String
        get() = shared.getString(KEY_SESSION_STATE, "").orEmpty()
        set(value) { shared.edit().putString(KEY_SESSION_STATE, value).apply() }

    override var shouldCheckSubscriptionStatus: Boolean =
            System.currentTimeMillis() > (lastSubscriptionCheck + TimeUnit.SECONDS.toMillis(getCheckDuration()))

    override var lastSubscriptionCheck: Long
        get() = shared.getLong(LAST_SUBSCRIPTION_CHECK, 0L)
        set(value) { shared.edit().putLong(LAST_SUBSCRIPTION_CHECK, value).apply() }

    override var isSubscriptionValid: Boolean
        get() = shared.getBoolean(SUBSCRIPTION_STATUS, false)
        set(value) { shared.edit().putBoolean(SUBSCRIPTION_STATUS, value).apply() }

    override var isIntruderActive: Boolean
        get() = shared.getBoolean(INTRUDER_STATUS, false)
        set(value) { shared.edit().putBoolean(INTRUDER_STATUS, value).apply() }

    override var imei: String
        get() = shared.getString(KEY_IMEI, "").orEmpty()
        set(value) { shared.edit().putString(KEY_IMEI, value).apply() }

    override var userId: String
        get() = shared.getString(KEY_USER_ID, "").orEmpty()
        set(value) { shared.edit().putString(KEY_USER_ID, value).apply() }

    override var userSubscriptionId: String
        get() = shared.getString(KEY_USER_SUBSCRIPTION_ID, "").orEmpty()
        set(value) { shared.edit().putString(KEY_USER_SUBSCRIPTION_ID, value).apply() }


    override var latitude: String
        get() = shared.getString(KEY_LATITUDE, "").orEmpty()
        set(value) { shared.edit().putString(KEY_LATITUDE, value).apply() }


    override var longitude: String
        get() = shared.getString(KEY_LONGITUDE, "").orEmpty()
        set(value) { shared.edit().putString(KEY_LONGITUDE, value).apply() }

    override var payedByCard: Boolean
        get() = shared.getBoolean(KEY_PAYED_BY_CARD, false)
        set(value) { shared.edit().putBoolean(KEY_PAYED_BY_CARD, value).apply() }

    override var isShutDown: Boolean
        get() = shared.getBoolean(KEY_SHUTDOWN, false)
        set(value) { shared.edit().putBoolean(KEY_SHUTDOWN, value).apply() }

    //SMS COMMAND

    override var isAlarm: Boolean
        get() = shared.getBoolean(IS_ALARM, false)
        set(value) { shared.edit().putBoolean(IS_ALARM, value).apply() }

    override var savedVolume: Int
        get() = shared.getInt(SAVED_VOLUME, -1)
        set(value) { shared.edit().putInt(SAVED_VOLUME, value).apply() }


    override var isLocationTrackingActive: Boolean
        get() = shared.getBoolean(KEY_LOCATION_TRACKING, false)
        set(value) { shared.edit().putBoolean(KEY_LOCATION_TRACKING, value).apply() }

    override var isRingPhoneActive: Boolean
        get() = shared.getBoolean(KEY_RING_PHONE, false)
        set(value) { shared.edit().putBoolean(KEY_RING_PHONE, value).apply() }

    override var isRemoteWipeActive: Boolean
        get() = shared.getBoolean(KEY_REMOTE_WIPE, false)
        set(value) { shared.edit().putBoolean(KEY_REMOTE_WIPE, value).apply() }

    //APP LOCK

    override var deviceLockPassword: String
        get() = securePreferences.getString(KEY_DEVICE_LOCK_PASSWORD, "").orEmpty()
        set(value) { securePreferences.edit().putString(KEY_DEVICE_LOCK_PASSWORD, value).apply() }


    override fun clear() {
        accessToken = ""
    }

    private fun getCheckDuration() = if(payedByCard) THIRTY_MINUTES_IN_SECONDS else FIVE_SECONDS

    companion object {
        private const val FIVE_SECONDS = 5L
        private const val THIRTY_MINUTES_IN_SECONDS = 30*60L

        private const val KEY_SESSION_STATE = "key_session_state"
        private const val SUBSCRIPTION_STATUS = "key_session_state"
        private const val INTRUDER_STATUS = "key_intruder_state"
        private const val LAST_SUBSCRIPTION_CHECK = "key_last_subscription_check"
        private const val IS_ALARM = "key_is_alarm"
        private const val SAVED_VOLUME = "key_saved_volume"
        private const val KEY_LOCATION_TRACKING = "key_location_tracking"
        private const val KEY_RING_PHONE = "key_ring_phone"
        private const val KEY_REMOTE_WIPE = "key_remote_wipe"
        private const val KEY_SHUTDOWN = "key_shutdown"


        const val KEY_DEVICE_LOCK_PASSWORD = "key_anti_theft_password"
        const val KEY_ACCESS_TOKEN = "key_access_token"
        const val KEY_DEVICE_TOKEN = "device_token"
        const val KEY_USER_ID = "key_user_id"
        const val KEY_USER_SUBSCRIPTION_ID = "key_user_subscription_id"
        const val KEY_IMEI = "key_imei"
        const val KEY_LATITUDE = "latitude"
        const val KEY_LONGITUDE = "longitude"
        const val KEY_PAYED_BY_CARD = "key_payed_by_card"
    }
}

/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * Allows editing of this preference instance with a call to [apply][SharedPreferences.Editor.apply]
 * or [commit][SharedPreferences.Editor.commit] to persist the changes.
 * Default behaviour is [apply][SharedPreferences.Editor.apply].
 * ```
 * prefs.edit {
 *     putString("key", value)
 * }
 * ```
 * To [commit][SharedPreferences.Editor.commit] changes:
 * ```
 * prefs.edit(commit = true) {
 *     putString("key", value)
 * }
 * ```
 */

@SuppressLint("ApplySharedPref")
inline fun SharedPreferences.edit(
        commit: Boolean = false,
        action: SharedPreferences.Editor.() -> Unit
) {
    val editor = edit()
    action(editor)
    if (commit) {
        editor.commit()
    } else {
        editor.apply()
    }
}