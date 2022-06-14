package com.pins.infinity.usecases

import com.pins.infinity.database.SettingsManager
import com.pins.infinity.repositories.AppLockRepository
import com.pins.infinity.repositories.DeviceRepository
import com.pins.infinity.usecases.SetNewPinUseCase.SetNewPinParam
import io.reactivex.Completable

class SetNewPinUseCase(
        private val appLockRepository: AppLockRepository,
        private val settingsManager: SettingsManager,
        private val deviceRepository: DeviceRepository
) : CompletableParamUseCase<SetNewPinParam>() {

    override fun buildUseCase(param: SetNewPinParam): Completable {
        val imei = deviceRepository.device?.imei ?: ""
        val token = settingsManager.accessToken
        settingsManager.accessToken = param.token
        return appLockRepository.setUpNewPin(Param(imei = imei, code = param.code))
                .doFinally {
                    settingsManager.accessToken = token
                }
    }

    data class SetNewPinParam(
            val token: String,
            val code: String
    )

    data class Param(
            val imei: String,
            val code: String
    )
}