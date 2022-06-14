package com.pins.infinity.modules

import com.pins.infinity.services.applock.AppLaunchService
import com.pins.infinity.services.applock.AppLockManager
import com.pins.infinity.services.remotecontrol.MediaPlayerService
import org.koin.dsl.module

/**
 * Created by Pavlo Melnyk on 10.04.2019.
 */

val serviceModule = module {
    single { MediaPlayerService(get()) }
    single { AppLockManager(get(), get()) }
    single { AppLaunchService() }
}
