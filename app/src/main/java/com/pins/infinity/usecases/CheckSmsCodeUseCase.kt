package com.pins.infinity.usecases

import com.pins.infinity.database.SettingsManager
import com.pins.infinity.model.RecoveryCheck
import com.pins.infinity.repositories.AppLockRepository
import com.pins.infinity.repositories.DeviceRepository
import com.pins.infinity.usecases.CheckSmsCodeUseCase.CheckCodeParam
import io.reactivex.Single

class CheckSmsCodeUseCase(
        private val appLockRepository: AppLockRepository,
        private val settingsManager: SettingsManager,
        private val deviceRepository: DeviceRepository
) : SingleParamUseCase<RecoveryCheck, CheckCodeParam>() {

    override fun buildUseCase(param: CheckCodeParam): Single<RecoveryCheck> {
        val imei = deviceRepository.device?.imei ?: ""
        val token = settingsManager.accessToken
        settingsManager.accessToken = param.token
        return appLockRepository.checkCode(Param(imei = imei, token = param.token, code = param.code))
                .doFinally {
                    settingsManager.accessToken = token
                }
    }

    data class Param(
            val imei: String,
            val token: String,
            val code: String
    )

    data class CheckCodeParam(
            val token: String,
            val code: String
    )
}