package com.pins.infinity.usecases

import com.pins.infinity.repositories.AppLockRepository
import com.pins.infinity.repositories.DeviceRepository
import io.reactivex.Completable

/**
 * Created by Pavlo Melnyk on 12.10.2020.
 */
class UnlockUseCase(
        private val appLockRepository: AppLockRepository,
        private val deviceRepository: DeviceRepository,
        private val unlockAppUseCase: TemporaryUnlockAppUseCase
) : CompletableParamUseCase<UnlockUseCase.Param>() {

    override fun buildUseCase(param: Param): Completable {
        val imei = deviceRepository.device?.imei ?: ""

        return appLockRepository.unlock(param = UnlockParam(
                imei = imei,
                password = param.pin
        )).doOnComplete {
            unlockAppUseCase.execute(param.packageName)
        }
    }

    data class UnlockParam(
            val imei: String,
            val password: String
    )

    data class Param(
            val pin: String,
            val packageName: String
    )
}