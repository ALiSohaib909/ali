package com.pins.infinity.services.applock

import android.app.Application
import android.content.Intent
import com.pins.infinity.activity.applock.AppLockPinActivity
import com.pins.infinity.database.daos.AppLockedDao
import com.pins.infinity.database.models.AppLockItem

/**
 * Created by Pavlo Melnyk on 18.04.2019.
 */

class AppLockManager(
        private val application: Application,
        private val appLockedDao: AppLockedDao) {

    fun checkLaunchedApp(packageName: String, appLaunchService: AppLaunchService) {
        if (isUnlocked(packageName)) {
            lockTemporallyUnlockApp()
        }

        if (isLocked(packageName)) {
            lockTemporallyUnlockApp()
            showAppLockPinView(packageName, appLaunchService)
        }

        if (isUnlocked(packageName)) {
            val allAppInfo = retrieveInstalledNonSystemApps(application)
            val newOne = allAppInfo.first { info -> info.packageName == packageName }

            appLockedDao.insertAppLockItems(listOf(
                    AppLockItem(
                            title = newOne.loadLabel(application.packageManager) as String,
                            packageName = newOne.packageName,
                            shouldBeLocked = false,
                            isTemporallyUnlock = false)
            ))
        }
    }

    private fun isLocked(packageName: String) =
            appLockedDao.getAppLockFiltered(true)
                    .any { app ->
                        app.packageName == packageName && !app.isTemporallyUnlock
                    }

    private fun isUnlocked(packageName: String) =
            appLockedDao.getAppLockFiltered(shouldBeLocked = false)
                    .any { app -> app.packageName == packageName }

    private fun showAppLockPinView(packageName: String, appLaunchService: AppLaunchService) {
        println("$TAG show locked pin view: $packageName")

        val intent = Intent(appLaunchService, AppLockPinActivity::class.java)
        intent.putExtra(PACKAGE_LOCK, packageName)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK

        appLaunchService.startActivity(intent)
    }

    private fun lockTemporallyUnlockApp() {
        appLockedDao.getAppLockFiltered(true)
                .filter { app ->
                    app.isTemporallyUnlock
                }.forEach { temporallyUnlockItem ->
                    appLockedDao.update(
                            shouldBeLocked = true,
                            isTemporallyUnlock = false,
                            packageName = temporallyUnlockItem.packageName)
                    println("$TAG lockTemporallyUnlockApp: ${temporallyUnlockItem.packageName}")
                }
    }

    companion object {
        const val TAG = "APP_LOCKER_MANAGER"
        const val PACKAGE_LOCK = "APP_LOCKER_PACKAGE"
    }
}