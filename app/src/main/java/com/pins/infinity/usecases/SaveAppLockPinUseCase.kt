package com.pins.infinity.usecases

import com.pins.infinity.database.SettingsManager

class SaveAppLockPinUseCase(
        private val settingsManager: SettingsManager
) : BaseParamUseCase<Unit, String>() {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCase(pin: String) {
        settingsManager.deviceLockPassword = pin
    }
}