package com.pins.infinity.usecases

import com.pins.infinity.model.RecoveryInit
import com.pins.infinity.repositories.AppLockRepository
import com.pins.infinity.repositories.DeviceRepository
import io.reactivex.Single

class ForgotPinInitUseCase(
        private val appLockRepository: AppLockRepository,
        private val deviceRepository: DeviceRepository
) : SingleParamUseCase<RecoveryInit, String>() {

    override fun buildUseCase(email: String): Single<RecoveryInit> {
        val imei = deviceRepository.device?.imei ?: ""
        return appLockRepository.forgotPinInit(Param(imei = imei, email = email))
    }

    data class Param(
            val imei: String,
            val email: String
    )
}