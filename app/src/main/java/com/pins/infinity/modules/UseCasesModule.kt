package com.pins.infinity.modules

import com.pins.infinity.usecases.*
import org.koin.dsl.module
import org.koin.experimental.builder.factory

/**
 * Created by Pavlo Melnyk on 19.10.2020.
 */
val useCasesModule = module {
    factory<SetUpPinUseCase>()
    factory<UnlockUseCase>()
    factory<TemporaryUnlockAppUseCase>()
    factory<SaveAppLockPinUseCase>()
    factory<GetAppLockPinUseCase>()
    factory<ForgotPinInitUseCase>()
    factory<CheckSmsCodeUseCase>()
    factory<SetNewPinUseCase>()
    factory<DangerTriggeredUseCase>()
}