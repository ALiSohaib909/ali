package com.pins.infinity.services.remotecontrol

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import com.pins.infinity.database.SettingsManager
import com.pins.infinity.database.daos.DeviceDao
import com.pins.infinity.services.SmsLogsUploadService
import com.pins.infinity.utility.log
import com.pins.infinity.viewModels.antitheft.AntiTheftCommand
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by Pavlo Melnyk on 07.03.2019.
 */

class SmsReceiver : BroadcastReceiver(), KoinComponent {

    private val settingsManager: SettingsManager by inject()
    private val mediaPlayerService: MediaPlayerService by inject()
    private val deviceDao: DeviceDao by inject()


    override fun onReceive(context: Context, intent: Intent) {
        smsCommandAction(context, intent)
        smsLogUpdateAction(context, intent)
    }

    private fun smsCommandAction(context: Context, intent: Intent) {
        log("$SMS_COMMAND $SMS_COMMAND Received sms")
        println("$SMS_COMMAND PINSAPP Received sms")
        val isDeviceLockPasswordSaved = deviceDao.getThisDevice()?.isAppLockActive ?: false

        if (isDeviceLockPasswordSaved) {
            (intent.extras?.get(DATA_UNITS) as? Array<*>)?.forEach { dataUnit ->
                val smsMessage = SmsMessage.createFromPdu(dataUnit as ByteArray)
                log("$SMS_COMMAND Retrieve sms message $smsMessage")
                processSms(context, smsMessage)
            }
        }
    }

    private fun smsLogUpdateAction(context: Context, intent: Intent) {
        println("$SMS_COMMAND PINSAPP Received sms")
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent.action) {
            context.startService(Intent(context, SmsLogsUploadService::class.java))
        }
    }

    private fun processSms(context: Context, smsMessage: SmsMessage) {
        val sender = smsMessage.originatingAddress
        val messageBody = smsMessage.messageBody
        log("$SMS_COMMAND process sms sender $sender body $messageBody")
        val messageParts = messageBody.split(SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (messageParts.size == 2) {
            val commandReceived = getCommand(messageParts)
            val passwordReceived = getPassword(messageParts)
            log("$SMS_COMMAND process sms antiTheftPassword $passwordReceived command $commandReceived")
            log("$SMS_COMMAND process sms saved antiTheftPassword ${settingsManager.deviceLockPassword}")
            if (passwordReceived == settingsManager.deviceLockPassword && commandReceived != AntiTheftCommand.WRONG_COMMAND) {
                this.abortBroadcast()
                executeCommand(commandReceived, context, sender)
            }
        }
    }

    private fun executeCommand(command: AntiTheftCommand, context: Context, sender: String) {
        when (command) {
            AntiTheftCommand.LOCATION_TRACKING -> if (settingsManager.isLocationTrackingActive) locate(sender, settingsManager.latitude, settingsManager.longitude)
            AntiTheftCommand.RING_PHONE -> if (settingsManager.isRingPhoneActive) mediaPlayerService.alarm(context)
            AntiTheftCommand.REMOTE_WIPE -> if (settingsManager.isRemoteWipeActive) wipe(context)
        }
    }

    companion object {
        private const val DATA_UNITS = "pdus"
        private const val SMS_COMMAND = "SMS_COMMAND"
        private const val SEPARATOR = ":"

        private fun getCommand(messageParts: Array<String>): AntiTheftCommand {
            val stringCommand = messageParts[0].trim().toLowerCase()
            return AntiTheftCommand.fromValue(stringCommand)
        }

        private fun getPassword(messageParts: Array<String>) =
                messageParts[1].trim()
    }
}


