package com.pins.infinity.services.applock

import android.app.ActivityManager
import android.app.Service
import android.app.usage.UsageStats
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import androidx.annotation.RequiresApi
import com.pins.infinity.utility.ContextUtils.getUsageStatsManager
import org.koin.android.ext.android.inject
import java.util.*

/**
 * Created by Pavlo Melnyk on 19.04.2019.
 */
class AppLaunchService : Service() {

    private val appLockManager: AppLockManager by inject()

    private lateinit var runnable: TimerTask
    private lateinit var handler: Handler
    private lateinit var timer: Timer
    private lateinit var prevActivePackage: String

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        println("$TAG onStartCommand")
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onCreate() {
        println("$TAG onCreate")
        initRunnable()
    }

    private fun getPackageNamesOfActiveApps(context: Context): String {
        return if (Build.VERSION.SDK_INT >= 21) {
            getPackageNameOfTopActivityViaUsageStats(context)
        } else {
            getPackageNamesOfActiveAppsViaRunningTasks(context)
        }
    }

    private fun getPackageNamesOfActiveAppsViaRunningTasks(context: Context): String {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val taskInfo = activityManager.getRunningTasks(1)
        val componentName = (taskInfo[0] as ActivityManager.RunningTaskInfo).topActivity
        println("$TAG ${componentName.packageName}")
        return componentName.packageName
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getPackageNameOfTopActivityViaUsageStats(context: Context): String {
        val usageStatsManager = getUsageStatsManager(context)
        val fiveSecondsAgo = System.currentTimeMillis() - 5000L
        val statsQuery = usageStatsManager.queryUsageStats(0, fiveSecondsAgo, System.currentTimeMillis())

        if (statsQuery.isEmpty()) return ""

        val sort = statsQuery.sortedWith(compareBy<UsageStats> { it.lastTimeUsed })
        println("$TAG ${sort.last().packageName}")

        return sort.last().packageName
    }

    private fun initRunnable() {
        handler = Handler()
        timer = Timer()
        prevActivePackage = ""
        runnable = object : TimerTask() {
            override fun run() {
                val activePackage = getPackageNamesOfActiveApps(this@AppLaunchService)

                if (activePackage == "") {
                    return
                }
                if (!prevActivePackage.contains(activePackage) && prevActivePackage != "") {
                    println("${AppLaunchService.TAG} started locked app: $activePackage")
                    appLockManager.checkLaunchedApp(activePackage, this@AppLaunchService)
                }

                prevActivePackage = activePackage
                appLockManager.checkLaunchedApp(activePackage, this@AppLaunchService)
            }
        }
        timer.schedule(runnable, 0, 1000)
    }

    companion object {
        const val TAG = "APP_LAUNCH_SERVICE"
    }
}