package com.pins.infinity.activity.applock

import com.pins.infinity.R
import com.pins.infinity.activity.base.BaseActivity
import com.pins.infinity.viewModels.applock.AppLockViewModel
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Created by Pavlo Melnyk on 22.04.2019.
 */
class AppLockActivity: BaseActivity<AppLockViewModel>() {

    override val viewModel: AppLockViewModel by viewModel()

    override val layout: Int = R.layout.activity_app_lock

}