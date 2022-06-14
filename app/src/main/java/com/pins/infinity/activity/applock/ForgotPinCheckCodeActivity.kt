package com.pins.infinity.activity.applock

import com.pins.infinity.R
import com.pins.infinity.activity.base.BaseActivity
import com.pins.infinity.viewModels.applock.ForgotPinCheckCodeViewModel
import com.pins.infinity.viewModels.applock.INIT_RECOVERY_TOKEN
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ForgotPinCheckCodeActivity: BaseActivity<ForgotPinCheckCodeViewModel>() {

    override val viewModel: ForgotPinCheckCodeViewModel by viewModel{
        parametersOf(intent.getStringExtra(INIT_RECOVERY_TOKEN))
    }

    override val layout: Int = R.layout.activity_app_lock_forgot_pin_check_code
}