package com.pins.infinity.usecases

import com.pins.infinity.database.SettingsManager
import com.pins.infinity.repositories.AppLockRepository
import com.pins.infinity.repositories.DeviceRepository
import io.reactivex.Completable

/**
 * Created by Pavlo Melnyk on 12.10.2020.
 */
class SetUpPinUseCase(
        private val appLockRepository: AppLockRepository,
        private val deviceRepository: DeviceRepository,
        private val settingsManager: SettingsManager
) : CompletableParamUseCase<String>() {

    override fun buildUseCase(pin: String): Completable {
        val accountId = settingsManager.userId
        val imei = deviceRepository.device?.imei ?: ""

        return appLockRepository.setUpPin(param = Param(
                accountId = accountId,
                imei = imei,
                pin = pin
        ))
    }

    data class Param(
            val accountId: String,
            val imei: String,
            val pin: String
    )
}