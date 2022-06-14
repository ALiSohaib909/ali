package com.pins.infinity.repositories

import com.pins.infinity.model.RecoveryCheck
import com.pins.infinity.model.RecoveryInit
import com.pins.infinity.usecases.*
import io.reactivex.Completable
import io.reactivex.Single

/**
 * Created by Pavlo Melnyk on 12.10.2020.
 */
interface AppLockRepository {
    fun setUpPin(param: SetUpPinUseCase.Param): Completable
    fun unlock(param: UnlockUseCase.UnlockParam): Completable
    fun updateTemporallyUnlock(packageName: String)
    fun forgotPinInit(param: ForgotPinInitUseCase.Param): Single<RecoveryInit>
    fun checkCode(param: CheckSmsCodeUseCase.Param): Single<RecoveryCheck>
    fun setUpNewPin(param: SetNewPinUseCase.Param): Completable
}