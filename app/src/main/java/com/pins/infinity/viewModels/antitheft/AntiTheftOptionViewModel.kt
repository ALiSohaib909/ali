package com.pins.infinity.viewModels.antitheft

import android.app.Application
import androidx.databinding.ObservableBoolean
import com.pins.infinity.R
import com.pins.infinity.database.SettingsManager
import com.pins.infinity.viewModels.base.BaseViewModel

/**
 * Created by Pavlo Melnyk on 2019-04-01.
 */
class AntiTheftOptionViewModel(application: Application,
                               private val settingsManager: SettingsManager,
                               val option: AntiTheftCommand
) : BaseViewModel(application) {

    val isActive = ObservableBoolean()
    var title = R.string.antiTheftView_locationTrackingTitle
    var firstDescription = R.string.antiTheftView_locationTrackingTitle
    var secondDescription = R.string.antiTheftView_locationTrackingTitle

    init {
        when (option) {
            AntiTheftCommand.LOCATION_TRACKING -> {
                initView(settingsManager.isLocationTrackingActive,
                        R.string.antiTheftView_locationTrackingTitle,
                        R.string.antiTheftView_locationTrackingFirstDescription,
                        R.string.antiTheftView_locationTrackingSecondDescription)
            }
            AntiTheftCommand.RING_PHONE -> {
                initView(settingsManager.isRingPhoneActive,
                        R.string.antiTheftView_ringMyPhoneTitle,
                        R.string.antiTheftView_ringMyPhoneFirstDescription,
                        R.string.antiTheftView_ringMyPhoneSecondDescription)
            }
            AntiTheftCommand.REMOTE_WIPE -> {
                initView(settingsManager.isRemoteWipeActive,
                        R.string.antiTheftView_remoteWipeTitle,
                        R.string.antiTheftView_remoteWipeFirstDescription,
                        R.string.antiTheftView_remoteWipeSecondDescription)
            }
        }
    }

    private fun saveIsOptionActive() {
        when (option) {
            AntiTheftCommand.LOCATION_TRACKING -> settingsManager.isLocationTrackingActive = isActive.get()
            AntiTheftCommand.RING_PHONE -> settingsManager.isRingPhoneActive = isActive.get()
            AntiTheftCommand.REMOTE_WIPE -> settingsManager.isRemoteWipeActive = isActive.get()
        }
    }

    fun onCheckedChanged(state: Boolean) {
        isActive.set(state)
        saveIsOptionActive()
    }

    private fun initView(active: Boolean, viewTitle: Int, first: Int, second: Int) {
        isActive.set(active)
        title = viewTitle
        firstDescription = first
        secondDescription = second
    }
}