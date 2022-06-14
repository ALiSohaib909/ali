package com.pins.infinity.activity.applock

import com.pins.infinity.R
import com.pins.infinity.activity.base.BaseActivity
import com.pins.infinity.viewModels.applock.AppLockForgotPinViewModel
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Created by Pavlo Melnyk on 02.05.2019.
 */
class AppLockForgotPinActivity: BaseActivity<AppLockForgotPinViewModel>() {

    override val viewModel: AppLockForgotPinViewModel by viewModel()

    override val layout: Int = R.layout.activity_app_lock_forgot_pin
}