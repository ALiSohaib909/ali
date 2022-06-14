package com.pins.infinity.services.simdetect

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import com.dexprotector.annotations.ClassEncryption

/**
 * Created by Pavlo Melnyk on 06.04.2021.
 */

@ClassEncryption
class SimDetectAccessibilityService : AccessibilityService() {
    override fun onCreate() {
        super.onCreate()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {}

    override fun onInterrupt() {}

    companion object {
        const val TAG = "SimDetectAccessiService"

        @JvmStatic
        fun isAccessibilityEnabled(context: Context): Boolean {
            val serviceId = context.packageName + "/" + SimDetectAccessibilityService::class.java.canonicalName
            if (Build.VERSION.SDK_INT >= 23) {
                val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
                val accessibilityServices = accessibilityManager.getEnabledAccessibilityServiceList(-1)
                val iterator: Iterator<*> = accessibilityServices.iterator()
                while (iterator.hasNext()) {
                    val service = iterator.next() as AccessibilityServiceInfo
                    if (serviceId == service.id) {
                        return true
                    }
                }
            }
            return false
        }
    }
}