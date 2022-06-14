package com.pins.infinity.viewModels.applock

import android.app.Application
import android.content.pm.ApplicationInfo
import androidx.databinding.ObservableBoolean
import com.pins.infinity.R
import com.pins.infinity.adapters.RecyclerAdapter
import com.pins.infinity.database.daos.AppLockedDao
import com.pins.infinity.database.models.AppLockItem
import com.pins.infinity.externals.MutableLiveArrayList
import com.pins.infinity.services.applock.retrieveInstalledNonSystemApps
import com.pins.infinity.viewModels.base.BaseRowViewModel
import com.pins.infinity.viewModels.base.BaseViewModel
import com.pins.infinity.viewModels.rows.AppLockRowViewModel

/**
 * Created by Pavlo Melnyk on 25.04.2019.
 */

class AppLockViewModel(application: Application,
                       private val appLockedDao: AppLockedDao)
    : BaseViewModel(application), RecyclerAdapter.OnRowClickListener {

    private val appLockElements = MutableLiveArrayList<AppLockRowViewModel>()
    private var allApps: List<ApplicationInfo>

    val isShowingLocked = ObservableBoolean()

    val adapter = RecyclerAdapter(appLockElements) {
        mapOf(BaseRowViewModel.DEFAULT_ITEM to R.layout.row_app_lock)
    }

    init {
        allApps = retrieveInstalledNonSystemApps(application)
        val currentItems = allApps.map {
            AppLockItem(
                    packageName = it.packageName,
                    title = it.loadLabel(context().packageManager) as String,
                    shouldBeLocked = false,
                    isTemporallyUnlock = false)
        }

        val allSavedItems = appLockedDao.getAllAppLockItems().blockingGet()
        val newItems = currentItems.minus(allSavedItems)

        if (newItems.isNotEmpty()) appLockedDao.insertAppLockItems(newItems)


        adapter.onRowClickListener = this
        showUnlockedCommand()
    }

    override fun onRowClick(model: Any) {
        if (model !is AppLockRowViewModel) {
            return
        }
        model.isChecked.set(!model.isChecked.get())
    }

    fun showUnlockedCommand() {
        isShowingLocked.set(false)
        val filteredApps = filterLockedUnlockedApps(false)
        val elements = filteredApps.map {
            AppLockRowViewModel(
                    title = it.loadLabel(context().packageManager) as String,
                    packageName = it.packageName,
                    iconResource = it.loadIcon(context().packageManager),
                    parentViewModel = this)
        }
        appLockElements.clear()
        appLockElements.addAll(elements)
    }

    fun showLockedCommand() {
        isShowingLocked.set(true)

        val filteredApps = filterLockedUnlockedApps(true)
        val elements = filteredApps.map {
            AppLockRowViewModel(
                    title = it.loadLabel(context().packageManager) as String,
                    packageName = it.packageName,
                    iconResource = it.loadIcon(context().packageManager),
                    parentViewModel = this)
        }
        appLockElements.clear()
        appLockElements.addAll(elements)
    }

    fun lockUnlockCommand() {
        val checkedElements = appLockElements.value?.filter { element -> element.isChecked.get() }

        checkedElements?.forEach { appLockedDao.update(!isShowingLocked.get(), false, it.packageName) }

        if (isShowingLocked.get()) showLockedCommand() else showUnlockedCommand()
    }

    private fun filterLockedUnlockedApps(shouldBeLocked: Boolean): List<ApplicationInfo> {
        val allFilteredItems = appLockedDao.getAppLockFiltered(shouldBeLocked)

        return allApps.filter { app ->
            allFilteredItems.any { item -> item.packageName == app.packageName }
        }
    }
}