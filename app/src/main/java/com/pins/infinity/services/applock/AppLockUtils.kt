package com.pins.infinity.services.applock

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_MAIN
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.pins.infinity.database.models.AppLockItem
import java.util.*


/**
 * Created by Pavlo Melnyk on 24.04.2019.
 */

fun retrieveInstalledNonSystemApps(context: Context): List<ApplicationInfo> {

    val packageManager = context.applicationContext.packageManager

    val intent = Intent(ACTION_MAIN, null)
    intent.addCategory(Intent.CATEGORY_LAUNCHER)
    val resInfos = packageManager.queryIntentActivities(intent, 0)
    //using hashset so that there will be no duplicate packages,
    //if no duplicate packages then there will be no duplicate apps
    val packageNames = HashSet<String>(0)
    val appInfos = ArrayList<ApplicationInfo>(0)

    //getting package names and adding them to the hashset
    for (resolveInfo in resInfos) {
        packageNames.add(resolveInfo.activityInfo.packageName)
    }

    //now we have unique packages in the hashset, so get their application infos
    //and add them to the arraylist
    for (packageName in packageNames) {
        try {
            appInfos.add(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA))
        } catch (e: PackageManager.NameNotFoundException) {
            //Do Nothing
        }

    }

    //to sort the list of apps by their names
    Collections.sort(appInfos, ApplicationInfo.DisplayNameComparator(packageManager))

    return appInfos
}

private fun isSystemPackage(applicationInfo: ApplicationInfo) =
        (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

private const val KEY_DATA = "/data/"