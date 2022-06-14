package com.pins.infinity.utility

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context

/**
 * Created by Pavlo Melnyk on 07.03.2019.
 */
class DeviceUtils {
    companion object {
        @JvmStatic
        fun isAppDeviceAdministrator(context: Context) : Boolean {
            val name = ComponentName(context, MyAdmin::class.java)
            val deviceManger = context.getSystemService(
                    Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager
            return deviceManger?.isAdminActive(name) ?: false
        }
    }
}

