package com.pins.infinity.activity.antitheft

import android.os.Bundle
import android.view.WindowManager
import com.pins.infinity.R
import com.pins.infinity.activity.base.BaseActivity
import com.pins.infinity.viewModels.antitheft.LockAction
import com.pins.infinity.viewModels.antitheft.SetupPinViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * Created by Pavlo Melnyk on 2018-12-04.
 */
class SetupPinActivity : BaseActivity<SetupPinViewModel>() {

    override val layout: Int = R.layout.activity_setup_pin

    override val viewModel: SetupPinViewModel by viewModel {
        parametersOf(intent.getSerializableExtra(SetupPinViewModel.ACTION_KEY))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val option = intent.getSerializableExtra(SetupPinViewModel.ACTION_KEY)
        if (option == LockAction.APP_LOCK) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
            viewModel.showInfoDialog(this)
        }

    }
}