package com.pins.infinity.usecases

import com.pins.infinity.database.SettingsManager

class GetAppLockPinUseCase(
        private val settingsManager: SettingsManager
) : BaseUseCase<String>() {

    override fun buildUseCase(): String = settingsManager.deviceLockPassword
}