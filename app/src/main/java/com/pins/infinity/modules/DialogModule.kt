package com.pins.infinity.modules

import com.pins.infinity.dialogs.DialogService
import com.pins.infinity.dialogs.DialogServiceImpl
import org.koin.dsl.module

/**
 * Created by Pavlo Melnyk on 30.11.2018.
 */
var dialogModule = module {
    single { DialogServiceImpl() as DialogService }
}