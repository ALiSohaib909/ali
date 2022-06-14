package com.pins.infinity.repositories

import com.pins.infinity.api.NetworkManager
import com.pins.infinity.api.exceptions.*
import com.pins.infinity.api.models.recovery.toDomain
import com.pins.infinity.api.services.DeviceApiService
import com.pins.infinity.api.services.RecoveryApiService
import com.pins.infinity.api.utils.makeRequest
import com.pins.infinity.api.utils.onErrorThrow
import com.pins.infinity.database.daos.AppLockedDao
import com.pins.infinity.model.RecoveryCheck
import com.pins.infinity.model.RecoveryInit
import com.pins.infinity.usecases.*
import io.reactivex.Completable
import io.reactivex.Single

/**
 * Created by Pavlo Melnyk on 12.10.2020.
 */
class AppLockRepositoryImpl(
        private val networkManager: NetworkManager,
        private val deviceApiService: DeviceApiService,
        private val recoveryApiService: RecoveryApiService,
        private val appLockedDao: AppLockedDao
) : AppLockRepository {
    override fun setUpPin(param: SetUpPinUseCase.Param): Completable =
            deviceApiService.sendPin(
                    accountId = param.accountId,
                    imei = param.imei,
                    newPin = param.pin)
                    .makeRequest(networkManager)
                    .onErrorThrow { it.toSetUpPinException() }

    override fun unlock(param: UnlockUseCase.UnlockParam): Completable =
            deviceApiService.unlock(
                    password = param.password,
                    imei = param.imei)
                    .makeRequest(networkManager)
                    .onErrorThrow { it.toUnlockAppLockedException() }

    override fun updateTemporallyUnlock(packageName: String) {
        appLockedDao.update(
                shouldBeLocked = true,
                isTemporallyUnlock = true,
                packageName = packageName)
    }

    override fun forgotPinInit(param: ForgotPinInitUseCase.Param): Single<RecoveryInit> =
            recoveryApiService.init(
                    mode = "reset-code",
                    imei = param.imei,
                    identifier = param.email,
                    type = "device")
                    .makeRequest(networkManager)
                    .onErrorThrow { it.toForgotPinInitException() }
                    .map { it.toDomain() }

    override fun checkCode(param: CheckSmsCodeUseCase.Param): Single<RecoveryCheck> =
            recoveryApiService.check(
                    imei = param.imei,
                    response = param.code)
                    .makeRequest(networkManager)
                    .onErrorThrow { it.toForgotPinCheckCodeException() }
                    .map { it.toDomain() }

    override fun setUpNewPin(param: SetNewPinUseCase.Param): Completable =
            recoveryApiService.password(
                    imei = param.imei,
                    password = param.code)
                    .onErrorThrow { it.toSetUpNewPinException() }
                    .makeRequest(networkManager)
}