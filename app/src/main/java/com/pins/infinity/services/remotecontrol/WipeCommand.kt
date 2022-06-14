package com.pins.infinity.services.remotecontrol

import android.app.admin.DevicePolicyManager
import android.content.Context
import com.ikarussecurity.android.internal.utils.ExternalMounts
import com.pins.infinity.utility.log
import java.io.File

/**
 * Created by Pavlo Melnyk on 09.04.2019.
 */


fun wipe(context: Context) {
    log("wipe")
    println("PINSAPP wipe")
    wipeInternalData(context)
    wipeExternalData(context)
}

private fun wipeInternalData(context: Context) {
    val devicePolicyManager = context.applicationContext.getSystemService(DEVICE_POLICY) as DevicePolicyManager
    devicePolicyManager.wipeData(0)
}

private fun wipeExternalData(context: Context) {
    val directory = ExternalMounts.get(context).iterator()

    while (directory.hasNext()) {
        val baseDirectory = directory.next() as String

        try {
            eraseDirectory(File(baseDirectory))
        } catch (e: Exception) {
            log("Could not wipe directory $baseDirectory $e")
        }
    }
}

private fun eraseDirectory(directory: File) {
    val directoryContents = directory.listFiles()
    directoryContents?.filter { file -> file.isDirectory }?.forEach { eraseDirectory(it) }

    if (!directory.delete()) {
        log("Could not delete directory...")
    }
}

private const val DEVICE_POLICY = "device_policy"
