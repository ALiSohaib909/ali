package com.pins.infinity.services.remotecontrol

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Handler
import android.os.PowerManager
import com.pins.infinity.database.SettingsManager
import com.pins.infinity.utility.log
import java.util.*

/**
 * Created by Pavlo Melnyk on 10.04.2019.
 */
class MediaPlayerService(val settingsManager: SettingsManager) {

    private lateinit var wakeLockAlarm: PowerManager.WakeLock
    private lateinit var mediaPlayer: MediaPlayer

    fun alarm(context: Context) {
        log("alarm")
        println("PINSAPP alarm")
        initAlarm(context)
        startAlarm(context)
    }

    @SuppressLint("InvalidWakeLockTag")
    private fun initAlarm(context: Context) {
        if (!initialized) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLockAlarm = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, PACKAGE)
            log("Alarm context set")
            initialized = true
        }
    }

    private fun startAlarm(context: Context) {
        if (isAlarmOn) {
            return
        }

        wakeLockAlarm.acquire(60 * 1000L /*1 minute*/)
        isAlarmOn = true
        println(TAG + "Starting alarm")

        try {
            val alarmTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, alarmTone)
                prepare()
                start()
                isLooping = true
            }
            settingsManager.isAlarm = true
            println(TAG + "alarm started")

            Timer().scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    if (isAlarmOn) {
                        maximizeVolume(context)
                    }
                }
            }, 0, 1000)

            Handler().postDelayed({ stop(context) }, ALARM_TIME)
        } catch (exception: Exception) {
            log("Starting the alarm failed: $exception")
        }
    }

    private fun stop(context: Context) {
        initAlarm(context)
        if (!settingsManager.isAlarm) {
            return
        }
        try {
            println(TAG + "Stop the alarm ")

            settingsManager.isAlarm = false
            isAlarmOn = false
            resetVolume(context)

            wakeLockAlarm.release()
            mediaPlayer.stop()
            mediaPlayer.release()
            println(TAG + "alarm stopped ")

        } catch (exception: Exception) {
            log("Stopping the alarm failed")
        }
    }

    private fun maximizeVolume(application: Context) {
        println(TAG + "maximizeVolume start")

        (application.getSystemService(Context.AUDIO_SERVICE) as? AudioManager)?.apply {
            setStreamMute(AudioManager.STREAM_MUSIC, false)
            val maxVolume = getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val currentVolume = getStreamVolume(AudioManager.STREAM_MUSIC)

            println(TAG + "savedVolume ${settingsManager.savedVolume}  currentVolume $currentVolume")
            if (settingsManager.savedVolume == -1) {
                settingsManager.savedVolume = currentVolume
                println(TAG + "savedVolume ${settingsManager.savedVolume}")
            }

            adjustStreamVolume(AudioManager.STREAM_MUSIC, 1, 0)
            adjustVolume(1, 0)
            setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0)
            println(TAG + "maximizeVolume finish")
        }
    }

    private fun resetVolume(application: Context) {
        println(TAG + "resetVolume start")
        val audioManager = application.applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        println(TAG + "savedVolume before reset ${settingsManager.savedVolume}")
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, settingsManager.savedVolume, 0)
        settingsManager.savedVolume = -1
        println(TAG + "savedVolume after reset ${settingsManager.savedVolume}")
        println(TAG + "resetVolume finish")
    }

    companion object {
        private const val TAG = "ALARM: "
        private const val PACKAGE = "com.pins.infinity.services.romotecontrol"
        private const val ALARM_TIME = 60*3000L
        private var initialized = false
        private var isAlarmOn = false
    }
}