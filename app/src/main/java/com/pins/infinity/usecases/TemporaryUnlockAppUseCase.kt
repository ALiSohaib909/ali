package com.pins.infinity.usecases

import com.pins.infinity.repositories.AppLockRepository

class TemporaryUnlockAppUseCase(
        private val appLockRepository: AppLockRepository
) : BaseParamUseCase<Unit, String>() {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCase(packageName: String) {
        appLockRepository.updateTemporallyUnlock(packageName = packageName)
    }
}