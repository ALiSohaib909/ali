package com.pins.infinity.activity.applock

import com.pins.infinity.R
import com.pins.infinity.activity.base.BaseActivity
import com.pins.infinity.services.applock.AppLockManager
import com.pins.infinity.viewModels.applock.AppLockPinViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * Created by Pavlo Melnyk on 22.04.2019.
 */
class AppLockPinActivity : BaseActivity<AppLockPinViewModel>() {

    override val layout: Int = R.layout.activity_app_lock_pin

    override val viewModel: AppLockPinViewModel by viewModel {
        parametersOf(intent.getStringExtra(AppLockManager.PACKAGE_LOCK))
    }
}