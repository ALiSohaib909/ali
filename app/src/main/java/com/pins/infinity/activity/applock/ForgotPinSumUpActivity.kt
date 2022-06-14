package com.pins.infinity.activity.applock

import com.pins.infinity.R
import com.pins.infinity.activity.base.BaseActivity
import com.pins.infinity.viewModels.applock.ForgotPinCheckCodeViewModel
import com.pins.infinity.viewModels.applock.ForgotPinNewPinViewModel
import com.pins.infinity.viewModels.applock.ForgotPinSumUpViewModel
import com.pins.infinity.viewModels.applock.INIT_RECOVERY_TOKEN
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ForgotPinSumUpActivity: BaseActivity<ForgotPinSumUpViewModel>() {

    override val viewModel: ForgotPinSumUpViewModel by viewModel()

    override val layout: Int = R.layout.activity_app_lock_forgot_pin_sum_up
}