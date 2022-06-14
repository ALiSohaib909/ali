package com.pins.infinity.activity.applock

import com.pins.infinity.R
import com.pins.infinity.activity.base.BaseActivity
import com.pins.infinity.viewModels.applock.CHECK_RECOVERY_TOKEN
import com.pins.infinity.viewModels.applock.ForgotPinCheckCodeViewModel
import com.pins.infinity.viewModels.applock.ForgotPinNewPinViewModel
import com.pins.infinity.viewModels.applock.INIT_RECOVERY_TOKEN
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ForgotPinNewPinActivity: BaseActivity<ForgotPinNewPinViewModel>() {

    override val viewModel: ForgotPinNewPinViewModel by viewModel{
        parametersOf(intent.getStringExtra(CHECK_RECOVERY_TOKEN))
    }

    override val layout: Int = R.layout.activity_app_lock_forgot_pin_new_pin
}