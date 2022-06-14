package com.pins.infinity.modules

import com.pins.infinity.database.SettingsManager
import com.pins.infinity.database.SettingsManagerImpl
import com.pins.infinity.repositories.*
import org.koin.dsl.module
import org.koin.experimental.builder.singleBy

/**
 * Created by Pavlo Melnyk on 12.10.2020.
 */
val repositoriesModule = module {
    singleBy<PaymentRepository, PaymentRepositoryImpl>()
    singleBy<UserRepository, UserRepositoryImpl>()
    singleBy<SettingsManager, SettingsManagerImpl>()
    singleBy<AuthoriseRepository, AuthoriseRepositoryImpl>()
    singleBy<AppLockRepository, AppLockRepositoryImpl>()
    singleBy<DeviceRepository, DeviceRepositoryImpl>()
}
