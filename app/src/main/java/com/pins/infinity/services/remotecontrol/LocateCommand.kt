package com.pins.infinity.services.remotecontrol

import android.telephony.SmsManager
import com.pins.infinity.utility.log

/**
 * Created by Pavlo Melnyk on 09.04.2019.
 */

fun locate(sender: String, latitude: String, longitude: String) {

    log("locate")
    println("PINSAPP locate")
    val message = "https://www.google.com/maps/search/?api=1&query=$latitude,+$longitude"

    sendSms(sender, message)
}

private fun sendSms(to: String, message: String) {
    val smsManager = SmsManager.getDefault()
    smsManager.sendTextMessage(to, null, message, null, null)
}
